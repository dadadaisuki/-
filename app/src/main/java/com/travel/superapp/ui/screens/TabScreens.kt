package com.travel.superapp.ui.screens

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun AiScreen(contentPadding: PaddingValues) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(16.dp),
    ) {
        Text("问AI（前端占位）")
        Text("后续接入 DeepSeek：对话、行程规划、景点问答等。")
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun PostScreen(contentPadding: PaddingValues) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    var modeIndex by remember { mutableStateOf(0) } // 0=文字, 1=图片
    val mode = when (modeIndex) {
        0 -> PostMode.TextOnly
        else -> PostMode.ImagesOnly
    }

    var textContent by remember { mutableStateOf("") }
    var selectedImages by remember { mutableStateOf<List<Uri>>(emptyList()) }

    var showLocation by remember { mutableStateOf(false) }
    var locationText by remember { mutableStateOf("") }

    var tagText by remember { mutableStateOf("") }
    var tags by remember { mutableStateOf<List<String>>(emptyList()) }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents(),
    ) { uris ->
        if (uris.isNullOrEmpty()) return@rememberLauncherForActivityResult
        // 限制最多 9 张，避免 UI 太卡
        val next = uris.take(9).toList()
        selectedImages = next
        coroutineScope.launch {
            snackbarHostState.showSnackbar("已选择 ${next.size} 张图片（前端预览）")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("发布") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
            ) {
                Button(
                    onClick = {
                        val trimmedText = textContent.trim()
                        if (mode == PostMode.TextOnly && trimmedText.isEmpty()) {
                            coroutineScope.launch { snackbarHostState.showSnackbar("请输入文字内容后再发布") }
                            return@Button
                        }
                        if (mode == PostMode.ImagesOnly && selectedImages.isEmpty()) {
                            coroutineScope.launch { snackbarHostState.showSnackbar("请选择图片后再发布") }
                            return@Button
                        }
                        if (showLocation && locationText.trim().isEmpty()) {
                            coroutineScope.launch { snackbarHostState.showSnackbar("开启显示地理位置后，请填写地点") }
                            return@Button
                        }
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("已创建草稿（前端）: ${trimmedText.take(12)}")
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("发布")
                }
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                ModeSelector(
                    modeIndex = modeIndex,
                    onModeChange = { modeIndex = it },
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = if (mode == PostMode.TextOnly) "文字内容" else "图片内容",
                            style = MaterialTheme.typography.titleSmall,
                        )
                        Spacer(Modifier.height(8.dp))

                        if (mode == PostMode.TextOnly) {
                            OutlinedTextField(
                                value = textContent,
                                onValueChange = { textContent = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("写下你的旅行记录...") },
                                minLines = 6,
                                maxLines = 8,
                            )
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                            ) {
                                Button(
                                    onClick = { imagePicker.launch("image/*") },
                                ) {
                                    Icon(Icons.Filled.Photo, contentDescription = "选择图片")
                                    Spacer(Modifier.size(8.dp))
                                    Text("选择图片")
                                }
                                if (selectedImages.isNotEmpty()) {
                                    Button(
                                        onClick = { selectedImages = emptyList() },
                                    ) {
                                        Icon(Icons.Filled.Delete, contentDescription = "清空图片")
                                        Spacer(Modifier.size(8.dp))
                                        Text("清空")
                                    }
                                }
                            }

                            Spacer(Modifier.height(10.dp))
                            if (selectedImages.isNotEmpty()) {
                                LazyVerticalGrid(
                                    columns = GridCells.Fixed(3),
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                    userScrollEnabled = false,
                                ) {
                                    gridItems(selectedImages) { uri ->
                                        ImageTile(
                                            context = context,
                                            uri = uri,
                                            onRemove = { selectedImages = selectedImages.filterNot { it == uri } },
                                        )
                                    }
                                }
                            } else {
                                Text(
                                    text = "暂无图片。选择后将显示在这里。",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                        ) {
                            Text("显示地理位置", style = MaterialTheme.typography.titleSmall)
                            Switch(
                                checked = showLocation,
                                onCheckedChange = { showLocation = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                                ),
                            )
                        }
                        if (showLocation) {
                            Spacer(Modifier.height(10.dp))
                            OutlinedTextField(
                                value = locationText,
                                onValueChange = { locationText = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("例：上海外滩 / 成都宽窄巷子") },
                                singleLine = true,
                                leadingIcon = { Icon(Icons.Filled.LocationOn, contentDescription = null) },
                            )
                        }
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("添加标签（支持回车添加）", style = MaterialTheme.typography.titleSmall)
                        Spacer(Modifier.height(10.dp))

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            items(tags) { tag ->
                                FilterChip(
                                    selected = true,
                                    onClick = { tags = tags.filterNot { it == tag } },
                                    label = {
                                        Text(
                                            text = tag,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                        )
                                    },
                                )
                            }
                        }

                        Spacer(Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                        ) {
                            OutlinedTextField(
                                value = tagText,
                                onValueChange = { tagText = it },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(56.dp),
                                placeholder = { Text("例如：#美食 #拍照 #亲子") },
                                singleLine = true,
                            )
                            Button(
                                onClick = {
                                    val raw = tagText.trim()
                                    if (raw.isEmpty()) return@Button
                                    val normalized = raw.trimStart('#')
                                    val list = normalized.split(Regex("[，,\\s]+")).filter { it.isNotBlank() }
                                    val next = (tags + list).distinct().take(12)
                                    tags = next
                                    tagText = ""
                                },
                            ) {
                                Text("添加")
                            }
                        }
                        if (tags.isEmpty()) {
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = "建议 3-5 个标签，方便搜索与分发。",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }

                Spacer(Modifier.height(60.dp))
            }
        }
    }
}

