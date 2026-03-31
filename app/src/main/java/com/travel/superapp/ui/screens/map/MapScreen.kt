package com.travel.superapp.ui.screens.map

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import coil3.compose.AsyncImage
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

private val CategoryColors = mapOf(
    MarkCategory.Food to Color(0xFFE57373),
    MarkCategory.Restaurant to Color(0xFFFFB74D),
    MarkCategory.Landmark to Color(0xFF64B5F6),
    MarkCategory.ScenicSpot to Color(0xFF81C784),
    MarkCategory.Shopping to Color(0xFFBA68C8),
    MarkCategory.Custom to Color(0xFF90A4AE),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Configuration.getInstance().userAgentValue = context.packageName

    val repository = remember { MapMarkRepository.getInstance(context) }
    val allMarks by repository.marks.collectAsState()

    var selectedCategory by remember { mutableStateOf<MarkCategory?>(null) }
    val visibleMarks = remember(allMarks, selectedCategory) {
        if (selectedCategory == null) allMarks else allMarks.filter { it.category == selectedCategory }
    }

    var pendingMark by remember { mutableStateOf<MapMark?>(null) }
    var sheetMark by remember { mutableStateOf<MapMark?>(null) }
    var showAddSheet by remember { mutableStateOf(false) }
    var pendingLat by remember { mutableStateOf(0.0) }
    var pendingLon by remember { mutableStateOf(0.0) }

    val sheetState = rememberStandardBottomSheetState(skipHiddenState = false)
    val scaffoldState = rememberBottomSheetScaffoldState(bottomSheetState = sheetState)

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { }

    BottomSheetScaffold(
        modifier = modifier,
        scaffoldState = scaffoldState,
        sheetPeekHeight = 0.dp,
        sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        sheetContent = {
            sheetMark?.let { mark ->
                MarkDetailSheet(
                    mark = mark,
                    onClose = { sheetMark = null },
                    onDelete = {
                        repository.deleteMark(mark.id)
                        sheetMark = null
                    },
                )
            } ?: run {
                AddMarkSheet(
                    lat = pendingLat,
                    lon = pendingLon,
                    onConfirm = { title, note, category, photos ->
                        val newMark = MapMark(
                            lat = pendingLat,
                            lon = pendingLon,
                            category = category,
                            title = title,
                            note = note,
                            photos = photos,
                            authorName = "游客",
                        )
                        repository.addMark(newMark)
                        showAddSheet = false
                    },
                    onCancel = { showAddSheet = false },
                )
            }
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            OsmMapView(
                modifier = Modifier.fillMaxSize(),
                marks = visibleMarks,
                onMapClick = { lat, lon ->
                    pendingLat = lat
                    pendingLon = lon
                    showAddSheet = true
                    sheetMark = null
                },
                onMarkerClick = { mark ->
                    sheetMark = mark
                    showAddSheet = false
                },
            )

            // 分类筛选
            Column(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(12.dp),
            ) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    item {
                        FilterChip(
                            selected = selectedCategory == null,
                            onClick = { selectedCategory = null },
                            label = { Text("全部") },
                        )
                    }
                    items(MarkCategory.entries.toList()) { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = if (selectedCategory == cat) null else cat },
                            label = { Text("${cat.emoji} ${cat.label}") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CategoryColors[cat]?.copy(alpha = 0.3f)
                                    ?: MaterialTheme.colorScheme.primaryContainer,
                            ),
                        )
                    }
                }
            }

            // 定位 & 添加按钮
            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                SmallFloatingActionButton(
                    onClick = {
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED
                        ) {
                            // TODO: 调用系统定位或 fusedLocationProvider 获取当前坐标
                        } else {
                            locationPermissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION,
                                ),
                            )
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                ) {
                    Icon(Icons.Default.MyLocation, contentDescription = "定位")
                }

                FloatingActionButton(
                    onClick = {
                        scope.launch {
                            // 添加到地图中心点
                            // pendingLat = ...
                            // pendingLon = ...
                            showAddSheet = true
                            sheetMark = null
                        }
                    },
                ) {
                    Icon(Icons.Default.Add, contentDescription = "添加标记")
                }
            }
        }
    }
}

