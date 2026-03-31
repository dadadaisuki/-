package com.travel.superapp.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
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

// 根据标签筛选导游的专属页面
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)
@Composable
fun GuideWithTagsScreen(
    contentPadding: PaddingValues,
    preSelectedTags: List<String>,
    onBack: () -> Unit,
) {
    val guides = remember { demoGuidesGuides() }
    var findMode by remember { mutableStateOf(GuideFindModeGuides.ByTag) }
    var sortGeneral by remember { mutableStateOf(GuideSortGeneralGuides.DefaultComposite) }

    var province by remember { mutableStateOf<String?>(null) }
    var city by remember { mutableStateOf<String?>(null) }
    var district by remember { mutableStateOf<String?>(null) }

    var distanceTier by remember { mutableStateOf(DistanceTierGuides.M100) }

    // 筛选折叠状态
    var filterExpanded by remember { mutableStateOf(false) }

    // 新增筛选条件 - 预选标签
    var selectedGender by remember { mutableStateOf<GuideGenderGuides?>(null) }
    var selectedExperience by remember { mutableStateOf<GuideExperienceGuides?>(null) }
    var selectedTags by remember { mutableStateOf(setOf<String>()) }
    var searchQuery by remember { mutableStateOf("") }

    // 初始化预选标签
    LaunchedEffect(preSelectedTags) {
        selectedTags = preSelectedTags.toSet()
    }

    val userLocation = remember { MockUserLocationXianGuides }

    val visible = guides.filteredAndSortedGuides(
        mode = findMode,
        user = userLocation,
        regionProvince = province,
        regionCity = city,
        regionDistrict = district,
        distanceTier = distanceTier,
        sortGeneral = sortGeneral,
        gender = selectedGender,
        experience = selectedExperience,
        tags = selectedTags,
        searchQuery = searchQuery,
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("为您推荐导游") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 12.dp),
        ) {
            // 显示匹配标签
            if (preSelectedTags.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "为您匹配：",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    preSelectedTags.take(3).forEach { tag ->
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                        ) {
                            Text(
                                text = tag,
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
            }

            Text(
                text = "找到 ${visible.size} 位匹配的导游",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
            )
            Spacer(Modifier.height(12.dp))

            // 筛选按钮
            OutlinedButton(
                onClick = { filterExpanded = !filterExpanded },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(Icons.Filled.FilterList, contentDescription = "筛选", modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(if (filterExpanded) "收起筛选" else "展开筛选")
            }

            // 折叠的筛选区域
            AnimatedVisibility(visible = filterExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .verticalScroll(rememberScrollState())
                        .padding(vertical = 8.dp),
                ) {
                    // 搜索框
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("搜索导游姓名") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                    )
                    Spacer(Modifier.height(12.dp))

                    // 性别筛选
                    Text("性别", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChipGuides(selected = selectedGender == null, onClick = { selectedGender = null }, label = { Text("不限") })
                        FilterChipGuides(selected = selectedGender == GuideGenderGuides.Male, onClick = { selectedGender = GuideGenderGuides.Male }, label = { Text("男导游") })
                        FilterChipGuides(selected = selectedGender == GuideGenderGuides.Female, onClick = { selectedGender = GuideGenderGuides.Female }, label = { Text("女导游") })
                    }
                    Spacer(Modifier.height(12.dp))

                    // 经验筛选
                    Text("经验", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.horizontalScroll(rememberScrollState())) {
                        FilterChipGuides(selected = selectedExperience == null, onClick = { selectedExperience = null }, label = { Text("不限") })
                        GuideExperienceGuides.entries.forEach { exp ->
                            FilterChipGuides(selected = selectedExperience == exp, onClick = { selectedExperience = exp }, label = { Text(exp.label) })
                        }
                    }
                    Spacer(Modifier.height(12.dp))

                    // 标签筛选
                    Text("服务标签", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(6.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        allGuideTags.forEach { tag ->
                            FilterChipGuides(
                                selected = selectedTags.contains(tag),
                                onClick = {
                                    selectedTags = if (selectedTags.contains(tag)) selectedTags - tag else selectedTags + tag
                                },
                                label = { Text(tag) },
                            )
                        }
                    }
                    Spacer(Modifier.height(12.dp))

                    // 排序
                    Text("排序", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChipGuides(selected = sortGeneral == GuideSortGeneralGuides.DefaultComposite, onClick = { sortGeneral = GuideSortGeneralGuides.DefaultComposite }, label = { Text("默认") })
                        FilterChipGuides(selected = sortGeneral == GuideSortGeneralGuides.ByRating, onClick = { sortGeneral = GuideSortGeneralGuides.ByRating }, label = { Text("评分") })
                        FilterChipGuides(selected = sortGeneral == GuideSortGeneralGuides.ByPrice, onClick = { sortGeneral = GuideSortGeneralGuides.ByPrice }, label = { Text("价格") })
                    }
                    Spacer(Modifier.height(12.dp))
                }
            }

            Spacer(Modifier.height(8.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
            Spacer(Modifier.height(8.dp))

            // 导游列表
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp),
            ) {
                items(count = visible.size, key = { index -> visible[index].id }) { index ->
                    val guide = visible[index]
                    GuideCardGuides(
                        guide = guide,
                        userLocation = userLocation,
                        showDistance = true,
                    )
                }

                if (visible.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("暂无符合条件的导游", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(Modifier.height(8.dp))
                                Text("尝试调整筛选条件", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
                            }
                        }
                    }
                }
            }
        }
    }
}

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
    Box(modifier = Modifier.fillMaxSize()) {
        content()
    }
}