private enum class PostMode {
    TextOnly,
    ImagesOnly,
}

@Composable
private fun ModeSelector(
    modeIndex: Int,
    onModeChange: (Int) -> Unit,
) {
    val titles = listOf("纯文字发布", "图片发布")
    TabRow(selectedTabIndex = modeIndex) {
        titles.forEachIndexed { idx, title ->
            androidx.compose.material3.Tab(
                selected = modeIndex == idx,
                onClick = { onModeChange(idx) },
                text = { Text(title) },
            )
        }
    }
}

@Composable
private fun ImageTile(
    context: Context,
    uri: Uri,
    onRemove: () -> Unit,
) {
    val bitmap = remember(uri) {
        runCatching {
            context.contentResolver.openInputStream(uri)?.use { input ->
                BitmapFactory.decodeStream(input)
            }
        }.getOrNull()
    }

    Box(
        modifier = Modifier
            .size(86.dp)
            .clip(RoundedCornerShape(12.dp))
    ) {
        if (bitmap != null) {
            androidx.compose.foundation.Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp)),
            )
        } else {
            Card(
                modifier = Modifier.fillMaxSize(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            ) {
                Box(modifier = Modifier.fillMaxSize())
            }
        }

        IconButton(
            onClick = onRemove,
            modifier = Modifier
                .align(androidx.compose.ui.Alignment.TopEnd)
                .padding(6.dp)
                .size(28.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)),
            ) {
                Icon(Icons.Filled.Delete, contentDescription = "移除图片")
            }
        }
    }
}

