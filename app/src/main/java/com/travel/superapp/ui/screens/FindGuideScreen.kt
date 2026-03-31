package com.travel.superapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import java.util.Locale

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
            ScrollableGuideCategoryContent(
                selectedIndex = selectedIndex,
            ) {
                GuideCategoryContent(
                    item = guideSidebarItems[selectedIndex],
                    index = selectedIndex,
                )
            }
        }
    }
}

@Composable
private fun ScrollableGuideCategoryContent(
    selectedIndex: Int,
    content: @Composable () -> Unit,
) {
    if (selectedIndex == 0 || selectedIndex > 4) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            content()
        }
    } else {
        Box(modifier = Modifier.fillMaxSize()) {
            content()
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
private fun GuideListContent() {
    val guides = remember { demoGuidesGuides() }
    var findMode by remember { mutableStateOf(GuideFindModeGuides.ByRegion) }
    var sortGeneral by remember { mutableStateOf(GuideSortGeneralGuides.DefaultComposite) }

    var province by remember { mutableStateOf<String?>(null) }
    var city by remember { mutableStateOf<String?>(null) }
    var district by remember { mutableStateOf<String?>(null) }

    var distanceTier by remember { mutableStateOf(DistanceTierGuides.M100) }

    val userLocation = remember { MockUserLocationHangzhouGuides }

    val visible = guides.filteredAndSortedGuides(
        mode = findMode,
        user = userLocation,
        regionProvince = province,
        regionCity = city,
        regionDistrict = district,
        distanceTier = distanceTier,
        sortGeneral = sortGeneral,
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
    ) {
        Text(
            text = "导游列表",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "定位说明：尚未接入 GPS，当前使用模拟坐标（杭州附近）用于距离与好评筛选。",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            FilterChipGuides(
                selected = findMode == GuideFindModeGuides.ByRegion,
                onClick = { findMode = GuideFindModeGuides.ByRegion },
                label = { Text("按地区") },
            )
            FilterChipGuides(
                selected = findMode == GuideFindModeGuides.ByDistance,
                onClick = { findMode = GuideFindModeGuides.ByDistance },
                label = { Text("按距离") },
            )
            FilterChipGuides(
                selected = findMode == GuideFindModeGuides.ByGoodRating,
                onClick = { findMode = GuideFindModeGuides.ByGoodRating },
                label = { Text("按好评") },
            )
        }

        Spacer(Modifier.height(12.dp))

        when (findMode) {
            GuideFindModeGuides.ByRegion -> {
                RegionSelectorsGuides(
                    province = province,
                    city = city,
                    district = district,
                    onProvinceChange = {
                        province = it
                        city = null
                        district = null
                    },
                    onCityChange = {
                        city = it
                        district = null
                    },
                    onDistrictChange = { district = it },
                )
            }
            GuideFindModeGuides.ByDistance,
            GuideFindModeGuides.ByGoodRating,
            -> {
                DistanceTierRowGuides(
                    selected = distanceTier,
                    onSelect = { distanceTier = it },
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        Text(
            text = "排序",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(6.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (findMode == GuideFindModeGuides.ByGoodRating) {
                // 按好评模式使用固定排序：星级优先，次注重价格便宜
            } else {
                FilterChipGuides(
                    selected = sortGeneral == GuideSortGeneralGuides.DefaultComposite,
                    onClick = { sortGeneral = GuideSortGeneralGuides.DefaultComposite },
                    label = { Text("默认") },
                )
                FilterChipGuides(
                    selected = sortGeneral == GuideSortGeneralGuides.ByRating,
                    onClick = { sortGeneral = GuideSortGeneralGuides.ByRating },
                    label = { Text("评分") },
                )
                FilterChipGuides(
                    selected = sortGeneral == GuideSortGeneralGuides.ByPrice,
                    onClick = { sortGeneral = GuideSortGeneralGuides.ByPrice },
                    label = { Text("价格") },
                )
            }
        }

        Spacer(Modifier.height(12.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
        Spacer(Modifier.height(8.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            visible.forEach { guide ->
                GuideCardGuides(
                    guide = guide,
                    userLocation = userLocation,
                    showDistance = findMode == GuideFindModeGuides.ByDistance ||
                        findMode == GuideFindModeGuides.ByGoodRating,
                )
            }
        }
        Spacer(Modifier.height(16.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterChipGuides(
    selected: Boolean,
    onClick: () -> Unit,
    label: @Composable () -> Unit,
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = label,
    )
}

@Composable
private fun GuideCategoryContent(
    item: GuideSidebarItem,
    index: Int,
) {
    when (index) {
        0 -> GuideListContent()
        1 -> CityAttractionsScreen()
        2 -> OrderDelegationScreen()
        3 -> GuideReviewCommunityScreen()
        4 -> LiveZoneScreen()
        else -> {
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

// ==================== 导游列表数据模型和组件 ====================

private val AvatarWidthGuides: androidx.compose.ui.unit.Dp = 100.dp
private val AvatarHeightGuides: androidx.compose.ui.unit.Dp = 175.dp

enum class GuideFindModeGuides {
    ByRegion,
    ByDistance,
    ByGoodRating,
}

enum class DistanceTierGuides(val label: String, val meters: Int) {
    M100("100m内", 100),
    M200("200m内", 200),
    M500("500m内", 500),
    KM1("1km内", 1_000),
    KM2("2km内", 2_000),
}

enum class GuideSortGeneralGuides {
    DefaultComposite,
    ByRating,
    ByPrice,
}

private data class GeoPointGuides(val latitude: Double, val longitude: Double)

private data class GuideGuides(
    val id: String,
    val name: String,
    val cityDisplay: String,
    val province: String,
    val city: String,
    val district: String,
    val latitude: Double,
    val longitude: Double,
    val rating: Double,
    val priceYuanPerDay: Int,
    val priceLabel: String,
)

private val MockUserLocationHangzhouGuides = GeoPointGuides(30.2741, 120.1551)

private fun haversineMetersGuides(a: GeoPointGuides, b: GeoPointGuides): Double {
    val r = 6_371_000.0
    val p1 = Math.toRadians(a.latitude)
    val p2 = Math.toRadians(b.latitude)
    val dLat = Math.toRadians(b.latitude - a.latitude)
    val dLon = Math.toRadians(b.longitude - a.longitude)
    val h = kotlin.math.sin(dLat / 2) * kotlin.math.sin(dLat / 2) +
        kotlin.math.cos(p1) * kotlin.math.cos(p2) * kotlin.math.sin(dLon / 2) * kotlin.math.sin(dLon / 2)
    val c = 2 * kotlin.math.asin(kotlin.math.sqrt(h.coerceIn(0.0, 1.0)))
    return r * c
}

private fun distanceToUserMetersGuides(guide: GuideGuides, user: GeoPointGuides): Double =
    haversineMetersGuides(user, GeoPointGuides(guide.latitude, guide.longitude))

private fun compositeSortKeyGuides(guide: GuideGuides): Double =
    guide.rating * 1_000.0 - guide.priceYuanPerDay / 20.0

private fun GuideGuides.matchesRegion(province: String?, city: String?, district: String?): Boolean {
    if (province != null && province.isNotBlank() && province != this.province) return false
    if (city != null && city.isNotBlank() && city != this.city) return false
    if (district != null && district.isNotBlank() && district != this.district) return false
    return true
}

private fun List<GuideGuides>.filteredAndSortedGuides(
    mode: GuideFindModeGuides,
    user: GeoPointGuides,
    regionProvince: String?,
    regionCity: String?,
    regionDistrict: String?,
    distanceTier: DistanceTierGuides,
    sortGeneral: GuideSortGeneralGuides,
): List<GuideGuides> {
    val base = when (mode) {
        GuideFindModeGuides.ByRegion -> filter { it.matchesRegion(regionProvince, regionCity, regionDistrict) }
        GuideFindModeGuides.ByDistance -> filter { distanceToUserMetersGuides(it, user) <= distanceTier.meters.toDouble() }
        GuideFindModeGuides.ByGoodRating -> filter { distanceToUserMetersGuides(it, user) <= distanceTier.meters.toDouble() }
    }
    return when (mode) {
        GuideFindModeGuides.ByGoodRating -> base.sortedWith(
            compareByDescending<GuideGuides> { it.rating }
                .thenBy { it.priceYuanPerDay }
                .thenBy { it.id },
        )
        else -> when (sortGeneral) {
            GuideSortGeneralGuides.DefaultComposite -> base.sortedWith(
                compareByDescending<GuideGuides> { compositeSortKeyGuides(it) }
                    .thenByDescending { it.rating }
                    .thenBy { it.priceYuanPerDay }
                    .thenBy { it.id },
            )
            GuideSortGeneralGuides.ByRating -> base.sortedWith(
                compareByDescending<GuideGuides> { it.rating }
                    .thenBy { it.priceYuanPerDay }
                    .thenBy { it.id },
            )
            GuideSortGeneralGuides.ByPrice -> base.sortedWith(
                compareBy<GuideGuides> { it.priceYuanPerDay }
                    .thenByDescending { it.rating }
                    .thenBy { it.id },
            )
        }
    }
}

private fun demoGuidesGuides(): List<GuideGuides> = listOf(
    GuideGuides("1", "林晓", "杭州市 · 西湖区", "浙江省", "杭州市", "西湖区", 30.2591, 120.1303, 4.9, 680, "¥680/天"),
    GuideGuides("2", "王磊", "杭州市 · 上城区", "浙江省", "杭州市", "上城区", 30.2446, 120.1804, 4.7, 520, "¥520/天"),
    GuideGuides("3", "陈悦", "杭州市 · 滨江区", "浙江省", "杭州市", "滨江区", 30.1876, 120.2103, 5.0, 880, "¥880/天"),
    GuideGuides("4", "赵敏", "宁波市 · 海曙区", "浙江省", "宁波市", "海曙区", 29.8730, 121.5500, 4.3, 460, "¥460/天"),
    GuideGuides("5", "周航", "南京市 · 玄武区", "江苏省", "南京市", "玄武区", 32.0486, 118.7975, 4.6, 590, "¥590/天"),
    GuideGuides("6", "孙宁", "苏州市 · 姑苏区", "江苏省", "苏州市", "姑苏区", 31.2989, 120.5853, 4.8, 720, "¥720/天"),
    GuideGuides("7", "马东", "上海市 · 黄浦区", "上海市", "市辖区", "黄浦区", 31.2304, 121.4737, 4.2, 980, "¥980/天"),
    GuideGuides("8", "韩雪", "北京市 · 朝阳区", "北京市", "市辖区", "朝阳区", 39.9219, 116.4432, 4.5, 850, "¥850/天"),
    GuideGuides("9", "沈清", "杭州市 · 西湖区", "浙江省", "杭州市", "西湖区", 30.27435, 120.15525, 4.8, 610, "¥610/天"),
    GuideGuides("10", "郑一", "杭州市 · 拱墅区", "浙江省", "杭州市", "拱墅区", 30.2751, 120.1546, 4.1, 430, "¥430/天"),
)

@Composable
private fun GuideCardGuides(
    guide: GuideGuides,
    userLocation: GeoPointGuides,
    showDistance: Boolean,
) {
    val dist = remember(guide.id, userLocation.latitude, userLocation.longitude) {
        distanceToUserMetersGuides(guide, userLocation)
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
                .padding(12.dp),
            verticalAlignment = Alignment.Top,
        ) {
            GuideAvatarGuides(name = guide.name)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = guide.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = guide.cityDisplay,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StarRatingBarGuides(rating = guide.rating)
                    Text(
                        text = String.format(java.util.Locale.US, "%.1f", guide.rating),
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Medium),
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    text = guide.priceLabel,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.primary,
                )
                if (showDistance) {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "距你约 ${dist.toInt()} m",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun GuideAvatarGuides(name: String) {
    val initial = name.firstOrNull()?.toString().orEmpty()
    Box(
        modifier = Modifier
            .width(AvatarWidthGuides)
            .height(AvatarHeightGuides)
            .clip(RoundedCornerShape(12.dp))
            .background(
                androidx.compose.ui.graphics.Brush.verticalGradient(
                    listOf(MaterialTheme.colorScheme.primary.copy(alpha = 0.85f), MaterialTheme.colorScheme.tertiary.copy(alpha = 0.75f)),
                ),
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = initial,
            style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
            color = Color.White,
        )
    }
}

@Composable
private fun StarRatingBarGuides(rating: Double, starSize: androidx.compose.ui.unit.Dp = 18.dp) {
    Row(horizontalArrangement = Arrangement.spacedBy(2.dp), verticalAlignment = Alignment.CenterVertically) {
        repeat(5) { index ->
            val fill = (rating - index).coerceIn(0.0, 1.0)
            Box(modifier = Modifier.size(starSize), contentAlignment = Alignment.CenterStart) {
                Icon(
                    imageVector = Icons.Outlined.StarOutline,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f),
                )
                if (fill > 0.0) {
                    Box(
                        modifier = Modifier
                            .width((starSize.value * fill).dp)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(2.dp)),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = null,
                            modifier = Modifier.size(starSize),
                            tint = Color(0xFFFFB300),
                        )
                    }
                }
            }
        }
    }
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
private fun RegionSelectorsGuides(
    province: String?,
    city: String?,
    district: String?,
    onProvinceChange: (String?) -> Unit,
    onCityChange: (String?) -> Unit,
    onDistrictChange: (String?) -> Unit,
) {
    val regionTree = rememberChinaRegionsGuides()
    val provinceOptions = remember(regionTree) { buildList<String?> { add(null); addAll(regionTree.provinces) } }
    val cityOptions = remember(province, regionTree) {
        buildList<String?> {
            add(null)
            if (province != null) {
                regionTree.citiesForProvince[province]?.let { addAll(it) }
            }
        }
    }
    val districtOptions = remember(province, city, regionTree) {
        buildList<String?> {
            add(null)
            if (province != null && city != null) {
                regionTree.districtsForProvinceCity[city]?.let { addAll(it) }
            }
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "地区（默认均为「不限」；选市、区前请先选上一级）",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        OptionalDropdownGuides(label = "省", selected = province, options = provinceOptions, onSelect = onProvinceChange)
        OptionalDropdownGuides(label = "市", selected = city, options = cityOptions, onSelect = onCityChange)
        OptionalDropdownGuides(label = "区（县）", selected = district, options = districtOptions, onSelect = onDistrictChange)
    }
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
private fun OptionalDropdownGuides(
    label: String,
    selected: String?,
    options: List<String?>,
    onSelect: (String?) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val display = selected ?: "不限"

    Column(Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = display,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) { expanded = !expanded },
                ),
            singleLine = true,
        )
        if (expanded) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                tonalElevation = 2.dp,
                shadowElevation = 2.dp,
                color = MaterialTheme.colorScheme.surface,
            ) {
                androidx.compose.foundation.lazy.LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(options.size) { index ->
                        val opt = options[index]
                        val text = opt ?: "不限"
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelect(opt); expanded = false }
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(text = text, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DistanceTierRowGuides(selected: DistanceTierGuides, onSelect: (DistanceTierGuides) -> Unit) {
    Column {
        Text(
            text = "距离范围（相对当前模拟位置）",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            DistanceTierGuides.values().forEach { tier ->
                FilterChipGuides(selected = selected == tier, onClick = { onSelect(tier) }, label = { Text(tier.label) })
            }
        }
    }
}

// 简化的区域数据
private data class ChinaRegionsGuides(
    val provinces: List<String>,
    val citiesForProvince: Map<String, List<String>>,
    val districtsForProvinceCity: Map<String, List<String>>,
)

@Composable
private fun rememberChinaRegionsGuides(): ChinaRegionsGuides {
    return remember {
        ChinaRegionsGuides(
            provinces = listOf("浙江省", "江苏省", "上海市", "北京市", "广东省", "四川省"),
            citiesForProvince = mapOf(
                "浙江省" to listOf("杭州市", "宁波市", "温州市", "嘉兴市", "湖州市"),
                "江苏省" to listOf("南京市", "苏州市", "无锡市", "常州市", "南通市"),
                "上海市" to listOf("市辖区"),
                "北京市" to listOf("市辖区"),
                "广东省" to listOf("广州市", "深圳市", "珠海市", "东莞市"),
                "四川省" to listOf("成都市", "绵阳市", "德阳市"),
            ),
            districtsForProvinceCity = mapOf(
                "杭州市" to listOf("西湖区", "上城区", "拱墅区", "下城区", "江干区", "滨江区", "萧山区"),
                "宁波市" to listOf("海曙区", "江北区", "北仑区", "镇海区", "鄞州区"),
                "苏州市" to listOf("姑苏区", "虎丘区", "吴中区", "相城区", "工业园区"),
                "南京市" to listOf("玄武区", "秦淮区", "建邺区", "鼓楼区", "浦口区"),
            )
        )
    }
}

// ==================== 城市景点筛选模块 ====================

private data class Attraction(
    val id: String,
    val name: String,
    val city: String,
    val description: String,
    val popularity: Int, // 热度值
    val tags: List<String>, // 标签，如 "本周热门", "当季推荐", "小众宝藏"
    val imageUrl: String? = null,
)

private fun demoAttractions(): List<Attraction> = listOf(
    Attraction("1", "西湖", "杭州市", "杭州西湖以其秀丽的湖光山色和众多历史遗迹闻名于世。", 95, listOf("本周热门", "当季推荐")),
    Attraction("2", "灵隐寺", "杭州市", "千年古刹，佛教圣地，环境幽静。", 88, listOf("当季推荐")),
    Attraction("3", "雷峰塔", "杭州市", "位于西湖南岸，历史悠久的文化遗迹。", 82, listOf("小众宝藏")),
    Attraction("4", "千岛湖", "杭州市", "国家级风景名胜区，水质清澈，岛屿众多。", 90, listOf("本周热门")),
    Attraction("5", "宋城", "杭州市", "大型主题公园，展现宋代文化。", 85, listOf("当季推荐")),
    Attraction("6", "西溪湿地", "杭州市", "国家级湿地公园，自然生态良好。", 78, listOf("小众宝藏")),
    Attraction("7", "中山陵", "南京市", "孙中山先生的陵墓，建筑宏伟。", 92, listOf("本周热门")),
    Attraction("8", "夫子庙", "南京市", "历史文化街区，美食与古迹并存。", 87, listOf("当季推荐")),
    Attraction("9", "颐和园", "北京市", "皇家园林，山水相连，建筑精美。", 96, listOf("本周热门", "当季推荐")),
    Attraction("10", "长城", "北京市", "世界奇迹，蜿蜒万里，气势磅礴。", 98, listOf("本周热门")),
    Attraction("11", "故宫", "北京市", "明清皇宫，建筑群宏大，文物丰富。", 97, listOf("当季推荐")),
    Attraction("12", "外滩", "上海市", "黄浦江畔的标志性建筑群。", 89, listOf("本周热门")),
    Attraction("13", "东方明珠", "上海市", "电视塔，上海的地标建筑。", 84, listOf("当季推荐")),
    Attraction("14", "豫园", "上海市", "古典园林，上海老城厢的代表。", 80, listOf("小众宝藏")),
    Attraction("15", "黄山", "安徽省", "奇松、怪石、云海、温泉四大奇观。", 93, listOf("本周热门", "当季推荐")),
    Attraction("16", "九寨沟", "四川省", "彩池、瀑布、雪山，风景如画。", 94, listOf("本周热门")),
    Attraction("17", "张家界", "湖南省", "石林奇观，峰峦叠翠。", 91, listOf("当季推荐")),
    Attraction("18", "桂林山水", "广西壮族自治区", "山青水秀，风景甲天下。", 86, listOf("小众宝藏")),
    Attraction("19", "丽江古城", "云南省", "纳西族文化，建筑古朴。", 83, listOf("当季推荐")),
    Attraction("20", "三亚", "海南省", "热带海滨城市，阳光沙滩。", 88, listOf("本周热门")),
)

@Composable
private fun CityAttractionsScreen() {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCity by remember { mutableStateOf("杭州市") } // 模拟定位当前城市
    var selectedTag by remember { mutableStateOf<String?>(null) }
    var sortBy by remember { mutableStateOf("热度") } // 热度 or 名称

    val allAttractions = remember { demoAttractions() }
    val cities = remember { allAttractions.map { it.city }.distinct() }
    val tags = remember { listOf("本周热门", "当季推荐", "小众宝藏") }

    val filteredAttractions = remember(searchQuery, selectedCity, selectedTag, sortBy) {
        allAttractions
            .filter { it.city == selectedCity }
            .filter { selectedTag == null || it.tags.contains(selectedTag) }
            .filter { searchQuery.isBlank() || it.name.contains(searchQuery, ignoreCase = true) || it.description.contains(searchQuery, ignoreCase = true) }
            .sortedWith(
                when (sortBy) {
                    "热度" -> compareByDescending<Attraction> { it.popularity }
                    else -> compareBy { it.name }
                }
            )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Text(
            text = "城市景点筛选",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "自动定位当前城市：$selectedCity，展示TOP20热门景点。",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(16.dp))

        // 城市选择
        Text("选择城市", style = MaterialTheme.typography.labelLarge)
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            cities.forEach { city ->
                FilterChip(
                    selected = selectedCity == city,
                    onClick = { selectedCity = city },
                    label = { Text(city) },
                )
            }
        }
        Spacer(Modifier.height(16.dp))

        // 搜索框
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("智能搜索景点") },
            placeholder = { Text("输入景点名称或描述，支持模糊匹配") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )
        Spacer(Modifier.height(16.dp))

        // 标签筛选
        Text("景点热度排行榜", style = MaterialTheme.typography.labelLarge)
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            FilterChip(
                selected = selectedTag == null,
                onClick = { selectedTag = null },
                label = { Text("全部") },
            )
            tags.forEach { tag ->
                FilterChip(
                    selected = selectedTag == tag,
                    onClick = { selectedTag = tag },
                    label = { Text(tag) },
                )
            }
        }
        Spacer(Modifier.height(16.dp))

        // 排序
        Text("排序", style = MaterialTheme.typography.labelLarge)
        Spacer(Modifier.height(8.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            FilterChip(
                selected = sortBy == "热度",
                onClick = { sortBy = "热度" },
                label = { Text("热度") },
            )
            FilterChip(
                selected = sortBy == "名称",
                onClick = { sortBy = "名称" },
                label = { Text("名称") },
            )
        }
        Spacer(Modifier.height(16.dp))

        // 景点列表
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(filteredAttractions) { attraction ->
                AttractionCard(attraction)
            }
        }
    }
}

@Composable
private fun AttractionCard(attraction: Attraction) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = attraction.name,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = attraction.city,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = attraction.description,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = "热度: ${attraction.popularity}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
                attraction.tags.forEach { tag ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = when (tag) {
                            "本周热门" -> Color(0xFFFF5722)
                            "当季推荐" -> Color(0xFF4CAF50)
                            "小众宝藏" -> Color(0xFF9C27B0)
                            else -> MaterialTheme.colorScheme.secondary
                        },
                    ) {
                        Text(
                            text = tag,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        )
                    }
                }
            }
        }
    }
}

// ==================== 订单委托全流程模块 ====================

private data class OrderRequest(
    val id: String,
    val guideName: String,
    val date: String,
    val peopleCount: Int,
    val requirements: String,
    val status: OrderStatus,
    val price: Int? = null,
)

private enum class OrderStatus {
    Pending, InProgress, Completed, Reviewed
}

private fun demoOrders(): List<OrderRequest> = listOf(
    OrderRequest("1", "林晓", "2024-03-15", 2, "西湖一日游，包含导览和午餐", OrderStatus.Pending),
    OrderRequest("2", "王磊", "2024-03-16", 4, "千岛湖两日游，住宿安排", OrderStatus.InProgress, 1360),
    OrderRequest("3", "陈悦", "2024-03-10", 1, "灵隐寺参观", OrderStatus.Completed, 680),
    OrderRequest("4", "赵敏", "2024-03-08", 3, "夫子庙美食游", OrderStatus.Reviewed, 1380),
)

@Composable
private fun OrderDelegationScreen() {
    var selectedTab by remember { mutableStateOf(OrderStatus.Pending) }
    val orders = remember { demoOrders() }
    val filteredOrders = remember(selectedTab) { orders.filter { it.status == selectedTab } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Text(
            text = "订单委托全流程",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "需求提交：导游/日期/人数/个性化需求。智能匹配系统推送3-5个候选导游。",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(16.dp))

        // 状态标签
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OrderStatus.values().forEach { status ->
                FilterChip(
                    selected = selectedTab == status,
                    onClick = { selectedTab = status },
                    label = {
                        Text(
                            when (status) {
                                OrderStatus.Pending -> "待确认"
                                OrderStatus.InProgress -> "进行中"
                                OrderStatus.Completed -> "已完成"
                                OrderStatus.Reviewed -> "已评价"
                            }
                        )
                    },
                )
            }
        }
        Spacer(Modifier.height(16.dp))

        // 新建订单按钮
        Button(
            onClick = { /* TODO: 新建订单 */ },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("新建委托订单")
        }
        Spacer(Modifier.height(16.dp))

        // 订单列表
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(filteredOrders) { order ->
                OrderCard(order)
            }
        }
    }
}

@Composable
private fun OrderCard(order: OrderRequest) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "导游：${order.guideName}",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "日期：${order.date} | 人数：${order.peopleCount}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = order.requirements,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = when (order.status) {
                        OrderStatus.Pending -> Color(0xFFFF9800)
                        OrderStatus.InProgress -> Color(0xFF2196F3)
                        OrderStatus.Completed -> Color(0xFF4CAF50)
                        OrderStatus.Reviewed -> Color(0xFF9E9E9E)
                    },
                ) {
                    Text(
                        text = when (order.status) {
                            OrderStatus.Pending -> "待确认"
                            OrderStatus.InProgress -> "进行中"
                            OrderStatus.Completed -> "已完成"
                            OrderStatus.Reviewed -> "已评价"
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    )
                }
            }
            if (order.price != null) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "总价：¥${order.price}",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            Spacer(Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                when (order.status) {
                    OrderStatus.Pending -> {
                        Button(onClick = { /* 确认 */ }, modifier = Modifier.weight(1f)) { Text("确认") }
                        OutlinedButton(onClick = { /* 拒绝 */ }, modifier = Modifier.weight(1f)) { Text("拒绝") }
                    }
                    OrderStatus.InProgress -> {
                        Button(onClick = { /* 联系导游 */ }, modifier = Modifier.weight(1f)) { Text("联系导游") }
                    }
                    OrderStatus.Completed -> {
                        Button(onClick = { /* 评价 */ }, modifier = Modifier.weight(1f)) { Text("评价") }
                        Button(onClick = { /* 分享海报 */ }, modifier = Modifier.weight(1f)) { Text("分享海报") }
                    }
                    OrderStatus.Reviewed -> {
                        Text("已评价", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

// ==================== 导游评价圈社区模块 ====================

private data class ReviewPost(
    val id: String,
    val userName: String,
    val attraction: String,
    val content: String,
    val rating: Double,
    val tags: List<String>,
    val likes: Int,
    val comments: Int,
    val isOfficialReply: Boolean = false,
)

private fun demoReviewPosts(): List<ReviewPost> = listOf(
    ReviewPost("1", "旅行者A", "西湖", "西湖真的太美了！导游林晓讲解非常专业，带我们走了很多小路。", 5.0, listOf("专业", "耐心"), 25, 8),
    ReviewPost("2", "游客B", "灵隐寺", "寺庙环境很好，但人有点多。导游王磊建议的时间点很合适。", 4.5, listOf("准时", "知识丰富"), 18, 5),
    ReviewPost("3", "背包客C", "千岛湖", "这次旅行超棒！导游陈悦安排的路线很合理，风景美不胜收。", 4.8, listOf("路线规划", "热情"), 32, 12),
    ReviewPost("4", "家庭D", "中山陵", "导游赵敏很细心，照顾到了老人和小孩的需求。", 4.7, listOf("细心", "安全"), 15, 3),
    ReviewPost("5", "摄影师E", "黄山", "黄山的日出太震撼了！导游周航帮我们找了好几个拍摄点。", 5.0, listOf("摄影指导", "专业"), 40, 15, true),
)

@Composable
private fun GuideReviewCommunityScreen() {
    var selectedFilter by remember { mutableStateOf("最新") }
    var searchQuery by remember { mutableStateOf("") }
    val posts = remember { demoReviewPosts() }
    val filters = listOf("最新", "热门", "好评", "景区")

    val filteredPosts = remember(selectedFilter, searchQuery) {
        posts
            .filter { searchQuery.isBlank() || it.content.contains(searchQuery, ignoreCase = true) || it.attraction.contains(searchQuery, ignoreCase = true) }
            .sortedWith(
                when (selectedFilter) {
                    "最新" -> compareByDescending<ReviewPost> { it.id } // 模拟最新
                    "热门" -> compareByDescending { it.likes }
                    "好评" -> compareByDescending { it.rating }
                    "景区" -> compareBy { it.attraction }
                    else -> compareBy { it.id }
                }
            )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Text(
            text = "导游评价圈社区",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "UGC动态流，支持城市/景区+点赞/评论/星级/最新筛选。",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(16.dp))

        // 搜索框
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("搜索评价") },
            placeholder = { Text("输入景区或内容关键词") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )
        Spacer(Modifier.height(16.dp))

        // 筛选标签
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            filters.forEach { filter ->
                FilterChip(
                    selected = selectedFilter == filter,
                    onClick = { selectedFilter = filter },
                    label = { Text(filter) },
                )
            }
        }
        Spacer(Modifier.height(16.dp))

        // 发布按钮
        Button(
            onClick = { /* TODO: 发布动态 */ },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("发布图文评价")
        }
        Spacer(Modifier.height(16.dp))

        // 问答专区入口
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Filled.RateReview, contentDescription = null, modifier = Modifier.size(24.dp))
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("导游问答专区", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Medium))
                    Text("官方认证回复，解答您的疑问", style = MaterialTheme.typography.bodySmall)
                }
                Button(onClick = { /* 进入问答 */ }) { Text("进入") }
            }
        }
        Spacer(Modifier.height(16.dp))

        // 动态流
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(filteredPosts) { post ->
                ReviewPostCard(post)
            }
        }
    }
}

@Composable
private fun ReviewPostCard(post: ReviewPost) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${post.userName} · ${post.attraction}",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Medium),
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = post.content,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
                if (post.isOfficialReply) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primary,
                    ) {
                        Text(
                            text = "官方",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        )
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                StarRatingBarGuides(rating = post.rating, starSize = 16.dp)
                Text(
                    text = String.format(Locale.US, "%.1f", post.rating),
                    style = MaterialTheme.typography.labelMedium,
                )
                Spacer(Modifier.width(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Star, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Red)
                    Spacer(Modifier.width(4.dp))
                    Text("${post.likes}", style = MaterialTheme.typography.labelMedium)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.RateReview, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.width(4.dp))
                    Text("${post.comments}", style = MaterialTheme.typography.labelMedium)
                }
            }
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                post.tags.forEach { tag ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                    ) {
                        Text(
                            text = "#$tag",
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        )
                    }
                }
            }
        }
    }
}

