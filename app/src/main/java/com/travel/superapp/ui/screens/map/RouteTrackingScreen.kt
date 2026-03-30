package com.travel.superapp.ui.screens.map

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import coil3.compose.AsyncImage
import com.travel.superapp.ui.screens.map.formatDistance
import com.travel.superapp.ui.screens.map.formatDuration
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

/** 路径跟踪录制状态 */
enum class TrackingState {
    Idle,       // 未开始
    Recording,  // 录制中
    Paused,     // 暂停
    Finished,   // 已停止
}

/** 路径跟踪主页面 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteTrackingScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Configuration.getInstance().userAgentValue = context.packageName

    val repository = remember { RouteRepository.getInstance(context) }
    val allTracks by repository.tracks.collectAsState()

    // ─── 录制状态 ────────────────────────────────────────────────────
    var trackingState by remember { mutableStateOf(TrackingState.Idle) }
    var trackPoints by remember { mutableStateOf<List<TrackPoint>>(emptyList()) }
    var startTime by remember { mutableLongStateOf(0L) }
    var pausedDuration by remember { mutableLongStateOf(0L) }
    var pauseStartTime by remember { mutableLongStateOf(0L) }
    var currentDistance by remember { mutableStateOf(0.0) }

    // ─── 权限 & GPS ────────────────────────────────────────────────
    var hasLocationPermission by remember { mutableStateOf(false) }
    var currentLocation by remember { mutableStateOf<TrackPoint?>(null) }
    var mapView by remember { mutableStateOf<MapView?>(null) }
    var routePolyline by remember { mutableStateOf<Polyline?>(null) }
    var myLocationOverlay by remember { mutableStateOf<MyLocationNewOverlay?>(null) }

    // ─── 对话框状态 ──────────────────────────────────────────────────
    var showSaveDialog by remember { mutableStateOf(false) }
    var showShareSheet by remember { mutableStateOf(false) }
    var savedTrackName by remember { mutableStateOf("") }
    var savedTrackTags by remember { mutableStateOf<List<String>>(emptyList()) }
    var tagInput by remember { mutableStateOf("") }

    var timerJob by remember { mutableStateOf<Job?>(null) }
    var elapsedTime by remember { mutableLongStateOf(0L) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { perms ->
        hasLocationPermission = perms[Manifest.permission.ACCESS_FINE_LOCATION] == true
    }

    LaunchedEffect(Unit) {
        hasLocationPermission = context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) ==
                android.content.pm.PackageManager.PERMISSION_GRANTED
        if (!hasLocationPermission) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.POST_NOTIFICATIONS,
                ),
            )
        }
    }

    // ─── 定时器 ────────────────────────────────────────────────────
    fun startTimer() {
        timerJob?.cancel()
        timerJob = scope.launch {
            while (isActive) {
                delay(1000)
                elapsedTime = System.currentTimeMillis() - startTime - pausedDuration
            }
        }
    }

    fun stopTimer() {
        timerJob?.cancel()
    }

    // ─── 路径点 → GeoPoint 列表 ───────────────────────────────────
    fun Polyline.updateRoute(pts: List<TrackPoint>) {
        setPoints(pts.map { GeoPoint(it.lat, it.lon) })
    }

    Box(modifier = modifier.fillMaxSize()) {
        // ─── 地图 ────────────────────────────────────────────────
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                MapView(ctx).also { mv ->
                    mv.setTileSource(TileSourceFactory.MAPNIK)
                    mv.setMultiTouchControls(true)
                    mv.controller.setZoom(16.0)
                    mv.controller.setCenter(GeoPoint(30.25, 120.15))
                    mapView = mv

                    // 当前位置图层
                    if (hasLocationPermission) {
                        val locOverlay = MyLocationNewOverlay(GpsMyLocationProvider(ctx), mv).also {
                            it.enableMyLocation()
                            mv.overlays.add(it)
                        }
                        myLocationOverlay = locOverlay
                    }
                }
            },
            update = { mv ->
                mv.overlays.removeAll { it is Polyline && it.id == "route_polyline" }
                if (trackPoints.isNotEmpty()) {
                    val poly = Polyline().apply {
                        id = "route_polyline"
                        outlinePaint.apply {
                            color = Color.parseColor("#2196F3")
                            strokeWidth = 8f
                            style = Paint.Style.STROKE
                            strokeCap = Paint.Cap.ROUND
                            strokeJoin = Paint.Join.ROUND
                            alpha = 220
                            isAntiAlias = true
                        }
                        setPoints(trackPoints.map { GeoPoint(it.lat, it.lon) })
                    }
                    mv.overlays.add(0, poly)
                    routePolyline = poly

                    // 自动移动地图到路径起点
                    if (trackPoints.size == 1) {
                        mv.controller.animateTo(GeoPoint(trackPoints[0].lat, trackPoints[0].lon))
                    }
                }
                mv.invalidate()
            },
        )

        // ─── 顶部栏 ────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp)
                .align(Alignment.TopCenter),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                shape = RoundedCornerShape(50),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                shadowElevation = 4.dp,
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.size(32.dp),
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "路径跟踪",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    )
                }
            }
        }

        // ─── 实时数据面板 ────────────────────────────────────────
        AnimatedVisibility(
            visible = trackingState != TrackingState.Idle,
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut() + slideOutVertically(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 72.dp),
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
                shadowElevation = 6.dp,
                modifier = Modifier.padding(horizontal = 16.dp),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                ) {
                    StatItem(label = "里程", value = formatDistance(currentDistance))
                    StatItem(label = "时长", value = formatDuration(elapsedTime))
                    val pace = if (currentDistance > 0) {
                        val secPerKm = (elapsedTime / 1000.0) / (currentDistance / 1000.0)
                        val min = (secPerKm / 60).toInt()
                        val sec = (secPerKm % 60).toInt()
                        "%d'%02d\"/公里".format(min, sec)
                    } else "--'--\"/公里"
                    StatItem(label = "配速", value = pace)
                }
            }
        }

        // ─── 底部控制面板 ───────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(16.dp)
                .shadow(8.dp, RoundedCornerShape(24.dp))
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.surface),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            when (trackingState) {
                TrackingState.Idle -> {
                    // 空闲状态：开始按钮
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        FilledIconButton(
                            onClick = {
                                if (!hasLocationPermission) {
                                    permissionLauncher.launch(
                                        arrayOf(
                                            Manifest.permission.ACCESS_FINE_LOCATION,
                                            Manifest.permission.ACCESS_COARSE_LOCATION,
                                        ),
                                    )
                                    return@FilledIconButton
                                }
                                trackPoints = emptyList()
                                currentDistance = 0.0
                                startTime = System.currentTimeMillis()
                                pausedDuration = 0L
                                elapsedTime = 0L
                                trackingState = TrackingState.Recording
                                startTimer()
                            },
                            modifier = Modifier.size(64.dp),
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                            ),
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = "开始", modifier = Modifier.size(36.dp))
                        }
                        Spacer(Modifier.width(20.dp))
                        // 历史路径入口
                        TextButton(onClick = { showShareSheet = true }) {
                            Icon(Icons.Default.Bookmark, contentDescription = null)
                            Spacer(Modifier.width(4.dp))
                            Text("我的路径")
                        }
                    }
                }

                TrackingState.Recording, TrackingState.Paused -> {
                    // 录制/暂停：停止 + 暂停/继续
                    val isRecording = trackingState == TrackingState.Recording
                    val actionColor by animateColorAsState(
                        targetValue = if (isRecording) MaterialTheme.colorScheme.error
                                       else MaterialTheme.colorScheme.primary,
                        label = "actionColor",
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        // 停止
                        FilledIconButton(
                            onClick = {
                                stopTimer()
                                trackingState = TrackingState.Finished
                                showSaveDialog = true
                            },
                            modifier = Modifier.size(52.dp),
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer,
                            ),
                        ) {
                            Icon(Icons.Default.Stop, contentDescription = "停止", tint = MaterialTheme.colorScheme.error)
                        }

                        // 暂停/继续
                        FilledIconButton(
                            onClick = {
                                if (trackingState == TrackingState.Recording) {
                                    pauseStartTime = System.currentTimeMillis()
                                    trackingState = TrackingState.Paused
                                    stopTimer()
                                } else {
                                    pausedDuration += System.currentTimeMillis() - pauseStartTime
                                    trackingState = TrackingState.Recording
                                    startTimer()
                                }
                            },
                            modifier = Modifier.size(64.dp),
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = actionColor,
                            ),
                        ) {
                            Icon(
                                if (isRecording) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isRecording) "暂停" else "继续",
                                modifier = Modifier.size(36.dp),
                            )
                        }

                        // 路径点数
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${trackPoints.size}",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            )
                            Text("记录点", style = MaterialTheme.typography.labelSmall)
                        }
                    }

                    Text(
                        text = if (isRecording) "GPS 跟踪中..." else "已暂停",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 12.dp),
                    )
                }

                TrackingState.Finished -> {
                    // 已停止：保存/分享/发布
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        ActionButton(
                            icon = Icons.Default.Bookmark,
                            label = "保存",
                            onClick = { showSaveDialog = true },
                        )
                        ActionButton(
                            icon = Icons.AutoMirrored.Filled.Send,
                            label = "转发",
                            onClick = { shareTrack(context, trackPoints, currentDistance, elapsedTime) },
                        )
                        ActionButton(
                            icon = Icons.Default.Public,
                            label = "发布",
                            onClick = {
                                savedTrackName = "路径 ${System.currentTimeMillis() % 100000}"
                                showSaveDialog = true
                            },
                            highlight = true,
                        )
                    }
                }
            }
        }
    }

    // ─── GPS 位置收集 ──────────────────────────────────────────────
    if (hasLocationPermission && trackingState == TrackingState.Recording) {
        val tracker = remember { LocationTracker.getInstance(context) }
        LaunchedEffect(tracker) {
            tracker.locationUpdates().collect { pt ->
                if (trackPoints.isNotEmpty()) {
                    val last = trackPoints.last()
                    currentDistance += haversine(last.lat, last.lon, pt.lat, pt.lon)
                }
                trackPoints = trackPoints + pt
                currentLocation = pt
            }
        }
    }

    // ─── 保存对话框 ────────────────────────────────────────────────
    if (showSaveDialog) {
        SaveTrackDialog(
            initialName = savedTrackName,
            initialTags = savedTrackTags,
            onConfirm = { name, tags ->
                val track = RouteTrack(
                    name = name.ifBlank { "未命名路径" },
                    points = trackPoints,
                    totalDistanceMeters = currentDistance,
                    totalDurationMs = elapsedTime,
                    tags = tags,
                    authorName = "游客",
                )
                repository.saveTrack(track)
                trackingState = TrackingState.Idle
                trackPoints = emptyList()
                showSaveDialog = false
                savedTrackName = ""
                savedTrackTags = emptyList()
            },
            onDismiss = {
                showSaveDialog = false
                if (trackingState == TrackingState.Finished) {
                    trackingState = TrackingState.Idle
                }
            },
        )
    }

    // ─── 历史路径分享面板 ──────────────────────────────────────────
    if (showShareSheet) {
        TrackHistorySheet(
            tracks = allTracks,
            onSelect = { track ->
                trackPoints = track.points
                currentDistance = track.totalDistanceMeters
                elapsedTime = track.totalDurationMs
                showShareSheet = false
                trackingState = TrackingState.Finished
            },
            onShare = { track ->
                shareTrack(context, track.points, track.totalDistanceMeters, track.totalDurationMs)
            },
            onPublish = { track ->
                scope.launch {
                    repository.publishTrack(track.id)
                }
            },
            onDismiss = { showShareSheet = false },
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            stopTimer()
            mapView?.onDetach()
        }
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun ActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    highlight: Boolean = false,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(8.dp),
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(
                    if (highlight) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.surfaceVariant,
                    CircleShape,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = label)
        }
        Spacer(Modifier.height(4.dp))
        Text(label, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
private fun SaveTrackDialog(
    initialName: String,
    initialTags: List<String>,
    onConfirm: (String, List<String>) -> Unit,
    onDismiss: () -> Unit,
) {
    var name by remember { mutableStateOf(initialName) }
    var tagInput by remember { mutableStateOf("") }
    var tags by remember { mutableStateOf(initialTags.toMutableList()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("保存路径", fontWeight = FontWeight.SemiBold) },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("路径名称") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = tagInput,
                    onValueChange = { tagInput = it },
                    label = { Text("标签（回车添加）") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    trailingIcon = {
                        if (tagInput.isNotBlank()) {
                            IconButton(onClick = {
                                if (tagInput.isNotBlank() && !tags.contains(tagInput)) {
                                    tags.add(tagInput.trim())
                                }
                                tagInput = ""
                            }) {
                                Icon(Icons.Default.Check, contentDescription = "添加")
                            }
                        }
                    },
                )
                if (tags.isNotEmpty()) {
                    Spacer(Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(tags) { tag ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.primaryContainer,
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(tag, style = MaterialTheme.typography.labelSmall)
                                    Spacer(Modifier.width(4.dp))
                                    Icon(Icons.Default.Close, contentDescription = "删除",
                                        modifier = Modifier.size(12.dp).clickable {
                                            tags.remove(tag)
                                        })
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(name, tags) }) {
                Text("保存")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TrackHistorySheet(
    tracks: List<RouteTrack>,
    onSelect: (RouteTrack) -> Unit,
    onShare: (RouteTrack) -> Unit,
    onPublish: (RouteTrack) -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp),
        ) {
            Text(
                text = "我的路径",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
            )
            Spacer(Modifier.height(16.dp))

            if (tracks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "暂无保存的路径\n去录制一条吧！",
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            } else {
                tracks.take(10).forEach { track ->
                    TrackHistoryItem(
                        track = track,
                        onSelect = { onSelect(track) },
                        onShare = { onShare(track) },
                        onPublish = { onPublish(track) },
                    )
                    Spacer(Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
private fun TrackHistoryItem(
    track: RouteTrack,
    onSelect: () -> Unit,
    onShare: () -> Unit,
    onPublish: () -> Unit,
) {
    Card(
        onClick = onSelect,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = track.name.ifBlank { "未命名路径" },
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    modifier = Modifier.weight(1f),
                )
                if (track.isPublished) {
                    Surface(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.primaryContainer) {
                        Text("已发布", style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                }
            }
            Spacer(Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(formatDistance(track.totalDistanceMeters), style = MaterialTheme.typography.bodySmall)
                Text(formatDuration(track.totalDurationMs), style = MaterialTheme.typography.bodySmall)
                Text("${track.points.size} 个点", style = MaterialTheme.typography.bodySmall)
            }
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextButton(onClick = onShare, contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("转发", style = MaterialTheme.typography.labelSmall)
                }
                if (!track.isPublished) {
                    TextButton(onClick = onPublish, contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)) {
                        Icon(Icons.Default.Public, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("发布", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}

/** 转发路径（生成文本摘要分享） */
private fun shareTrack(ctx: Context, points: List<TrackPoint>, distance: Double, duration: Long) {
    val summary = buildString {
        appendLine("📍 我的行走轨迹")
        appendLine("距离：${formatDistance(distance)}")
        appendLine("时长：${formatDuration(duration)}")
        appendLine("记录点：${points.size} 个")
        if (points.isNotEmpty()) {
            appendLine("起点：(%.4f, %.4f)".format(points.first().lat, points.first().lon))
            appendLine("终点：(%.4f, %.4f)".format(points.last().lat, points.last().lon))
        }
        appendLine()
        appendLine("—— 来自 TravelSuperApp")
    }

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, summary)
    }
    ctx.startActivity(Intent.createChooser(intent, "分享路径"))
}
