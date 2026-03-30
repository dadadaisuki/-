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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
                .background(MaterialTheme.colorScheme.surface)
                .verticalScroll(rememberScrollState()),
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
            ),
        )
    }
}
