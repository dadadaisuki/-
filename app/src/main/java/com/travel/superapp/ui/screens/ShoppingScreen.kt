package com.travel.superapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.outlined.LocalMall
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.Store
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlin.math.*

// ─── 购物页 ──────────────────────────────────────────────────────────────────

private data class ShopItem(
    val id: String,
    val name: String,
    val location: String,
    val latitude: Double,
    val longitude: Double,
    val category: String,
    val priceRange: String,
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

// 西安购物中心数据
private fun demoShops(): List<ShopItem> = listOf(
    ShopItem("s1", "赛格国际购物中心", "雁塔区小寨", 34.2249, 108.9451, "综合商场", "¥¥¥", 4.8, listOf("高端", "网红", "美食")),
    ShopItem("s2", "SKP", "碑林区长安路", 34.2446, 108.9512, "综合商场", "¥¥¥¥", 4.9, listOf("奢侈品", "大牌", "品质")),
    ShopItem("s3", "大悦城", "雁塔区慈恩路", 34.2213, 108.9496, "综合商场", "¥¥¥", 4.7, listOf("年轻", "网红", "潮牌")),
    ShopItem("s4", "大融城", "未央区凤城七路", 34.2912, 108.9372, "综合商场", "¥¥¥", 4.6, listOf("家庭", "亲子", "美食")),
    ShopItem("s5", "万达广场", "碑林区东大街", 34.2592, 108.9521, "综合商场", "¥¥", 4.5, listOf("大众", "实惠", "亲子")),
    ShopItem("s6", "民生百货", "新城区解放路", 34.2671, 108.9689, "百货", "¥¥", 4.3, listOf("老字号", "日用品", "实惠")),
    ShopItem("s7", "开元商城", "碑林区钟楼", 34.2596, 108.9432, "百货", "¥¥", 4.4, listOf("老字号", "品牌", "折扣")),
    ShopItem("s8", "骡马市步行街", "碑林区东大街", 34.2612, 108.9563, "商业街", "¥¥", 4.3, listOf("潮流", "年轻人", "美食")),
    ShopItem("s9", "小寨商业街", "雁塔区小寨", 34.2268, 108.9458, "商业街", "¥¥", 4.4, listOf("学生", "潮流", "美食")),
    ShopItem("s10", "回民街", "莲湖区北院门", 34.2659, 108.9375, "特色街区", "¥", 4.6, listOf("小吃", "特产", "伴手礼")),
    ShopItem("s11", "大唐不夜城", "雁塔区雁塔路", 34.2208, 108.9489, "商业街", "¥¥", 4.8, listOf("网红", "夜景", "文化")),
    ShopItem("s12", "印象城", "碑林区南门外", 34.2501, 108.9489, "综合商场", "¥¥¥", 4.5, listOf("年轻", "潮流", "餐饮")),
)

private val ShopCategoryColors = mapOf(
    "综合商场" to Color(0xFFE3F2FD),
    "百货" to Color(0xFFFCE4EC),
    "商业街" to Color(0xFFF3E5F5),
    "特色街区" to Color(0xFFFFF3E0),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingScreen(
    contentPadding: PaddingValues,
    onBack: () -> Unit,
) {
    var filter by remember { mutableStateOf("全部") }
    val userLat = mockUserLocationXian.first
    val userLon = mockUserLocationXian.second

    // 添加距离信息
    val shopsWithDistance = remember {
        demoShops().map { shop ->
            val distance = calculateDistance(userLat, userLon, shop.latitude, shop.longitude)
            shop to distance
        }.sortedBy { it.second }
    }

    val categories = listOf("全部", "综合商场", "百货", "商业街", "特色街区")
    val filtered = remember(filter) {
        if (filter == "全部") shopsWithDistance
        else shopsWithDistance.filter { it.first.category == filter }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding),
    ) {
        TopAppBar(
            title = { Text("附近购物中心") },
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
                        text = "根据您的位置自动推荐周边商场",
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

            items(filtered, key = { it.first.id }) { (shop, distance) ->
                ShopCard(shop, distance)
            }
        }
    }
}

@Composable
private fun ShopCard(shop: ShopItem, distanceMeters: Double) {
    val icon: ImageVector = when (shop.category) {
        "综合商场" -> Icons.Outlined.LocalMall
        "商业街" -> Icons.Outlined.Store
        else -> Icons.Outlined.ShoppingBag
    }
    val bgColor = ShopCategoryColors[shop.category] ?: MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)

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
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = bgColor),
                modifier = Modifier.size(70.dp),
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(16.dp)
                        .size(38.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = shop.name,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f),
                    )
                    Text(
                        text = "⭐ ${shop.rating}",
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
                        text = shop.location,
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

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = shop.category,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.secondary,
                    )
                    Text(
                        text = shop.priceRange,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                Spacer(Modifier.height(6.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(shop.tags) { tag ->
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