// ==================== 直播专区模块 ====================

private data class LiveStream(
    val id: String,
    val guideName: String,
    val title: String,
    val attraction: String,
    val status: LiveStatus,
    val startTime: String,
    val viewers: Int,
    val hasReplay: Boolean = false,
    val hasBooking: Boolean = false,
)

private enum class LiveStatus {
    Upcoming, Live, Ended
}

private fun demoLiveStreams(): List<LiveStream> = listOf(
    LiveStream("1", "林晓", "西湖日出实景直播", "西湖", LiveStatus.Live, "10:00", 1250, hasReplay = true),
    LiveStream("2", "王磊", "灵隐寺晨钟直播", "灵隐寺", LiveStatus.Upcoming, "08:30", 0, hasBooking = true),
    LiveStream("3", "陈悦", "千岛湖游船体验", "千岛湖", LiveStatus.Ended, "14:00", 890, hasReplay = true),
    LiveStream("4", "赵敏", "中山陵历史讲解", "中山陵", LiveStatus.Upcoming, "09:00", 0, hasBooking = true),
    LiveStream("5", "周航", "黄山云海奇观", "黄山", LiveStatus.Live, "06:30", 2100, hasReplay = true),
)

@Composable
private fun LiveZoneScreen() {
    var selectedFilter by remember { mutableStateOf("全部") }
    val streams = remember { demoLiveStreams() }
    val filters = listOf("全部", "直播中", "预约", "回放")

    val filteredStreams = remember(selectedFilter) {
        when (selectedFilter) {
            "直播中" -> streams.filter { it.status == LiveStatus.Live }
            "预约" -> streams.filter { it.hasBooking }
            "回放" -> streams.filter { it.hasReplay }
            else -> streams
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Text(
            text = "直播专区",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "直播卡片展示主播/状态/开播时间。预约提醒+回放功能。",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(16.dp))

        // 筛选标签
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            filters.forEach { filter ->
                FilterChip(
                    selected = selectedFilter == filter,
                    onClick = { selectedFilter = filter },
                    label = { Text(filter) },
                )
            }
        }
        Spacer(Modifier.height(16.dp))

        // 直播带货橱窗
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("直播带货橱窗", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Medium))
                Spacer(Modifier.height(8.dp))
                Text("当地特产/门票/体验一键下单", style = MaterialTheme.typography.bodySmall)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { /* 特产 */ }) { Text("特产") }
                    Button(onClick = { /* 门票 */ }) { Text("门票") }
                    Button(onClick = { /* 体验 */ }) { Text("体验") }
                }
            }
        }
        Spacer(Modifier.height(16.dp))

        // 直播列表
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(filteredStreams) { stream ->
                LiveStreamCard(stream)
            }
        }
    }
}