@Composable
fun MineScreen(
    contentPadding: PaddingValues,
    onOpenSettings: () -> Unit,
    onLogout: () -> Unit,
) {
    var showRoleDialog by remember { mutableStateOf(false) }
    var currentRole by remember { mutableStateOf("游客") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(16.dp),
    ) {
        // 用户卡片
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "旅",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White,
                    )
                }
                Spacer(Modifier.size(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("旅行者小明", style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "当前身份：$currentRole",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                IconButton(onClick = { showRoleDialog = true }) {
                    Icon(Icons.Filled.Edit, contentDescription = "切换身份")
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // 身份切换提示
        if (currentRole == "游客") {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                ),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        Icons.Filled.School,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(Modifier.size(10.dp))
                    Text(
                        "成为导游，接单赚钱，开启旅行新事业",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f),
                    )
                    TextButton(onClick = {
                        showRoleDialog = true
                    }) {
                        Text("去认证")
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
        }

        // 功能列表
        val features = remember(currentRole) {
            if (currentRole == "导游") {
                listOf(
                    FeatureItem("我的发帖", Icons.Filled.Article, "MANAGE_POSTS"),
                    FeatureItem("浏览记录", Icons.Filled.History, "BROWSE_HISTORY"),
                    FeatureItem("我的收藏", Icons.Filled.Favorite, "FAVORITES"),
                    FeatureItem("订单管理", Icons.Filled.Receipt, "ORDER_MANAGE"),
                    FeatureItem("导游设置", Icons.Filled.Badge, "GUIDE_SETTINGS"),
                )
            } else {
                listOf(
                    FeatureItem("我的发帖", Icons.Filled.Article, "MANAGE_POSTS"),
                    FeatureItem("浏览记录", Icons.Filled.History, "BROWSE_HISTORY"),
                    FeatureItem("我的收藏", Icons.Filled.Favorite, "FAVORITES"),
                    FeatureItem("订单管理", Icons.Filled.Receipt, "ORDER_MANAGE"),
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column {
                features.forEachIndexed { index, item ->
                    FeatureRow(
                        item = item,
                        onClick = {
                            // 各功能入口，后续扩展
                        },
                    )
                    if (index < features.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                        )
                    }
                }
            }
        }

        Spacer(Modifier.weight(1f))

        // 应用设置 & 退出登录
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OutlinedButton(
                onClick = onOpenSettings,
                modifier = Modifier.weight(1f),
            ) {
                Icon(Icons.Filled.Settings, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.size(6.dp))
                Text("应用设置")
            }
            OutlinedButton(
                onClick = onLogout,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error,
                ),
            ) {
                Icon(Icons.Filled.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.size(6.dp))
                Text("退出登录")
            }
        }
    }

    // 身份切换对话框
    if (showRoleDialog) {
        AlertDialog(
            onDismissRequest = { showRoleDialog = false },
            title = { Text("选择身份") },
            text = {
                Column {
                    RoleOption(
                        title = "游客",
                        desc = "浏览、收藏、发布旅行记录",
                        selected = currentRole == "游客",
                        onClick = {
                            currentRole = "游客"
                            showRoleDialog = false
                        },
                    )
                    Spacer(Modifier.height(8.dp))
                    RoleOption(
                        title = "导游",
                        desc = "认证后可接单，带团出行",
                        selected = currentRole == "导游",
                        onClick = {
                            currentRole = "导游"
                            showRoleDialog = false
                        },
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showRoleDialog = false }) {
                    Text("关闭")
                }
            },
        )
    }
}

