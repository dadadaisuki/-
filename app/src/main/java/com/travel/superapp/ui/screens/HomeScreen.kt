package com.travel.superapp.ui.screens

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.travel.superapp.data.model.PostEntity
import com.travel.superapp.data.repository.PostRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun HomeScreen(
    onOpenEntry: (String) -> Unit,
    onNavigateToGuide: () -> Unit = {},
) {
    var posts by remember { mutableStateOf<List<PostEntity>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var hasError by remember { mutableStateOf(false) }
    val postRepo = remember { PostRepository() }

    LaunchedEffect(Unit) {
        isLoading = true
        try {
            withContext(Dispatchers.IO) {
                posts = postRepo.getRecentPosts(limit = 30).getOrNull() ?: emptyList()
            }
            hasError = false
        } catch (e: Exception) {
            // 网络错误或数据库未配置，显示演示数据
            hasError = true
        }
        isLoading = false
    }

    val displayPosts = if (posts.isEmpty() || hasError) {
        demoPosts()
    } else {
        posts.map { it.toDemoPost() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        // 顶部渐变色块
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF7EC17E),   // #7ec17e
                            Color(0xFF06C4CC),   // rgb(6,196,204)
                        ),
                    ),
                )
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 20.dp),
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column {
                        Text(
                            text = "伴旅",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                            ),
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "发现美好旅程",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color.White.copy(alpha = 0.85f),
                            ),
                        )
                    }
                    // 右上角通知栏
                    NotificationBar()
                }
                Spacer(Modifier.height(12.dp))
                SearchBar(modifier = Modifier.fillMaxWidth())
            }
        }

        Spacer(Modifier.height(12.dp))
        EntryGrid(
            entries = remember { defaultEntries },
            onClick = { entry ->
                onOpenEntry(entry.route)
            },
        )

        Spacer(Modifier.height(12.dp))
        InfoMarqueeRow(
            items = remember { defaultInfoChips },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(8.dp))
        Text(
            text = "旅行动态",
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
        )

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 12.dp, end = 12.dp, bottom = 96.dp),
                verticalItemSpacing = 12.dp,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(displayPosts, key = { it.id }) { post ->
                    PostCard(post)
                }
            }
        }
    }
}

private fun PostEntity.toDemoPost() = DemoPost(
    id = id,
    title = textContent ?: "来自 ${locationName ?: "未知地点"} 的分享",
    subtitle = locationName ?: "",
    imageHeightDp = 160,
    color = Color(0xFFB3E5FC),
)

@Composable
private fun SearchBar(modifier: Modifier = Modifier) {
    var query by remember { mutableStateOf("") }
    OutlinedTextField(
        value = query,
        onValueChange = { query = it },
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text("搜索城市 / 景点 / 游记 / 导游") },
        singleLine = true,
    )
}

@Composable
private fun NotificationBar() {
    var expanded by remember { mutableStateOf(false) }

    Box {
        IconButton(
            onClick = { expanded = true },
            modifier = Modifier
                .size(40.dp)
                .background(Color.White.copy(alpha = 0.2f), CircleShape),
        ) {
            Icon(
                imageVector = Icons.Filled.Notifications,
                contentDescription = "通知",
                tint = Color.White,
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            DropdownMenuItem(
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Favorite, contentDescription = null, modifier = Modifier.size(20.dp), tint = Color(0xFFE91E63))
                        Spacer(Modifier.width(8.dp))
                        Text("我的点赞")
                    }
                },
                onClick = {
                    expanded = false
                    // 跳转到我的点赞页面
                },
            )
            DropdownMenuItem(
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Star, contentDescription = null, modifier = Modifier.size(20.dp), tint = Color(0xFFFFB300))
                        Spacer(Modifier.width(8.dp))
                        Text("我的评论")
                    }
                },
                onClick = {
                    expanded = false
                    // 跳转到我的评论页面
                },
            )
            DropdownMenuItem(
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Notifications, contentDescription = null, modifier = Modifier.size(20.dp), tint = Color(0xFF2196F3))
                        Spacer(Modifier.width(8.dp))
                        Text("系统通知")
                    }
                },
                onClick = {
                    expanded = false
                    // 跳转到系统通知页面
                },
            )
        }
    }
}

@Composable
private fun EntryGrid(
    entries: List<HomeEntry>,
    onClick: (HomeEntry) -> Unit,
) {
    Column(modifier = Modifier.padding(horizontal = 12.dp)) {
        for (row in 0 until 2) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                for (col in 0 until 4) {
                    val index = row * 4 + col
                    val entry = entries[index]
                    EntryTile(
                        entry = entry,
                        modifier = Modifier.weight(1f),
                        onClick = { onClick(entry) },
                    )
                }
            }
            if (row == 0) Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
private fun EntryTile(
    entry: HomeEntry,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Card(
        modifier = modifier
            .height(74.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(14.dp),
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = entry.icon,
                    contentDescription = entry.title,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary,
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = entry.title,
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun InfoMarqueeRow(
    items: List<InfoChip>,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items(items) { item ->
            Card(
                shape = RoundedCornerShape(999.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            ) {
                Text(
                    text = item.text,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    style = MaterialTheme.typography.labelLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun PostCard(post: DemoPost) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(post.imageHeightDp.dp)
                    .background(post.color),
            )
            Column(Modifier.padding(12.dp)) {
                Text(
                    text = post.title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = post.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

private data class HomeEntry(
    val title: String,
    val icon: ImageVector,
    val route: String,
)

private data class InfoChip(val text: String)

private data class DemoPost(
    val id: String,
    val title: String,
    val subtitle: String,
    val imageHeightDp: Int,
    val color: Color,
)

private val defaultEntries = listOf(
    HomeEntry("点亮地图", Icons.Filled.Map, "entry/map_lights"),
    HomeEntry("我的足迹", Icons.Filled.Place, "entry/my_footprints"),
    HomeEntry("美食", Icons.Filled.Restaurant, "entry/food"),
    HomeEntry("购物", Icons.Filled.ShoppingBag, "entry/shopping"),
    HomeEntry("路径跟踪", Icons.Filled.Map, "entry/route_tracking"),
    HomeEntry("结伴骑游", Icons.AutoMirrored.Filled.DirectionsBike, "entry/bike"),
    HomeEntry("乘车出游", Icons.Filled.DirectionsCar, "entry/car"),
    HomeEntry("组团", Icons.Filled.Groups, "entry/group"),
)

private val defaultInfoChips = listOf(
    InfoChip("公告：欢迎加入旅行平台内测"),
    InfoChip("活动：发布游记赢周边礼品"),
    InfoChip("公告：导游认证入口即将上线"),
)

private fun demoPosts(): List<DemoPost> {
    val palette = listOf(
        Color(0xFFB3E5FC),
        Color(0xFFC8E6C9),
        Color(0xFFFFF9C4),
        Color(0xFFD1C4E9),
        Color(0xFFFFCCBC),
        Color(0xFFBBDEFB),
    )
    return (1..30).map { i ->
        DemoPost(
            id = "post_$i",
            title = "旅行记录 #$i：这座城市真的太好逛了",
            subtitle = "两天一夜轻松路线｜美食打卡｜拍照点位合集",
            imageHeightDp = if (i % 3 == 0) 210 else if (i % 3 == 1) 160 else 190,
            color = palette[i % palette.size],
        )
    }
}

