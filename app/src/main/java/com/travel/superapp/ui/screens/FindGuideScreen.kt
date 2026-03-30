package com.travel.superapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.travel.superapp.ui.screens.guidelist.GuideListPanel

private data class GuideSidebarItem(
    val label: String,
    val icon: ImageVector,
)

private val guideSidebarItems = listOf(
    GuideSidebarItem("导游列表", Icons.Filled.FormatListBulleted),
    GuideSidebarItem("城市景点筛选", Icons.Filled.FilterList),
    GuideSidebarItem("订单委托", Icons.Filled.Assignment),
    GuideSidebarItem("导游评价圈", Icons.Filled.RateReview),
    GuideSidebarItem("直播专区", Icons.Filled.LiveTv),
)

@Composable
fun FindGuideScreen(contentPadding: PaddingValues) {
    var selectedIndex by remember { mutableIntStateOf(0) }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .background(MaterialTheme.colorScheme.background),
    ) {
        GuideCategorySidebar(
            items = guideSidebarItems,
            selectedIndex = selectedIndex,
            onSelect = { selectedIndex = it },
            modifier = Modifier
                .width(92.dp)
                .fillMaxHeight(),
        )

        HorizontalDivider(
            modifier = Modifier
                .fillMaxHeight()
                .width(1.dp),
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.surface),
        ) {
            GuideCategoryContent(
                item = guideSidebarItems[selectedIndex],
                index = selectedIndex,
            )
        }
    }
}

@Composable
private fun GuideCategorySidebar(
    items: List<GuideSidebarItem>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val track = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
    val selectedBg = MaterialTheme.colorScheme.surface
    val accent = MaterialTheme.colorScheme.primary

    Surface(
        modifier = modifier,
        color = track,
        tonalElevation = 0.dp,
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            itemsIndexed(items) { index, item ->
                val selected = index == selectedIndex
                Row(
                    modifier = Modifier
                        .padding(horizontal = 6.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (selected) selectedBg else Color.Transparent)
                        .clickable { onSelect(index) }
                        .padding(vertical = 12.dp, horizontal = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (selected) {
                        Box(
                            modifier = Modifier
                                .width(3.dp)
                                .height(36.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(accent),
                        )
                        Spacer(Modifier.width(6.dp))
                    } else {
                        Spacer(Modifier.width(9.dp))
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f),
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            modifier = Modifier.size(22.dp),
                            tint = if (selected) accent else MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = item.label,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                            ),
                            color = if (selected) accent else MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            lineHeight = MaterialTheme.typography.labelSmall.lineHeight,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GuideCategoryContent(
    item: GuideSidebarItem,
    index: Int,
) {
    if (index == 0) {
        GuideListPanel(Modifier.fillMaxSize())
        return
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Text(
            text = item.label,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = guideCategorySubtitle(index),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(16.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
            ),
            shape = RoundedCornerShape(12.dp),
        ) {
            Column(Modifier.padding(16.dp)) {
                Text(
                    text = guideCategoryPlaceholderTitle(index),
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Medium),
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = guideCategoryPlaceholderBody(index),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

private fun guideCategorySubtitle(index: Int): String = when (index) {
    0 -> "浏览认证导游、在线状态与服务标签。"
    1 -> "按城市与景点组合筛选，快速匹配行程。"
    2 -> "发布需求、跟进报价与签约进度。"
    3 -> "真实评价与游记式反馈，帮助你选择导游。"
    4 -> "观看导游直播讲解与目的地实况。"
    else -> ""
}

private fun guideCategoryPlaceholderTitle(index: Int): String = when (index) {
    0 -> "导游列表（占位）"
    1 -> "城市景点筛选（占位）"
    2 -> "订单委托（占位）"
    3 -> "导游评价圈（占位）"
    4 -> "直播专区（占位）"
    else -> "内容"
}

private fun guideCategoryPlaceholderBody(index: Int): String = when (index) {
    0 -> "后续接入导游卡片列表、排序与收藏。"
    1 -> "后续接入城市、区县与景点多级筛选。"
    2 -> "后续接入委托单创建、状态与消息提醒。"
    3 -> "后续接入评价流、话题与互动。"
    4 -> "后续接入直播间列表与预约入口。"
    else -> ""
}