@Composable
private fun GuideCategorySidebar(
    items: List<GuideSidebarItem>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val selectedBg = MaterialTheme.colorScheme.surface
    val accent = MaterialTheme.colorScheme.primary

    Surface(
        modifier = modifier,
        tonalElevation = 0.dp,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF7EC17E).copy(alpha = 0.3f),
                            Color(0xFF06C4CC).copy(alpha = 0.2f),
                            Color(0xFF7EC17E).copy(alpha = 0.1f),
                        ),
                    ),
                ),
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
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                Color(0xFF7EC17E),
                                                Color(0xFF06C4CC),
                                            ),
                                        ),
                                    ),
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
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)
@Composable
private fun GuideListContent() {
    val guides = remember { demoGuidesGuides() }
    var findMode by remember { mutableStateOf(GuideFindModeGuides.ByRegion) }
    var sortGeneral by remember { mutableStateOf(GuideSortGeneralGuides.DefaultComposite) }

    var province by remember { mutableStateOf<String?>(null) }
    var city by remember { mutableStateOf<String?>(null) }
    var district by remember { mutableStateOf<String?>(null) }

    var distanceTier by remember { mutableStateOf(DistanceTierGuides.M100) }

    // 筛选折叠状态
    var filterExpanded by remember { mutableStateOf(false) }

    // 新增筛选条件
    var selectedGender by remember { mutableStateOf<GuideGenderGuides?>(null) }
    var selectedExperience by remember { mutableStateOf<GuideExperienceGuides?>(null) }
    var selectedTags by remember { mutableStateOf(setOf<String>()) }
    var searchQuery by remember { mutableStateOf("") }

    val userLocation = remember { MockUserLocationXianGuides }

    val visible = guides.filteredAndSortedGuides(
        mode = findMode,
        user = userLocation,
        regionProvince = province,
        regionCity = city,
        regionDistrict = district,
        distanceTier = distanceTier,
        sortGeneral = sortGeneral,
        gender = selectedGender,
        experience = selectedExperience,
        tags = selectedTags,
        searchQuery = searchQuery,
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp),
    ) {
        Text(
            text = "导游列表",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "尚未接入 GPS，当前使用模拟坐标（西安附近）用于距离与好评筛选。",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(12.dp))

        // 筛选按钮（折叠/展开）
        OutlinedButton(
            onClick = { filterExpanded = !filterExpanded },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Icon(
                imageVector = Icons.Filled.FilterList,
                contentDescription = "筛选",
                modifier = Modifier.size(18.dp),
            )
            Spacer(Modifier.width(8.dp))
            Text(if (filterExpanded) "收起筛选" else "展开筛选")
            Spacer(Modifier.weight(1f))
            Icon(
                imageVector = if (filterExpanded) Icons.Filled.ArrowDropDown else Icons.Filled.ArrowDropDown,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
            )
        }

        // 折叠的筛选区域（可滚动）
        AnimatedVisibility(visible = filterExpanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 8.dp),
            ) {
                // 搜索框
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("搜索导游") },
                    placeholder = { Text("输入导游姓名或标签") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                )

                Spacer(Modifier.height(12.dp))

                // 筛选方式下拉
                FilterModeDropdown(
                    selectedMode = findMode,
                    onModeSelected = { findMode = it },
                )

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
                    GuideFindModeGuides.ByTag,
                    -> {
                        DistanceTierRowGuides(
                            selected = distanceTier,
                            onSelect = { distanceTier = it },
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                // 性别筛选
                Text(
                    text = "性别偏好",
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
                    FilterChipGuides(
                        selected = selectedGender == null,
                        onClick = { selectedGender = null },
                        label = { Text("不限") },
                    )
                    GuideGenderGuides.entries.forEach { gender ->
                        FilterChipGuides(
                            selected = selectedGender == gender,
                            onClick = { selectedGender = gender },
                            label = { Text(gender.label) },
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                // 经验筛选
                Text(
                    text = "导游经验",
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
                    FilterChipGuides(
                        selected = selectedExperience == null,
                        onClick = { selectedExperience = null },
                        label = { Text("不限") },
                    )
                    GuideExperienceGuides.entries.forEach { exp ->
                        FilterChipGuides(
                            selected = selectedExperience == exp,
                            onClick = { selectedExperience = exp },
                            label = { Text(exp.label) },
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                // 标签筛选
                Text(
                    text = "导游标签（可多选）",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(6.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    allGuideTags.forEach { tag ->
                        FilterChipGuides(
                            selected = selectedTags.contains(tag),
                            onClick = {
                                selectedTags = if (selectedTags.contains(tag)) {
                                    selectedTags - tag
                                } else {
                                    selectedTags + tag
                                }
                            },
                            label = { Text(tag) },
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
                    if (findMode != GuideFindModeGuides.ByGoodRating) {
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
            }
        }

        Spacer(Modifier.height(8.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
        Spacer(Modifier.height(8.dp))

        // 导游列表 - 使用 LazyColumn 实现流畅滚动
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 16.dp),
        ) {
            items(
                count = visible.size,
                key = { index -> visible[index].id }
            ) { index ->
                val guide = visible[index]
                GuideCardGuides(
                    guide = guide,
                    userLocation = userLocation,
                    showDistance = findMode == GuideFindModeGuides.ByDistance ||
                            findMode == GuideFindModeGuides.ByGoodRating,
                )
            }
            
            // 空状态提示
            if (visible.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "暂无符合条件的导游",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = "尝试调整筛选条件",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterModeDropdown(
    selectedMode: GuideFindModeGuides,
    onModeSelected: (GuideFindModeGuides) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val label = when (selectedMode) {
        GuideFindModeGuides.ByRegion -> "按地区"
        GuideFindModeGuides.ByDistance -> "按距离"
        GuideFindModeGuides.ByGoodRating -> "按好评"
        GuideFindModeGuides.ByTag -> "按标签"
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
    ) {
        OutlinedTextField(
            value = label,
            onValueChange = {},
            readOnly = true,
            label = { Text("筛选方式") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            singleLine = true,
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            GuideFindModeGuides.entries.forEach { mode ->
                val itemLabel = when (mode) {
                    GuideFindModeGuides.ByRegion -> "按地区"
                    GuideFindModeGuides.ByDistance -> "按距离"
                    GuideFindModeGuides.ByGoodRating -> "按好评"
                    GuideFindModeGuides.ByTag -> "按标签"
                }
                DropdownMenuItem(
                    text = { Text(itemLabel) },
                    onClick = {
                        onModeSelected(mode)
                        expanded = false
                    },
                )
            }
        }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SimpleDropdownField(
    label: String,
    selected: String,
    options: List<String>,
    onSelect: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = {
                androidx.compose.material3.Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = null,
                    modifier = Modifier.clickable { expanded = !expanded },
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) { expanded = !expanded },
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
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 4.dp),
                ) {
                    items(options) { option ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelect(option); expanded = false }
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = option,
                                style = MaterialTheme.typography.bodyLarge,
                                color = if (option == selected)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.onSurface,
                                fontWeight = if (option == selected) FontWeight.Medium else FontWeight.Normal,
                            )
                        }
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
    ByTag,
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

enum class GuideGenderGuides(val label: String) {
    Male("男导游"),
    Female("女导游"),
}

enum class GuideExperienceGuides(val label: String, val years: Int) {
    New("1年内", 1),
    Junior("1-3年", 2),
    Senior("3-5年", 3),
    Expert("5年以上", 4),
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
    val age: Int, // 年龄
    val gender: GuideGenderGuides, // 性别
    val experience: GuideExperienceGuides, // 经验
    val tags: List<String>, // 标签：骑行、登山、摄影、美食、文化等
)

// 所有可选标签
val allGuideTags = listOf(
    "骑行专家", "登山向导", "摄影达人", "美食之旅", "文化深度",
    "城市观光", "自然风光", "历史古迹", "亲子活动", "极限运动",
    "夜间导览", "温泉之旅", "古镇探秘", "海滨度假", "摄影指导",
    "二次元达人", "小众宝藏"
)

private val MockUserLocationXianGuides = GeoPointGuides(34.3416, 108.9398)

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
    gender: GuideGenderGuides?,
    experience: GuideExperienceGuides?,
    tags: Set<String>,
    searchQuery: String,
): List<GuideGuides> {
    val base = when (mode) {
        GuideFindModeGuides.ByRegion -> filter { it.matchesRegion(regionProvince, regionCity, regionDistrict) }
        GuideFindModeGuides.ByDistance -> filter { distanceToUserMetersGuides(it, user) <= distanceTier.meters.toDouble() }
        GuideFindModeGuides.ByGoodRating -> filter { distanceToUserMetersGuides(it, user) <= distanceTier.meters.toDouble() }
        GuideFindModeGuides.ByTag -> this
    }.filter { guide ->
        // 性别筛选
        (gender == null || guide.gender == gender) &&
        // 经验筛选
        (experience == null || guide.experience == experience) &&
        // 标签筛选（至少包含一个选中的标签）
        (tags.isEmpty() || guide.tags.any { it in tags }) &&
        // 搜索筛选（搜索姓名或标签）
        (searchQuery.isBlank() || guide.name.contains(searchQuery, ignoreCase = true) || guide.tags.any { it.contains(searchQuery, ignoreCase = true) })
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
    GuideGuides("1", "刘明", "西安市 · 雁塔区", "陕西省", "西安市", "雁塔区", 34.2199, 108.9508, 4.9, 680, "¥680/天", 35, GuideGenderGuides.Male, GuideExperienceGuides.Senior, listOf("历史古迹", "文化深度", "摄影达人")),
    GuideGuides("2", "王芳", "西安市 · 碑林区", "陕西省", "西安市", "碑林区", 34.2568, 108.9436, 4.8, 580, "¥580/天", 30, GuideGenderGuides.Female, GuideExperienceGuides.Junior, listOf("城市观光", "美食之旅", "文化深度")),
    GuideGuides("3", "张伟", "西安市 · 莲湖区", "陕西省", "西安市", "莲湖区", 34.2655, 108.9325, 5.0, 880, "¥880/天", 38, GuideGenderGuides.Male, GuideExperienceGuides.Expert, listOf("历史古迹", "夜间导览", "摄影指导")),
    GuideGuides("4", "李娜", "西安市 · 新城区", "陕西省", "西安市", "新城区", 34.2685, 108.9672, 4.7, 520, "¥520/天", 28, GuideGenderGuides.Female, GuideExperienceGuides.Junior, listOf("亲子活动", "美食之旅", "古镇探秘")),
    GuideGuides("5", "赵强", "西安市 · 灞桥区", "陕西省", "西安市", "灞桥区", 34.2758, 109.0288, 4.6, 590, "¥590/天", 42, GuideGenderGuides.Male, GuideExperienceGuides.Expert, listOf("历史古迹", "文化深度", "骑行专家")),
    GuideGuides("6", "孙丽", "西安市 · 未央区", "陕西省", "西安市", "未央区", 34.2842, 108.9431, 4.8, 720, "¥720/天", 32, GuideGenderGuides.Female, GuideExperienceGuides.Senior, listOf("古镇探秘", "美食之旅", "摄影达人")),
    GuideGuides("7", "马东", "咸阳市 · 秦都区", "陕西省", "咸阳市", "秦都区", 34.3297, 108.7092, 4.5, 480, "¥480/天", 29, GuideGenderGuides.Male, GuideExperienceGuides.Senior, listOf("历史古迹", "文化深度", "自然风光")),
    GuideGuides("8", "韩雪", "渭南市 · 临渭区", "陕西省", "渭南市", "临渭区", 34.4975, 109.5099, 4.3, 450, "¥450/天", 26, GuideGenderGuides.Female, GuideExperienceGuides.New, listOf("亲子活动", "自然风光", "小众宝藏")),
    GuideGuides("9", "沈清", "西安市 · 长安区", "陕西省", "西安市", "长安区", 34.0873, 108.9395, 4.9, 650, "¥650/天", 33, GuideGenderGuides.Male, GuideExperienceGuides.Junior, listOf("骑行专家", "自然风光", "摄影指导")),
    GuideGuides("10", "郑一", "西安市 · 临潼区", "陕西省", "西安市", "临潼区", 34.3644, 109.2131, 4.8, 620, "¥620/天", 36, GuideGenderGuides.Male, GuideExperienceGuides.Senior, listOf("历史古迹", "文化深度", "夜间导览")),
    GuideGuides("11", "李娜", "宝鸡市 · 渭滨区", "陕西省", "宝鸡市", "渭滨区", 34.3618, 107.2370, 4.6, 550, "¥550/天", 31, GuideGenderGuides.Female, GuideExperienceGuides.Senior, listOf("自然风光", "古镇探秘", "二次元达人")),
    GuideGuides("12", "张伟", "延安市 · 宝塔区", "陕西省", "延安市", "宝塔区", 36.5853, 109.4897, 4.7, 580, "¥580/天", 35, GuideGenderGuides.Male, GuideExperienceGuides.Expert, listOf("历史古迹", "文化深度", "红色旅游")),
    GuideGuides("13", "吴芳", "汉中市 · 汉台区", "陕西省", "汉中市", "汉台区", 33.0678, 107.0230, 4.5, 480, "¥480/天", 27, GuideGenderGuides.Female, GuideExperienceGuides.Junior, listOf("自然风光", "古镇探秘", "美食之旅")),
    GuideGuides("14", "刘洋", "安康市 · 汉滨区", "陕西省", "安康市", "汉滨区", 32.6854, 109.0293, 4.4, 460, "¥460/天", 29, GuideGenderGuides.Male, GuideExperienceGuides.Senior, listOf("自然风光", "登山向导", "摄影达人")),
    GuideGuides("15", "黄丽", "西安市 · 雁塔区", "陕西省", "西安市", "雁塔区", 34.2188, 108.9520, 4.9, 780, "¥780/天", 34, GuideGenderGuides.Female, GuideExperienceGuides.Expert, listOf("历史古迹", "文化深度", "夜间导览")),
    GuideGuides("16", "杨帆", "西安市 · 碑林区", "陕西省", "西安市", "碑林区", 34.2520, 108.9400, 4.8, 700, "¥700/天", 33, GuideGenderGuides.Male, GuideExperienceGuides.Expert, listOf("历史古迹", "美食之旅", "摄影达人")),
    GuideGuides("17", "徐婷", "商洛市 · 商州区", "陕西省", "商洛市", "商州区", 33.8704, 109.9404, 4.7, 420, "¥420/天", 28, GuideGenderGuides.Female, GuideExperienceGuides.Junior, listOf("自然风光", "小众宝藏", "摄影指导")),
    GuideGuides("18", "赵强", "铜川市 · 耀州区", "陕西省", "铜川市", "耀州区", 34.9117, 108.9488, 4.5, 450, "¥450/天", 31, GuideGenderGuides.Male, GuideExperienceGuides.Senior, listOf("历史古迹", "文化深度", "自然风光")),
    GuideGuides("19", "王静", "榆林市 · 榆阳区", "陕西省", "榆林市", "榆阳区", 38.2852, 109.7348, 4.6, 480, "¥480/天", 30, GuideGenderGuides.Female, GuideExperienceGuides.Junior, listOf("自然风光", "摄影达人", "美食之旅")),
    GuideGuides("20", "陈龙", "西安市 · 莲湖区", "陕西省", "西安市", "莲湖区", 34.2700, 108.9300, 4.9, 750, "¥750/天", 37, GuideGenderGuides.Male, GuideExperienceGuides.Expert, listOf("历史古迹", "夜间导览", "摄影指导")),
    GuideGuides("21", "林婷", "西安市 · 新城区", "陕西省", "西安市", "新城区", 34.2700, 108.9650, 4.7, 580, "¥580/天", 29, GuideGenderGuides.Female, GuideExperienceGuides.Senior, listOf("亲子活动", "美食之旅", "城市观光")),
    GuideGuides("22", "周杰", "咸阳市 · 渭城区", "陕西省", "咸阳市", "渭城区", 34.3288, 108.6977, 4.6, 520, "¥520/天", 32, GuideGenderGuides.Male, GuideExperienceGuides.Senior, listOf("历史古迹", "文化深度", "古镇探秘")),
    GuideGuides("23", "吴倩", "西安市 · 长安区", "陕西省", "西安市", "长安区", 34.0900, 108.9400, 4.8, 620, "¥620/天", 31, GuideGenderGuides.Female, GuideExperienceGuides.Junior, listOf("自然风光", "骑行专家", "摄影达人")),
    GuideGuides("24", "孙浩", "渭南市 · 华州区", "陕西省", "渭南市", "华州区", 34.5153, 109.7711, 4.5, 440, "¥440/天", 35, GuideGenderGuides.Male, GuideExperienceGuides.Expert, listOf("自然风光", "登山向导", "摄影指导")),
    GuideGuides("25", "郑婷", "西安市 · 灞桥区", "陕西省", "西安市", "灞桥区", 34.2780, 109.0250, 4.7, 560, "¥560/天", 28, GuideGenderGuides.Female, GuideExperienceGuides.Junior, listOf("历史古迹", "文化深度", "美食之旅")),
)

@OptIn(ExperimentalLayoutApi::class)
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
            GuideAvatarGuides(name = guide.name, gender = guide.gender)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = guide.name,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = guide.priceLabel,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
                Spacer(Modifier.height(4.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = guide.gender.label,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = "｜",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = guide.experience.label,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = guide.cityDisplay,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
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
                // 标签展示
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    guide.tags.take(4).forEach { tag ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                        ) {
                            Text(
                                text = tag,
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            )
                        }
                    }
                    if (guide.tags.size > 4) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                        ) {
                            Text(
                                text = "+${guide.tags.size - 4}",
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            )
                        }
                    }
                }
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
private fun GuideAvatarGuides(name: String, gender: GuideGenderGuides) {
    val initial = name.firstOrNull()?.toString().orEmpty()
    val gradientColors = when (gender) {
        GuideGenderGuides.Male -> listOf(Color(0xFF1976D2), Color(0xFF64B5F6))
        GuideGenderGuides.Female -> listOf(Color(0xFFE91E63), Color(0xFFF48FB1))
    }
    Box(
        modifier = Modifier
            .width(AvatarWidthGuides)
            .height(AvatarHeightGuides)
            .clip(RoundedCornerShape(12.dp))
            .background(
                androidx.compose.ui.graphics.Brush.verticalGradient(gradientColors),
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
    Attraction("1", "秦始皇兵马俑", "西安市", "世界第八大奇迹，震撼的地下军阵。", 98, listOf("本周热门", "当季推荐")),
    Attraction("2", "大雁塔", "西安市", "唐代佛教建筑典范，玄奘译经之地。", 95, listOf("本周热门", "当季推荐")),
    Attraction("3", "大唐芙蓉园", "西安市", "盛唐文化主题公园，夜景璀璨。", 92, listOf("当季推荐")),
    Attraction("4", "西安城墙", "西安市", "中国现存最完整的古城墙。", 93, listOf("本周热门")),
    Attraction("5", "华清池", "西安市", "皇家温泉行宫，杨贵妃沐浴之地。", 90, listOf("当季推荐")),
    Attraction("6", "钟鼓楼", "西安市", "古城地标，晨钟暮鼓。", 88, listOf("小众宝藏")),
    Attraction("7", "大明宫国家遗址公园", "西安市", "大唐帝国的大朝正宫。", 89, listOf("当季推荐")),
    Attraction("8", "陕西历史博物馆", "西安市", "中国第一座大型现代化国家级博物馆。", 96, listOf("本周热门")),
    Attraction("9", "回民街", "西安市", "西安著名小吃街，回族美食汇聚。", 91, listOf("本周热门", "当季推荐")),
    Attraction("10", "大唐不夜城", "西安市", "盛唐文化步行街，灯光秀震撼。", 94, listOf("本周热门")),
    Attraction("11", "骊山", "西安市", "自然风光与历史文化并存。", 85, listOf("当季推荐")),
    Attraction("12", "乾陵", "咸阳市", "唐高宗与武则天的合葬墓。", 86, listOf("小众宝藏")),
    Attraction("13", "法门寺", "宝鸡市", "佛教圣地，供奉佛指舍利。", 87, listOf("当季推荐")),
    Attraction("14", "延安革命纪念馆", "延安市", "红色旅游圣地，中国革命圣地。", 84, listOf("小众宝藏")),
    Attraction("15", "华山", "渭南市", "奇险天下第一山，五岳之一。", 97, listOf("本周热门", "当季推荐")),
)

@Composable
private fun CityAttractionsScreen() {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCity by remember { mutableStateOf("西安市") } // 模拟定位当前城市
    var selectedTag by remember { mutableStateOf<String?>(null) }
    var sortBy by remember { mutableStateOf("热度") } // 热度 or 名称
    var filterExpanded by remember { mutableStateOf(false) } // 筛选折叠状态

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
        Spacer(Modifier.height(16.dp))

        // 筛选按钮（折叠/展开）
        OutlinedButton(
            onClick = { filterExpanded = !filterExpanded },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Icon(
                imageVector = Icons.Filled.FilterList,
                contentDescription = "筛选",
                modifier = Modifier.size(18.dp),
            )
            Spacer(Modifier.width(8.dp))
            Text(if (filterExpanded) "收起筛选" else "展开筛选")
        }

        // 折叠的筛选区域
        AnimatedVisibility(visible = filterExpanded) {
            Column {
                Spacer(Modifier.height(12.dp))

                // 城市选择下拉
                SimpleDropdownField(
                    label = "选择城市",
                    selected = selectedCity,
                    options = cities,
                    onSelect = { selectedCity = it },
                )

                Spacer(Modifier.height(12.dp))

                // 搜索框
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("智能搜索景点") },
                    placeholder = { Text("输入景点名称或描述，支持模糊匹配") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )
                Spacer(Modifier.height(12.dp))

                // 标签下拉
                SimpleDropdownField(
                    label = "景点标签",
                    selected = selectedTag ?: "全部",
                    options = listOf("全部") + tags,
                    onSelect = { selectedTag = if (it == "全部") null else it },
                )

                Spacer(Modifier.height(12.dp))

                // 排序下拉
                SimpleDropdownField(
                    label = "排序方式",
                    selected = sortBy,
                    options = listOf("热度", "名称"),
                    onSelect = { sortBy = it },
                )

                Spacer(Modifier.height(16.dp))
            }
        }

        Spacer(Modifier.height(8.dp))

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
    Pending, InProgress, Completed, Reviewed;

    val label: String
        get() = when (this) {
            Pending -> "待确认"
            InProgress -> "进行中"
            Completed -> "已完成"
            Reviewed -> "已评价"
        }
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
    var filterExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Text(
            text = "订单委托全流程",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
        )
        Spacer(Modifier.height(16.dp))

        // 筛选按钮（折叠/展开）
        OutlinedButton(
            onClick = { filterExpanded = !filterExpanded },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Icon(
                imageVector = Icons.Filled.FilterList,
                contentDescription = "筛选",
                modifier = Modifier.size(18.dp),
            )
            Spacer(Modifier.width(8.dp))
            Text(if (filterExpanded) "收起筛选" else "展开筛选")
        }

        // 折叠的筛选区域
        AnimatedVisibility(visible = filterExpanded) {
            Column {
                Spacer(Modifier.height(12.dp))

                // 状态标签下拉
                SimpleDropdownField(
                    label = "订单状态",
                    selected = selectedTab.label,
                    options = OrderStatus.entries.map { it.label },
                    onSelect = { selectedTab = OrderStatus.entries.first { s -> s.label == it } },
                )

                Spacer(Modifier.height(16.dp))
            }
        }

        Spacer(Modifier.height(8.dp))

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
    var filterExpanded by remember { mutableStateOf(false) }
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
        Spacer(Modifier.height(16.dp))

        // 筛选按钮（折叠/展开）
        OutlinedButton(
            onClick = { filterExpanded = !filterExpanded },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Icon(
                imageVector = Icons.Filled.FilterList,
                contentDescription = "筛选",
                modifier = Modifier.size(18.dp),
            )
            Spacer(Modifier.width(8.dp))
            Text(if (filterExpanded) "收起筛选" else "展开筛选")
        }

        // 折叠的筛选区域
        AnimatedVisibility(visible = filterExpanded) {
            Column {
                Spacer(Modifier.height(12.dp))

                // 搜索框
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("搜索评价") },
                    placeholder = { Text("输入景区或内容关键词") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )
                Spacer(Modifier.height(12.dp))

                // 筛选下拉
                SimpleDropdownField(
                    label = "评价筛选",
                    selected = selectedFilter,
                    options = filters,
                    onSelect = { selectedFilter = it },
                )

                Spacer(Modifier.height(16.dp))
            }
        }

        Spacer(Modifier.height(8.dp))

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
    var filterExpanded by remember { mutableStateOf(false) }
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
        Spacer(Modifier.height(16.dp))

        // 筛选按钮（折叠/展开）
        OutlinedButton(
            onClick = { filterExpanded = !filterExpanded },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Icon(
                imageVector = Icons.Filled.FilterList,
                contentDescription = "筛选",
                modifier = Modifier.size(18.dp),
            )
            Spacer(Modifier.width(8.dp))
            Text(if (filterExpanded) "收起筛选" else "展开筛选")
        }

        // 折叠的筛选区域
        AnimatedVisibility(visible = filterExpanded) {
            Column {
                Spacer(Modifier.height(12.dp))

                // 筛选下拉
                SimpleDropdownField(
                    label = "直播筛选",
                    selected = selectedFilter,
                    options = filters,
                    onSelect = { selectedFilter = it },
                )

                Spacer(Modifier.height(16.dp))
            }
        }

        Spacer(Modifier.height(8.dp))

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
