package com.travel.superapp.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleEntryScreen(
    title: String,
    contentPadding: PaddingValues,
    onBack: () -> Unit,
    content: @Composable () -> Unit = {},
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "返回")
                    }
                },
            )
        },
    ) { inner ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(inner),
        ) {
            content()
        }
    }
}

// ============ 点亮地图 ============
@Composable
fun MapLightsScreen(contentPadding: PaddingValues, onBack: () -> Unit) {
    val cities = listOf(
        "杭州市", "北京市", "上海市", "广州市", "深圳市",
        "成都市", "南京市", "西安市", "苏州市", "武汉市",
        "重庆市", "天津市", "青岛市", "大连市", "厦门市",
    )
    val visited = setOf("杭州市", "上海市", "北京市", "成都市")
    SimpleEntryScreen(title = "点亮地图", contentPadding = contentPadding, onBack = onBack) {
        LazyColumn(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item {
                Text(
                    text = "已点亮 ${visited.size} 个城市",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                )
                Spacer(Modifier.height(8.dp))
            }
            items(cities) { city ->
                val isVisited = city in visited
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isVisited) MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    ),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Place,
                            contentDescription = null,
                            tint = if (isVisited) MaterialTheme.colorScheme.primary else Color.Gray,
                            modifier = Modifier.size(24.dp),
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = city,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = if (isVisited) FontWeight.Medium else FontWeight.Normal,
                            ),
                            color = if (isVisited) MaterialTheme.colorScheme.onPrimaryContainer else Color.Gray,
                        )
                        Spacer(Modifier.weight(1f))
                        Text(
                            text = if (isVisited) "已点亮" else "未踏足",
                            style = MaterialTheme.typography.labelMedium,
                            color = if (isVisited) MaterialTheme.colorScheme.primary else Color.Gray,
                        )
                    }
                }
            }
        }
    }
}

// ============ 我的足迹 ============
@Composable
fun MyFootprintsScreen(contentPadding: PaddingValues, onBack: () -> Unit) {
    val footprints = listOf(
        Triple("西湖断桥残雪", "杭州市", "2024-03-10"),
        Triple("故宫博物院", "北京市", "2024-02-18"),
        Triple("外滩夜景", "上海市", "2023-12-25"),
        Triple("宽窄巷子", "成都市", "2023-11-12"),
        Triple("秦淮河夜景", "南京市", "2023-10-05"),
    )
    SimpleEntryScreen(title = "我的足迹", contentPadding = contentPadding, onBack = onBack) {
        LazyColumn(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(footprints) { (place, city, date) ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = place,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "$city · $date",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}

// ============ 路径跟踪 ============
@Composable
fun RouteTrackingScreen(contentPadding: PaddingValues, onBack: () -> Unit) {
    val routes = listOf(
        Triple("西湖环湖骑行路线", "12.5km", "约2小时"),
        Triple("灵隐寺登山路线", "6.8km", "约3小时"),
        Triple("钱塘江夜跑路线", "8.2km", "约1小时"),
        Triple("西溪湿地漫步路线", "5.3km", "约2小时"),
    )
    SimpleEntryScreen(title = "路径跟踪", contentPadding = contentPadding, onBack = onBack) {
        LazyColumn(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Route,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp),
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = "本周累计",
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Spacer(Modifier.weight(1f))
                        Text(
                            text = "32.8 km",
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
            items(routes) { (name, distance, duration) ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Route,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(32.dp),
                        )
                        Spacer(Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = name,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "距离 $distance · 预计 $duration",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }
        }
    }
}
