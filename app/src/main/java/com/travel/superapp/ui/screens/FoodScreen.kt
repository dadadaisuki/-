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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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

// ─── 美食页 ──────────────────────────────────────────────────────────────────

private data class FoodItem(
    val id: String,
    val name: String,
    val location: String,
    val category: String,
    val price: String,
    val rating: Double,
    val tags: List<String>,
)

private fun demoFoods(): List<FoodItem> = listOf(
    FoodItem("f1", "外婆家", "杭州西湖区", "江浙菜", "¥60/人", 4.5, listOf("实惠", "家常菜")),
    FoodItem("f2", "绿茶餐厅", "杭州上城区", "江浙菜", "¥70/人", 4.3, listOf("排队王", "适合聚餐")),
    FoodItem("f3", "海底捞火锅", "杭州拱墅区", "火锅", "¥150/人", 4.7, listOf("服务好", "24h")),
    FoodItem("f4", "知味观", "杭州西湖区", "杭帮菜", "¥100/人", 4.2, listOf("老字号", "小吃")),
    FoodItem("f5", "楼外楼", "杭州西湖区", "杭帮菜", "¥200/人", 4.6, listOf("景点餐厅", "名人打卡")),
    FoodItem("f6", "咬不得高祖生煎", "杭州上城区", "小吃", "¥30/人", 4.1, listOf("生煎", "早餐")),
)

private val FoodColors = listOf(
    Color(0xFFFFCDD2), Color(0xFFFFF9C4), Color(0xFFDCEDC8),
    Color(0xFFD1C4E9), Color(0xFFFFE0B2), Color(0xFFB2DFDB),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodScreen(modifier: Modifier = Modifier) {
    var filter by remember { mutableStateOf("全部") }
    val tags = listOf("全部", "江浙菜", "火锅", "小吃", "烧烤", "西餐", "川菜", "粤菜")

    val filtered = remember(filter) {
        if (filter == "全部") demoFoods() else demoFoods().filter { it.category.contains(filter) }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Text(
                text = "🍜 美食推荐",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold),
            )
        }

        item {
            Text(
                text = "发现身边的美味，吃遍大江南北",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(12.dp))
        }

        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(tags) { tag ->
                    FilterChip(
                        selected = filter == tag,
                        onClick = { filter = tag },
                        label = { Text(tag) },
                    )
                }
            }
        }

        item { Spacer(Modifier.height(4.dp)) }

        items(filtered, key = { it.id }) { food ->
            FoodCard(food)
        }
    }
}

@Composable
private fun FoodCard(food: FoodItem) {
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
                    .size(80.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .padding(0.dp),
            ) {
                Text(
                    text = food.name.take(1),
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(10.dp)),
                    // 用背景色代替图片
                )
            }
            Spacer(Modifier.width(12.dp))
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
                    )
                    Text(
                        text = food.rating.toString(),
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = food.location,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
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