@Composable
private fun OsmMapView(
    modifier: Modifier = Modifier,
    marks: List<MapMark>,
    onMapClick: (Double, Double) -> Unit,
    onMarkerClick: (MapMark) -> Unit,
) {
    val context = LocalContext.current

    DisposableEffect(Unit) {
        onDispose { }
    }

    AndroidView(
        modifier = modifier,
        update = { mv ->
            mv.overlays.clear()
            marks.forEach { mark ->
                val marker = Marker(mv).apply {
                    position = GeoPoint(mark.lat, mark.lon)
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    title = "${mark.category.emoji} ${mark.title}"
                    snippet = mark.note.ifEmpty { null }
                    setOnMarkerClickListener { _, _ ->
                        onMarkerClick(mark)
                        true
                    }
                }
                mv.overlays.add(marker)
            }

            mv.setOnTouchListener { _, event ->
                if (event.action == android.view.MotionEvent.ACTION_UP) {
                    val projection = mv.projection
                    val geoPoint = projection.fromPixels(event.x.toInt(), event.y.toInt()) as GeoPoint
                    onMapClick(geoPoint.latitude, geoPoint.longitude)
                }
                false
            }

            mv.invalidate()
        },
        factory = { ctx ->
            MapView(ctx).apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)
                controller.setZoom(5.0)
                controller.setCenter(GeoPoint(35.0, 105.0))
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddMarkSheet(
    lat: Double,
    lon: Double,
    onConfirm: (title: String, note: String, category: MarkCategory, photos: List<String>) -> Unit,
    onCancel: () -> Unit,
) {
    var title by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(MarkCategory.Custom) }
    var photos by remember { mutableStateOf<List<String>>(emptyList()) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "📍 添加标记",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            )
            IconButton(onClick = onCancel) {
                Icon(Icons.Default.Close, contentDescription = "关闭")
            }
        }

        Spacer(Modifier.height(4.dp))
        Text(
            text = "坐标：%.4f, %.4f".format(lat, lon),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(Modifier.height(16.dp))

        Text("分类", style = MaterialTheme.typography.labelLarge)
        Spacer(Modifier.height(8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(MarkCategory.entries.toList()) { cat ->
                FilterChip(
                    selected = category == cat,
                    onClick = { category = cat },
                    label = { Text("${cat.emoji} ${cat.label}") },
                )
            }
        }

        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("标题 *") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )

        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = note,
            onValueChange = { note = it },
            label = { Text("备注（可选）") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2,
            maxLines = 4,
        )

        Spacer(Modifier.height(12.dp))

        // 图片添加占位（TODO: 接实际图片选择器）
        Text("图片（TODO: 接图片选择器）", style = MaterialTheme.typography.labelLarge)
        Spacer(Modifier.height(8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(photos) { photoUrl ->
                AsyncImage(
                    model = photoUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp)),
                )
            }
            item {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { /* TODO: 打开图片选择器 */ },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Default.Add, contentDescription = "添加图片")
                }
            }
        }

        Spacer(Modifier.height(20.dp))
        Button(
            onClick = {
                if (title.isNotBlank()) {
                    onConfirm(title, note, category, photos)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = title.isNotBlank(),
        ) {
            Text("确认添加")
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun MarkDetailSheet(
    mark: MapMark,
    onClose: () -> Unit,
    onDelete: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = mark.category.emoji,
                    style = MaterialTheme.typography.titleLarge,
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = mark.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                )
            }
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, contentDescription = "关闭")
            }
        }

        Spacer(Modifier.height(4.dp))
        Text(
            text = "坐标：%.4f, %.4f".format(mark.lat, mark.lon),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = "by ${mark.authorName} · ${formatTime(mark.createdAt)}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        if (mark.note.isNotEmpty()) {
            Spacer(Modifier.height(12.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                ),
            ) {
                Text(
                    text = mark.note,
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }

        if (mark.photos.isNotEmpty()) {
            Spacer(Modifier.height(12.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(mark.photos) { photoUrl ->
                    AsyncImage(
                        model = photoUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .size(120.dp)
                            .clip(RoundedCornerShape(8.dp)),
                    )
                }
            }
        }

        Spacer(Modifier.height(20.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
        ) {
            TextButton(onClick = onDelete) {
                Text("删除", color = MaterialTheme.colorScheme.error)
            }
        }

        Spacer(Modifier.height(16.dp))
    }
}

private fun formatTime(timestamp: Long): String {
    val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
    return sdf.format(java.util.Date(timestamp))
}
