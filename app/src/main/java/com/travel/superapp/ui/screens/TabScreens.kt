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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.travel.superapp.data.ai.ChatBubble
import com.travel.superapp.data.ai.DeepSeekRepository
import com.travel.superapp.data.ai.MessageRole
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiScreen(contentPadding: PaddingValues) {
    val repository = remember { DeepSeekRepository() }
    val chatMessages by repository.messages.collectAsState()
    val isTyping by repository.isTyping.collectAsState()

    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // 初始化对话
    LaunchedEffect(Unit) {
        repository.initConversation()
    }

    // 新消息到来时自动滚动到底部
    LaunchedEffect(chatMessages.size) {
        if (chatMessages.isNotEmpty()) {
            coroutineScope.launch {
                listState.animateScrollToItem(chatMessages.lastIndex)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "AI",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                        Spacer(Modifier.size(10.dp))
                        Column {
                            Text(
                                text = "旅行 AI 助手",
                                style = MaterialTheme.typography.titleMedium,
                            )
                            Text(
                                text = "Powered by DeepSeek",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { repository.clearConversation() }) {
                        Icon(
                            Icons.Filled.Refresh,
                            contentDescription = "清空对话",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(innerPadding)
                .imePadding(),
        ) {
            // ===== 消息列表 =====
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 8.dp),
            ) {
                items(chatMessages, key = { it.id }) { bubble ->
                    ChatBubbleItem(bubble = bubble)
                }

                // 正在输入的提示
                if (isTyping && chatMessages.lastOrNull()?.role == MessageRole.ASSISTANT) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start,
                        ) {
                            Box(
                                modifier = Modifier
                                    .widthIn(max = 260.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                                contentAlignment = Alignment.CenterStart,
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.primary),
                                    ) {}
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)),
                                    ) {}
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                                    ) {}
                                }
                            }
                        }
                    }
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

            // ===== 输入栏 =====
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.Bottom,
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("问我任何关于旅行的问题...") },
                    maxLines = 4,
                    enabled = !isTyping,
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    ),
                )
                Spacer(Modifier.size(8.dp))
                IconButton(
                    onClick = {
                        val text = inputText.trim()
                        if (text.isNotEmpty() && !isTyping) {
                            inputText = ""
                            coroutineScope.launch {
                                repository.sendStreamMessage(text)
                            }
                        }
                    },
                    enabled = inputText.isNotBlank() && !isTyping,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            if (inputText.isNotBlank() && !isTyping)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.surfaceVariant,
                        ),
                ) {
                    if (isTyping) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary,
                        )
                    } else {
                        Icon(
                            Icons.Filled.Send,
                            contentDescription = "发送",
                            tint = if (inputText.isNotBlank())
                                MaterialTheme.colorScheme.onPrimary
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatBubbleItem(bubble: ChatBubble) {
    val isUser = bubble.role == MessageRole.USER

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
            verticalAlignment = Alignment.Bottom,
        ) {
            // 头像
            if (!isUser) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "AI",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Spacer(Modifier.size(6.dp))
            }

            Box(
                modifier = Modifier
                    .widthIn(max = 280.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (isUser) 16.dp else 4.dp,
                            bottomEnd = if (isUser) 4.dp else 16.dp,
                        )
                    )
                    .background(
                        if (isUser)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.surfaceVariant,
                    )
                    .padding(12.dp),
            ) {
                Text(
                    text = bubble.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isUser)
                        MaterialTheme.colorScheme.onPrimary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            // 用户头像
            if (isUser) {
                Spacer(Modifier.size(6.dp))
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "我",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun PostScreen(contentPadding: PaddingValues) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // 表单状态
    var textContent by remember { mutableStateOf("") }
    var selectedImages by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var showLocation by remember { mutableStateOf(false) }
    var locationText by remember { mutableStateOf("") }
    var tagText by remember { mutableStateOf("") }
    var tags by remember { mutableStateOf<List<String>>(emptyList()) }

    // 发布状态
    var isPublishing by remember { mutableStateOf(false) }
    var uploadProgress by remember { mutableStateOf<String?>(null) }  // 如 "上传中 (2/5)"

    fun hasContent(): Boolean =
        textContent.trim().isNotEmpty() || selectedImages.isNotEmpty()

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents(),
    ) { uris ->
        if (uris.isNullOrEmpty()) return@rememberLauncherForActivityResult
        val next = (selectedImages + uris.toList()).distinctBy { it.toString() }.take(9)
        selectedImages = next
        coroutineScope.launch {
            snackbarHostState.showSnackbar("已选择 ${next.size} 张图片")
        }
    }

    fun resetForm() {
        textContent = ""
        selectedImages = emptyList()
        showLocation = false
        locationText = ""
        tags = emptyList()
        tagText = ""
        isPublishing = false
        uploadProgress = null
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
                        if (showLocation && locationText.trim().isEmpty()) {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("开启显示地理位置后，请填写地点")
                            }
                            return@Button
                        }
                        coroutineScope.launch {
                            val userId = com.travel.superapp.data.auth.AuthManager.currentUserId ?: run {
                                snackbarHostState.showSnackbar("请先登录后再发布")
                                return@launch
                            }
                            isPublishing = true
                            val postRepo = com.travel.superapp.data.repository.PostRepository()
                            val result = postRepo.createPostWithImages(
                                userId = userId,
                                context = context,
                                imageUris = selectedImages,
                                textContent = textContent.trim().ifEmpty { null },
                                locationName = if (showLocation) locationText.trim() else null,
                                showLocation = showLocation,
                                tags = tags,
                                onImageUploaded = { uploaded, total ->
                                    uploadProgress = "上传中 ($uploaded/$total)"
                                },
                            )
                            isPublishing = false
                            uploadProgress = null
                            result.fold(
                                onSuccess = {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("发布成功！")
                                    }
                                    resetForm()
                                },
                                onFailure = {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("发布失败：${it.message}")
                                    }
                                },
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = hasContent() && !isPublishing,
                    colors = ButtonDefaults.buttonColors(
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    ),
                ) {
                    if (isPublishing) {
                        if (uploadProgress != null) {
                            Icon(
                                Icons.Filled.Photo,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                            )
                            Spacer(Modifier.size(8.dp))
                            Text(uploadProgress!!)
                        } else {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp,
                            )
                            Spacer(Modifier.size(8.dp))
                            Text("发布中...")
                        }
                    } else {
                        Icon(
                            Icons.Filled.Article,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                        )
                        Spacer(Modifier.size(8.dp))
                        Text("发布")
                    }
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
                // ===== 文字内容输入 =====
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                        ) {
                            Text("文字内容", style = MaterialTheme.typography.titleSmall)
                            Text(
                                "${textContent.length}/1000",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = textContent,
                            onValueChange = { if (it.length <= 1000) textContent = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("写下你的旅行故事...（可选）") },
                            minLines = 4,
                            maxLines = 8,
                            enabled = !isPublishing,
                        )
                    }
                }

                // ===== 图片选择 =====
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                        ) {
                            Text("添加图片", style = MaterialTheme.typography.titleSmall)
                            Text(
                                "${selectedImages.size}/9",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        Spacer(Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            OutlinedButton(
                                onClick = { imagePicker.launch("image/*") },
                                enabled = !isPublishing,
                            ) {
                                Icon(Icons.Filled.Photo, contentDescription = null)
                                Spacer(Modifier.size(6.dp))
                                Text("选择图片")
                            }
                            if (selectedImages.isNotEmpty()) {
                                OutlinedButton(
                                    onClick = { selectedImages = emptyList() },
                                    enabled = !isPublishing,
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = MaterialTheme.colorScheme.error,
                                    ),
                                ) {
                                    Icon(Icons.Filled.Delete, contentDescription = null)
                                    Spacer(Modifier.size(6.dp))
                                    Text("清空")
                                }
                            }
                        }

                        if (selectedImages.isNotEmpty()) {
                            Spacer(Modifier.height(10.dp))
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(3),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(((selectedImages.size + 2) / 3 * 96).dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                userScrollEnabled = false,
                            ) {
                                gridItems(selectedImages) { uri ->
                                    ImageTile(
                                        context = context,
                                        uri = uri,
                                        onRemove = {
                                            if (!isPublishing) {
                                                selectedImages = selectedImages.filterNot { it == uri }
                                            }
                                        },
                                        isRemoving = isPublishing,
                                    )
                                }
                            }
                        } else {
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "暂无图片。最多支持 9 张图片。",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }

                // ===== 地理位置 =====
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
                                onCheckedChange = { if (!isPublishing) showLocation = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                                ),
                            )
                        }
                        if (showLocation) {
                            Spacer(Modifier.height(10.dp))
                            OutlinedTextField(
                                value = locationText,
                                onValueChange = { if (!isPublishing) locationText = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("例：上海外滩 / 成都宽窄巷子") },
                                singleLine = true,
                                enabled = !isPublishing,
                                leadingIcon = {
                                    Icon(Icons.Filled.LocationOn, contentDescription = null)
                                },
                            )
                        }
                    }
                }

                // ===== 标签 =====
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
                            Text("添加标签", style = MaterialTheme.typography.titleSmall)
                            Text(
                                "${tags.size}/12",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        Spacer(Modifier.height(8.dp))

                        if (tags.isNotEmpty()) {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                items(tags) { tag ->
                                    FilterChip(
                                        selected = true,
                                        onClick = {
                                            if (!isPublishing) {
                                                tags = tags.filterNot { it == tag }
                                            }
                                        },
                                        label = { Text("#$tag") },
                                        trailingIcon = {
                                            if (!isPublishing) {
                                                Icon(
                                                    Icons.Filled.Close,
                                                    contentDescription = "移除",
                                                    modifier = Modifier.size(14.dp),
                                                )
                                            }
                                        },
                                    )
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                        ) {
                            OutlinedTextField(
                                value = tagText,
                                onValueChange = { if (!isPublishing) tagText = it },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(56.dp),
                                placeholder = { Text("输入标签，回车添加") },
                                singleLine = true,
                                enabled = !isPublishing,
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
                                enabled = !isPublishing && tagText.isNotBlank(),
                            ) {
                                Text("添加")
                            }
                        }
                        if (tags.isEmpty()) {
                            Spacer(Modifier.height(6.dp))
                            Text(
                                "建议添加 3-5 个标签，方便搜索与分发",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }

                // 发布条件提示
                if (!hasContent() && !isPublishing) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        ),
                    ) {
                        Text(
                            "请输入文字内容或添加图片后再发布",
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                Spacer(Modifier.height(80.dp))
            }
        }
    }
}

@Composable
private fun ImageTile(
    context: Context,
    uri: Uri,
    onRemove: () -> Unit,
    isRemoving: Boolean = false,
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
            enabled = !isRemoving,
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

