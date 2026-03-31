package com.travel.superapp.ui.screens

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlin.math.*

// ─── 美食页 ──────────────────────────────────────────────────────────────────

private data class FoodItem(
    val id: String,
    val name: String,
    val location: String,
    val latitude: Double,
    val longitude: Double,
    val category: String,
    val price: String,
    val rating: Double,
    val tags: List<String>,
)

// 模拟用户位置（西安钟楼附近）
private val mockUserLocationXian = Pair(34.2596, 108.9432)

// 计算距离（米）
private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val r = 6371000.0 // 地球半径（米）
    val p1 = Math.toRadians(lat1)
    val p2 = Math.toRadians(lat2)
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val h = sin(dLat / 2) * sin(dLat / 2) +
        cos(p1) * cos(p2) * sin(dLon / 2) * sin(dLon / 2)
    return 2 * r * asin(sqrt(h))
}

// 西安美食数据
private fun demoFoods(): List<FoodItem> = listOf(
    FoodItem("f1", "老孙家泡馍", "莲湖区北院门", 34.2659, 108.9375, "陕菜", "¥80/人", 4.8, listOf("老字号", "泡馍", "必吃")),
    FoodItem("f2", "西安饭庄", "碑林区东大街", 34.2608, 108.9521, "陕菜", "¥120/人", 4.6, listOf("老字号", "特色菜", "宴请")),
    FoodItem("f3", "德发长", "碑林区钟楼", 34.2596, 108.9432, "饺子", "¥60/人", 4.5, listOf("老字号", "饺子", "百年")),
    FoodItem("f4", "同盛祥", "莲湖区北院门", 34.2655, 108.9380, "陕菜", "¥70/人", 4.7, listOf("泡馍", "网红", "必吃")),
    FoodItem("f5", "春发生", "碑林区南院门", 34.2528, 108.9478, "陕菜", "¥50/人", 4.4, listOf("葫芦头", "老字号", "小吃")),
    FoodItem("f6", "贾三灌汤包", "莲湖区北院门", 34.2662, 108.9372, "小吃", "¥40/人", 4.8, listOf("灌汤包", "网红", "必吃")),
    FoodItem("f7", "魏家凉皮", "雁塔区小寨", 34.2268, 108.9458, "小吃", "¥25/人", 4.3, listOf("凉皮", "快餐", "实惠")),
    FoodItem("f8", "海底捞火锅", "雁塔区小寨", 34.2255, 108.9462, "火锅", "¥150/人", 4.7, listOf("服务好", "24h", "连锁")),
    FoodItem("f9", "醉长安", "雁塔区大唐不夜城", 34.2208, 108.9489, "陕菜", "¥180/人", 4.9, listOf("网红", "环境好", "拍照")),
    FoodItem("f10", "长安大牌档", "雁塔区小寨", 34.2258, 108.9465, "陕菜", "¥90/人", 4.6, listOf("网红", "小吃", "表演")),
    FoodItem("f11", "遇见长安", "碑林区南门", 34.2515, 108.9485, "陕菜", "¥100/人", 4.5, listOf("网红", "拍照", "打卡")),
    FoodItem("f12", "biangbiang面", "莲湖区大皮院", 34.2675, 108.9365, "面食", "¥30/人", 4.8, listOf("面食", "网红", "必吃")),
)

private val FoodColors = listOf(
    Color(0xFFFFCDD2), Color(0xFFFFF9C4), Color(0xFFDCEDC8),
    Color(0xFFD1C4E9), Color(0xFFFFE0B2), Color(0xFFB2DFDB),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodScreen(
    contentPadding: PaddingValues,
    onBack: () -> Unit,
) {
    var filter by remember { mutableStateOf("全部") }
    val userLat = mockUserLocationXian.first
    val userLon = mockUserLocationXian.second

    // 添加距离信息
    val foodsWithDistance = remember {
        demoFoods().map { food ->
            val distance = calculateDistance(userLat, userLon, food.latitude, food.longitude)
            food to distance
        }.sortedBy { it.second }
    }

    val categories = listOf("全部", "陕菜", "小吃", "火锅", "饺子", "面食")
    val filtered = remember(filter) {
        if (filter == "全部") foodsWithDistance
        else foodsWithDistance.filter { it.first.category.contains(filter) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding),
    ) {
        TopAppBar(
            title = { Text("附近美食") },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "根据您的位置自动推荐周边餐厅",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = "共 ${filtered.size} 家",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }

            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(categories) { cat ->
                        FilterChip(
                            selected = filter == cat,
                            onClick = { filter = cat },
                            label = { Text(cat) },
                        )
                    }
                }
            }

            items(filtered, key = { it.first.id }) { (food, distance) ->
                FoodCard(food, distance)
            }
        }
    }
}

@Composable
private fun FoodCard(food: FoodItem, distanceMeters: Double) {
    val bgColor = FoodColors[food.id.hashCode() % FoodColors.size]

    // 格式化距离
    val distanceText = when {
        distanceMeters < 1000 -> "${distanceMeters.toInt()}m"
        else -> String.format("%.1fkm", distanceMeters / 1000)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .clip(RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = bgColor),
                    modifier = Modifier.fillMaxSize(),
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Restaurant,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp),
                            tint = Color.White.copy(alpha = 0.8f),
                        )
                    }
                }
            }

            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = food.name,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f),
                    )
                    Text(
                        text = "⭐ ${food.rating}",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.primary,
                    )
                }

                Spacer(Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = food.location,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f),
                    )
                    Text(
                        text = distanceText,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }

                Spacer(Modifier.height(4.dp))

                Text(
                    text = food.price,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.secondary,
                )

                Spacer(Modifier.height(6.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(food.tags) { tag ->
                        Text(
                            text = "#$tag",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }
        }
    }
}
