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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

// ─── 我的足迹页 ───────────────────────────────────────────────────────────────

private data class Footprint(
    val id: String,
    val city: String,
    val province: String,
    val visitedAt: String,
    val notes: String,
    val category: String,
    val tags: List<String>,
)

private fun demoFootprints(): List<Footprint> = listOf(
    Footprint("fp1", "杭州", "浙江", "2025-08-12", "西湖边散步，夜游河坊街", "旅行", listOf("西湖", "美食", "夜景")),
    Footprint("fp2", "苏州", "江苏", "2025-10-03", "园林之旅，平江路古街", "旅行", listOf("园林", "古镇")),
    Footprint("fp3", "乌镇", "浙江", "2025-10-05", "水乡古镇，拍照打卡", "旅行", listOf("古镇", "夜景")),
    Footprint("fp4", "上海", "上海", "2025-12-20", "外滩跨年，陆家嘴", "旅行", listOf("都市", "夜景")),
    Footprint("fp5", "黄山", "安徽", "2025-05-01", "爬山看日出，难忘的云海", "旅行", listOf("爬山", "自然")),
    Footprint("fp6", "千岛湖", "浙江", "2025-07-15", "划船玩水，农家乐", "旅行", listOf("避暑", "水上")),
)

private val ProvinceColorMap = mapOf(
    "浙江" to Color(0xFF4CAF50),
    "江苏" to Color(0xFF2196F3),
    "上海" to Color(0xFFFF9800),
    "安徽" to Color(0xFF9C27B0),
    "北京" to Color(0xFFF44336),
    "广东" to Color(0xFFE91E63),
    "四川" to Color(0xFFFF5722),
    "云南" to Color(0xFF00BCD4),
    "山东" to Color(0xFF795548),
    "福建" to Color(0xFF009688),
)

@Composable
fun FootprintScreen(modifier: Modifier = Modifier) {
    var selectedProvince by remember { mutableStateOf<String?>(null) }
    val provinces = remember { demoFootprints().map { it.province }.distinct() }

    val filtered = remember(selectedProvince) {
        if (selectedProvince == null) demoFootprints()
        else demoFootprints().filter { it.province == selectedProvince }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Text(
                text = "📍 我的足迹",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold),
            )
        }

        item {
            Text(
                text = "点亮过的城市，终会成为回忆",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(12.dp))
        }

        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    ProvinceChip(
                        label = "全部",
                        selected = selectedProvince == null,
                        color = MaterialTheme.colorScheme.primary,
                        onClick = { selectedProvince = null },
                    )
                }
                items(provinces) { province ->
                    ProvinceChip(
                        label = province,
                        selected = selectedProvince == province,
                        color = ProvinceColorMap[province] ?: MaterialTheme.colorScheme.primary,
                        onClick = { selectedProvince = if (selectedProvince == province) null else province },
                    )
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "共 ${filtered.size} 个足迹",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = "点亮 ${ProvinceColorMap.keys.count { p -> demoFootprints().any { it.province == p } } } 个省级行政区",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }

        item { Spacer(Modifier.height(4.dp)) }

        items(filtered, key = { it.id }) { footprint ->
            FootprintCard(footprint)
        }
    }
}

@Composable
private fun ProvinceChip(
    label: String,
    selected: Boolean,
    color: Color,
    onClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) color.copy(alpha = 0.2f)
                else MaterialTheme.colorScheme.surfaceVariant,
        ),
        border = if (selected) androidx.compose.foundation.BorderStroke(2.dp, color) else null,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (selected) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = color,
                )
                Spacer(Modifier.width(4.dp))
            }
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = if (selected) color else MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun FootprintCard(footprint: Footprint) {
    val color = ProvinceColorMap[footprint.province] ?: MaterialTheme.colorScheme.primary

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(color.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = footprint.province.firstOrNull()?.toString() ?: "?",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = color,
                        )
                    }
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(
                            text = footprint.city,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        )
                        Text(
                            text = footprint.province,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                Text(
                    text = footprint.visitedAt,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            if (footprint.notes.isNotEmpty()) {
                Spacer(Modifier.height(10.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                Spacer(Modifier.height(10.dp))
                Text(
                    text = footprint.notes,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Spacer(Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(footprint.tags) { tag ->
                    Text(
                        text = "#$tag",
                        style = MaterialTheme.typography.labelSmall,
                        color = color,
                    )
                }
            }
        }
    }
}