@Composable
private fun RoleOption(
    title: String,
    desc: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (selected) MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
        )
        Spacer(Modifier.size(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(desc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun FeatureRow(
    item: FeatureItem,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            item.icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(22.dp),
        )
        Spacer(Modifier.size(14.dp))
        Text(
            text = item.title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f),
        )
        Icon(
            Icons.Filled.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

private data class FeatureItem(
    val title: String,
    val icon: ImageVector,
    val route: String,
)

private fun Modifier.clickable(onClick: () -> Unit): Modifier =
    this.then(
        Modifier.pointerInput(Unit) {
            awaitPointerEventScope {
                while (true) {
                    val event = awaitPointerEvent()
                    if (event.changes.any { it.pressed }) {
                        onClick()
                    }
                }
            }
        }
    )

// ==================== 应用设置页面 ====================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    contentPadding: PaddingValues,
    onBack: () -> Unit,
) {
    var notifyEnabled by remember { mutableStateOf(true) }
    var soundEnabled by remember { mutableStateOf(true) }
    var vibrateEnabled by remember { mutableStateOf(true) }
    var privateMode by remember { mutableStateOf(false) }
    var allowRecommend by remember { mutableStateOf(true) }
    var showOnlineStatus by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("应用设置") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ChevronRight, contentDescription = "返回", modifier = Modifier.rotate(180f))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // 通知设置
            SettingsSection(title = "通知设置") {
                SettingsSwitchItem(
                    title = "接收推送通知",
                    desc = "新消息、活动提醒",
                    checked = notifyEnabled,
                    onCheckedChange = { notifyEnabled = it },
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                SettingsSwitchItem(
                    title = "声音提醒",
                    desc = "消息提示音",
                    checked = soundEnabled,
                    onCheckedChange = { soundEnabled = it },
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                SettingsSwitchItem(
                    title = "震动提醒",
                    desc = "消息震动反馈",
                    checked = vibrateEnabled,
                    onCheckedChange = { vibrateEnabled = it },
                )
            }

            // 账号与安全
            SettingsSection(title = "账号与安全") {
                SettingsClickItem(
                    title = "修改登录密码",
                    desc = "定期更换，保障账号安全",
                    onClick = { },
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                SettingsClickItem(
                    title = "绑定手机号",
                    desc = "183****1234",
                    onClick = { },
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                SettingsClickItem(
                    title = "绑定邮箱",
                    desc = "未绑定",
                    onClick = { },
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                SettingsClickItem(
                    title = "第三方账号绑定",
                    desc = "微信 / 微博 / QQ",
                    onClick = { },
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                SettingsClickItem(
                    title = "登录设备管理",
                    desc = "查看并管理已登录设备",
                    onClick = { },
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                SettingsClickItem(
                    title = "账号注销",
                    desc = "注销后所有数据无法恢复",
                    onClick = { },
                    danger = true,
                )
            }

            // 隐私设置
            SettingsSection(title = "隐私设置") {
                SettingsSwitchItem(
                    title = "隐私模式",
                    desc = "隐藏个人动态和收藏",
                    checked = privateMode,
                    onCheckedChange = { privateMode = it },
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                SettingsSwitchItem(
                    title = "个性化内容推荐",
                    desc = "基于浏览记录推荐内容",
                    checked = allowRecommend,
                    onCheckedChange = { allowRecommend = it },
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                SettingsSwitchItem(
                    title = "显示在线状态",
                    desc = "他人可见你的在线状态",
                    checked = showOnlineStatus,
                    onCheckedChange = { showOnlineStatus = it },
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                SettingsClickItem(
                    title = "黑名单管理",
                    desc = "已屏蔽的用户列表",
                    onClick = { },
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                SettingsClickItem(
                    title = "授权管理",
                    desc = "第三方应用访问权限",
                    onClick = { },
                )
            }

            // 关于与支持
            SettingsSection(title = "关于与支持") {
                SettingsClickItem(
                    title = "关于我们",
                    desc = "版本 1.0.0",
                    onClick = { },
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                SettingsClickItem(
                    title = "用户协议",
                    desc = "《旅行 superapp 用户服务协议》",
                    onClick = { },
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                SettingsClickItem(
                    title = "隐私政策",
                    desc = "了解我们如何保护你的数据",
                    onClick = { },
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                SettingsClickItem(
                    title = "意见反馈",
                    desc = "遇到问题或建议，告诉我们",
                    onClick = { },
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                SettingsClickItem(
                    title = "检查更新",
                    desc = "当前已是最新版本",
                    onClick = { },
                )
            }
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp),
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier.padding(vertical = 4.dp),
                content = content,
            )
        }
    }
}

@Composable
private fun ColumnScope.SettingsSwitchItem(
    title: String,
    desc: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge)
            Text(
                desc,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.primary,
            ),
        )
    }
}

@Composable
private fun ColumnScope.SettingsClickItem(
    title: String,
    desc: String,
    onClick: () -> Unit,
    danger: Boolean = false,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                title,
                style = MaterialTheme.typography.bodyLarge,
                color = if (danger) MaterialTheme.colorScheme.error else Color.Unspecified,
            )
            Text(
                desc,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Icon(
            Icons.Filled.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

private fun Modifier.rotate(degrees: Float): Modifier =
    this.then(
        Modifier.graphicsLayer {
            rotationZ = degrees
        }
    )