@Composable
private fun LiveStreamCard(stream: LiveStream) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stream.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "${stream.guideName} · ${stream.attraction}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = when (stream.status) {
                                LiveStatus.Live -> Color(0xFFE91E63)
                                LiveStatus.Upcoming -> Color(0xFF2196F3)
                                LiveStatus.Ended -> Color(0xFF9E9E9E)
                            },
                        ) {
                            Text(
                                text = when (stream.status) {
                                    LiveStatus.Live -> "直播中"
                                    LiveStatus.Upcoming -> "即将开始"
                                    LiveStatus.Ended -> "已结束"
                                },
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            )
                        }
                        Text(
                            text = "开播时间：${stream.startTime}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        if (stream.status == LiveStatus.Live) {
                            Text(
                                text = "${stream.viewers}人观看",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }
                }
                // 模拟直播画面
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(MaterialTheme.colorScheme.primary.copy(alpha = 0.8f), MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f)),
                            ),
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    if (stream.status == LiveStatus.Live) {
                        Text("LIVE", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold), color = Color.White)
                    } else {
                        Text("预览", style = MaterialTheme.typography.labelMedium, color = Color.White)
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                if (stream.hasBooking && stream.status == LiveStatus.Upcoming) {
                    Button(onClick = { /* 预约提醒 */ }, modifier = Modifier.weight(1f)) { Text("预约提醒") }
                }
                if (stream.hasReplay && stream.status == LiveStatus.Ended) {
                    Button(onClick = { /* 观看回放 */ }, modifier = Modifier.weight(1f)) { Text("观看回放") }
                }
                if (stream.status == LiveStatus.Live) {
                    Button(onClick = { /* 进入直播 */ }, modifier = Modifier.weight(1f)) { Text("进入直播") }
                }
            }
        }
    }
}
