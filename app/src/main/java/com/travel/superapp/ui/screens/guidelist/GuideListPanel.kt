package com.travel.superapp.ui.screens.guidelist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import java.util.Locale

private val AvatarWidth: Dp = 100.dp
private val AvatarHeight: Dp = 175.dp

/** 省/市/区下拉选项列表最大高度，内部可滚动，避免条目过多无法浏览 */
private val DropdownListMaxHeight: Dp = 360.dp

@Composable
fun GuideListPanel(modifier: Modifier = Modifier) {
    val guides = remember { demoGuides() }
    var findMode by remember { mutableStateOf(GuideFindMode.ByRegion) }
    var sortGeneral by remember { mutableStateOf(GuideSortGeneral.DefaultComposite) }

    var province by remember { mutableStateOf<String?>(null) }
    var city by remember { mutableStateOf<String?>(null) }
    var district by remember { mutableStateOf<String?>(null) }

    var distanceTier by remember { mutableStateOf(DistanceTier.KM2) }

    val userLocation = remember { MockUserLocationHangzhou }

    val listState = rememberLazyListState()

    val visible = guides.filteredAndSorted(
        mode = findMode,
        user = userLocation,
        regionProvince = province,
        regionCity = city,
        regionDistrict = district,
        distanceTier = distanceTier,
        sortGeneral = sortGeneral,
    )

    LaunchedEffect(findMode, sortGeneral, province, city, district, distanceTier) {
        listState.scrollToItem(0)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
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
            FilterChip(
                selected = findMode == GuideFindMode.ByRegion,
                onClick = { findMode = GuideFindMode.ByRegion },
                label = { Text("按地区") },
            )
            FilterChip(
                selected = findMode == GuideFindMode.ByDistance,
                onClick = { findMode = GuideFindMode.ByDistance },
                label = { Text("按距离") },
            )
            FilterChip(
                selected = findMode == GuideFindMode.ByGoodRating,
                onClick = { findMode = GuideFindMode.ByGoodRating },
                label = { Text("按好评") },
            )
        }

        Spacer(Modifier.height(12.dp))

        when (findMode) {
            GuideFindMode.ByRegion -> {
                RegionSelectors(
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
            GuideFindMode.ByDistance,
            GuideFindMode.ByGoodRating,
            -> {
                DistanceTierRow(
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
            if (findMode == GuideFindMode.ByGoodRating) {
                // 按好评模式使用固定排序：星级优先，次注重价格便宜
            } else {
                FilterChip(
                    selected = sortGeneral == GuideSortGeneral.DefaultComposite,
                    onClick = { sortGeneral = GuideSortGeneral.DefaultComposite },
                    label = { Text("默认") },
                )
                FilterChip(
                    selected = sortGeneral == GuideSortGeneral.ByRating,
                    onClick = { sortGeneral = GuideSortGeneral.ByRating },
                    label = { Text("评分") },
                )
                FilterChip(
                    selected = sortGeneral == GuideSortGeneral.ByPrice,
                    onClick = { sortGeneral = GuideSortGeneral.ByPrice },
                    label = { Text("价格") },
                )
            }
        }

        Spacer(Modifier.height(12.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
        Spacer(Modifier.height(8.dp))

        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(
                visible,
                key = { it.id },
            ) { guide ->
                GuideCard(
                    guide = guide,
                    userLocation = userLocation,
                    showDistance = findMode == GuideFindMode.ByDistance ||
                        findMode == GuideFindMode.ByGoodRating,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RegionSelectors(
    province: String?,
    city: String?,
    district: String?,
    onProvinceChange: (String?) -> Unit,
    onCityChange: (String?) -> Unit,
    onDistrictChange: (String?) -> Unit,
) {
    val regionTree = rememberChinaRegions()
    val provinceOptions = remember(regionTree) {
        buildList<String?> {
            add(null)
            addAll(regionTree.provinces)
        }
    }
    val cityOptions = remember(province, regionTree) {
        buildList<String?> {
            add(null)
            if (province != null) {
                addAll(regionTree.citiesForProvince(province))
            }
        }
    }
    val districtOptions = remember(province, city, regionTree) {
        buildList<String?> {
            add(null)
            if (province != null && city != null) {
                addAll(regionTree.districtsForProvinceCity(province, city))
            }
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "地区（默认均为「不限」；选市、区前请先选上一级）",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        OptionalDropdown(
            label = "省",
            selected = province,
            options = provinceOptions,
            onSelect = onProvinceChange,
        )
        OptionalDropdown(
            label = "市",
            selected = city,
            options = cityOptions,
            onSelect = onCityChange,
        )
        OptionalDropdown(
            label = "区（县）",
            selected = district,
            options = districtOptions,
            onSelect = onDistrictChange,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OptionalDropdown(
    label: String,
    selected: String?,
    options: List<String?>,
    onSelect: (String?) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val display = selected ?: "不限"
    val dropdownListState = rememberLazyListState()

    // 不使用 ExposedDropdownMenu / ExposedDropdownMenuBox；用透明层接收点击，避免 TextField 吞掉鼠标/触摸事件导致无法展开
    Column(Modifier.fillMaxWidth()) {
        Box(Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = display,
                onValueChange = {},
                readOnly = true,
                label = { Text(label) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )
            Box(
                Modifier
                    .matchParentSize()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) { expanded = !expanded },
            )
        }
        if (expanded) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = DropdownListMaxHeight),
                shape = RoundedCornerShape(8.dp),
                tonalElevation = 2.dp,
                shadowElevation = 2.dp,
                color = MaterialTheme.colorScheme.surface,
            ) {
                LazyColumn(
                    state = dropdownListState,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    items(
                        count = options.size,
                        key = { index -> "${index}_${options[index] ?: "null"}" },
                    ) { index ->
                        val opt = options[index]
                        val text = opt ?: "不限"
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSelect(opt)
                                    expanded = false
                                }
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = text,
                                style = MaterialTheme.typography.bodyLarge,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DistanceTierRow(
    selected: DistanceTier,
    onSelect: (DistanceTier) -> Unit,
) {
    Column {
        Text(
            text = "距离范围（相对当前模拟位置）",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            DistanceTier.entries.forEach { tier ->
                FilterChip(
                    selected = selected == tier,
                    onClick = { onSelect(tier) },
                    label = { Text(tier.label) },
                )
            }
        }
    }
}

@Composable
private fun GuideCard(
    guide: Guide,
    userLocation: GeoPoint,
    showDistance: Boolean,
) {
    val dist = remember(guide.id, userLocation.latitude, userLocation.longitude) {
        distanceToUserMeters(guide, userLocation)
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
            GuideAvatar(
                name = guide.name,
                modifier = Modifier
                    .width(AvatarWidth)
                    .height(AvatarHeight),
            )
            Spacer(Modifier.width(12.dp))
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
            ) {
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    StarRatingBar(rating = guide.rating)
                    Text(
                        text = String.format(Locale.US, "%.1f", guide.rating),
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
private fun GuideAvatar(
    name: String,
    modifier: Modifier = Modifier,
) {
    val initial = name.firstOrNull()?.toString().orEmpty()
    val gradient = Brush.verticalGradient(
        listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.85f),
            MaterialTheme.colorScheme.tertiary.copy(alpha = 0.75f),
        ),
    )
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(gradient),
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
private fun StarRatingBar(
    rating: Double,
    starSize: Dp = 18.dp,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(2.dp), verticalAlignment = Alignment.CenterVertically) {
        repeat(5) { index ->
            val fill = (rating - index).coerceIn(0.0, 1.0)
            Box(
                modifier = Modifier.size(starSize),
                contentAlignment = Alignment.CenterStart,
            ) {
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
