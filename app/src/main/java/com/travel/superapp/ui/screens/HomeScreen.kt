package com.travel.superapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    contentPadding: PaddingValues,
    onOpenEntry: (String) -> Unit,
) {
    val entries = remember {
        listOf(
            HomeEntry("点亮地图", Icons.Filled.Map, "entry/map_lights"),
            HomeEntry("我的足迹", Icons.Filled.Place, "entry/my_footprints"),
            HomeEntry("美食", Icons.Filled.Restaurant, "entry/food"),
            HomeEntry("购物", Icons.Filled.ShoppingBag, "entry/shopping"),
            HomeEntry("路径跟踪", Icons.Filled.Map, "entry/route_tracking"),
            HomeEntry("骑行", Icons.AutoMirrored.Filled.DirectionsBike, "entry/bike"),
            HomeEntry("乘车游", Icons.Filled.DirectionsCar, "entry/car"),
            HomeEntry("组团", Icons.Filled.Groups, "entry/group"),
        )
    }

    val infoItems = remember {
        listOf(
            InfoChip("公告：欢迎加入旅行平台内测"),
            InfoChip("广告：春季特惠机酒套餐"),
            InfoChip("活动：发布游记赢周边礼品"),
            InfoChip("公告：导游认证入口即将上线"),
        )
    }

    val posts = remember { demoPosts() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .background(MaterialTheme.colorScheme.background),
    ) {
        Spacer(Modifier.height(8.dp))
        SearchBar(modifier = Modifier.padding(horizontal = 16.dp))

        Spacer(Modifier.height(12.dp))
        EntryGrid(
            entries = entries,
            onClick = { onOpenEntry(it.route) },
        )

        Spacer(Modifier.height(12.dp))
        InfoMarqueeRow(
            items = infoItems,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(8.dp))
        Text(
            text = "旅行动态",
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
        )

        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 12.dp, end = 12.dp, bottom = 96.dp),
            verticalItemSpacing = 12.dp,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(posts, key = { it.id }) { post ->
                PostCard(post)
            }
        }
    }
}

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
private fun EntryGrid(
    entries: List<HomeEntry>,
    onClick: (HomeEntry) -> Unit,
) {
    Column(modifier = Modifier.padding(horizontal = 12.dp)) {
        for (row in 0 until 2) {
            androidx.compose.foundation.layout.Row(
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

