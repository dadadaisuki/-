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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.Store
import androidx.compose.material.icons.outlined.LocalMall
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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

// ─── 购物页 ──────────────────────────────────────────────────────────────────

private data class ShopItem(
    val id: String,
    val name: String,
    val location: String,
    val category: String,
    val priceRange: String,
    val rating: Double,
    val tags: List<String>,
)

private fun demoShops(): List<ShopItem> = listOf(
    ShopItem("s1", "杭州大厦", "杭州拱墅区", "综合商场", "¥¥¥¥", 4.6, listOf("奢侈品", "大牌")),
    ShopItem("s2", "武林银泰", "杭州下城区", "百货", "¥¥¥", 4.3, listOf("美妆", "折扣")),
    ShopItem("s3", "湖滨银泰", "杭州上城区", "商业街", "¥¥¥", 4.5, listOf("网红", "步行街")),
    ShopItem("s4", "河坊街", "杭州上城区", "特色街区", "¥¥", 4.4, listOf("老字号", "伴手礼")),
    ShopItem("s5", "四季青", "杭州上城区", "服装批发", "¥", 4.0, listOf("便宜", "砍价")),
    ShopItem("s6", "万象城", "杭州江干区", "综合商场", "¥¥¥¥", 4.7, listOf("高端", "品质")),
    ShopItem("s7", "龙翔桥", "杭州上城区", "小商品", "¥", 3.8, listOf("便宜", "杂货")),
    ShopItem("s8", "in77", "杭州西湖区", "商业中心", "¥¥¥", 4.6, listOf("年轻人", "网红店")),
)

private val ShopCategoryColors = mapOf(
    "综合商场" to Color(0xFFE3F2FD),
    "百货" to Color(0xFFFCE4EC),
    "商业街" to Color(0xFFF3E5F5),
    "特色街区" to Color(0xFFFFF3E0),
    "服装批发" to Color(0xFFE8F5E9),
    "小商品" to Color(0xFFFBE9E7),
    "商业中心" to Color(0xFFE0F7FA),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingScreen(modifier: Modifier = Modifier) {
    var filter by remember { mutableStateOf("全部") }
    val categories = listOf("全部", "综合商场", "百货", "商业街", "特色街区", "服装批发", "小商品")
    val filtered = remember(filter) {
        if (filter == "全部") demoShops() else demoShops().filter { it.category == filter }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Text(
                text = "🛍️ 购物指南",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold),
            )
        }

        item {
            Text(
                text = "发现本地好店，买遍地道好物",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(12.dp))
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

        item { Spacer(Modifier.height(4.dp)) }

        items(filtered, key = { it.id }) { shop ->
            ShopCard(shop)
        }
    }
}

@Composable
private fun ShopCard(shop: ShopItem) {
    val icon: ImageVector = when (shop.category) {
        "综合商场" -> Icons.Outlined.LocalMall
        "商业街", "商业中心" -> Icons.Outlined.Store
        else -> Icons.Outlined.ShoppingBag
    }
    val bgColor = ShopCategoryColors[shop.category] ?: MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)

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
                modifier = Modifier.width(80.dp).height(80.dp),
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.padding(18.dp),
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

                Text(
                    text = shop.location,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

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