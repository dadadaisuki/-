package com.travel.superapp.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalkModuleScreen(onBack: () -> Unit) {
    var tabIndex by remember { mutableStateOf(0) }
    Column(Modifier.fillMaxSize().padding(12.dp)) {
        TopAppBar(
            title = { Text("漫步") },
            navigationIcon = {
                IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, "返回") }
            },
        )
        Spacer(Modifier.height(8.dp))
        TabRow(selectedTabIndex = tabIndex) {
            listOf("主题路线", "步数挑战").forEachIndexed { index, label ->
                Tab(selected = tabIndex == index, onClick = { tabIndex = index }, text = { Text(label) })
            }
        }
        Spacer(Modifier.height(8.dp))
        when (tabIndex) {
            0 -> WalkThemeRouteTab()
            1 -> WalkStepChallengeTab()
        }
    }
}

@Composable
private fun WalkThemeRouteTab() {
    var query by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("全部") }
    var selectedRoute by remember { mutableStateOf<WalkRoute?>(null) }
    var runningRoute by remember { mutableStateOf<WalkRoute?>(null) }

    val categories = listOf("历史人文", "文艺探店", "夜景灯光", "美食地图", "亲子休闲")
    val routes = remember(query, selectedCategory) {
        demoWalkRoutes().filter {
            (query.isBlank() || it.city.contains(query) || it.name.contains(query)) &&
                (selectedCategory == "全部" || it.tags.contains(selectedCategory))
        }
    }

    if (runningRoute != null) {
        WalkNavPage(route = runningRoute!!, onStop = { runningRoute = null })
        return
    }

    Column(Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            placeholder = { Text("输入城市/区域") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )
        Spacer(Modifier.height(8.dp))
        Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            (listOf("全部") + categories).forEach { category ->
                FilterChip(
                    selected = selectedCategory == category,
                    onClick = { selectedCategory = category },
                    label = { Text(category) },
                )
            }
        }
        Spacer(Modifier.height(8.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
            items(routes) { route ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedRoute = route },
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Box(Modifier.height(120.dp).fillMaxWidth().background(Color.Gray.copy(alpha = 0.25f))) {
                            Text("封面图", Modifier.align(Alignment.Center))
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(route.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Text("${route.distanceKm}km | ${route.durationMinutes}分 | 打卡${route.checkpoints}点 | ${route.difficulty}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }

        selectedRoute?.let { route ->
            WalkRouteDetailDialog(route = route, onStart = { runningRoute = route }, onClose = { selectedRoute = null })
        }
    }
}

@Composable
private fun WalkRouteDetailDialog(route: WalkRoute, onStart: () -> Unit, onClose: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(8.dp),
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("${route.name} 详情", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Box(Modifier.height(160.dp).fillMaxWidth().background(Color.LightGray.copy(alpha = 0.3f))) {
                Text("地图轨迹预览", Modifier.align(Alignment.Center))
            }
            Spacer(Modifier.height(8.dp))
            Text("途经点:")
            route.points.forEach { point -> Text("- $point", style = MaterialTheme.typography.bodySmall) }
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onStart) { Text("开始漫步") }
                OutlinedButton(onClick = onClose) { Text("关闭") }
            }
        }
    }
}

@Composable
private fun WalkNavPage(route: WalkRoute, onStop: () -> Unit) {
    var currentSteps by remember { mutableStateOf(0) }
    var walkedKm by remember { mutableStateOf(0.0) }
    var nextCheckpointDistance by remember { mutableStateOf(0.5) }

    Column(Modifier.fillMaxSize().padding(12.dp)) {
        Text("巡游中：${route.name}", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(16.dp))
        Text("当前步数: ${currentSteps}", style = MaterialTheme.typography.headlineMedium)
        Text("已走距离: ${String.format("%.1f", walkedKm)}km", style = MaterialTheme.typography.titleMedium)
        Text("下一打卡点距离: ${String.format("%.1f", nextCheckpointDistance)}km", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { currentSteps += 200; walkedKm += 0.15; nextCheckpointDistance = kotlin.math.max(0.0, nextCheckpointDistance - 0.15) }) { Text("步数+200") }
            Button(onClick = onStop) { Text("结束") }
        }
        Spacer(Modifier.height(8.dp))
        Text("语音播放控制", style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun WalkStepChallengeTab() {
    var target by remember { mutableStateOf(8000) }
    var todaySteps by remember { mutableStateOf(4300) }
    var showPicker by remember { mutableStateOf(false) }
    var selectedBadge by remember { mutableStateOf<Badge?>(null) }

    val days = remember { listOf(5600, 8200, 7800, 10200, 6600, 7300, todaySteps) }
    val badges = remember {
        listOf(
            Badge("城市行者", "步行3天达八千步", 5600, 8000),
            Badge("夜景达人", "夜走5次", 2, 5),
            Badge("亲子先锋", "亲子走过2个公园", 1, 2),
        )
    }

    Column(Modifier.fillMaxSize().padding(12.dp)) {
        Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
            Column(Modifier.padding(16.dp)) {
                Text("今日步数", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("${todaySteps}", style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.ExtraBold)
                Spacer(Modifier.height(8.dp))
                LinearProgressIndicator(progress = todaySteps / target.toFloat(), modifier = Modifier.fillMaxWidth())
                Text("目标: $target 步", style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(4.dp))
                Button(onClick = { showPicker = true }) { Text("修改目标") }
            }
        }
        Spacer(Modifier.height(12.dp))
        Text("本周趋势", fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            days.forEachIndexed { idx, step ->
                val reached = step >= target
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(Modifier.height((step / 400f).coerceAtLeast(10f).dp).width(20.dp).background(if (reached) Color(0xFF4CAF50) else Color.Gray))
                    Text("周${idx + 1}", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        Text("城市徽章墙", fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(8.dp))
        LazyVerticalStaggeredGrid(columns = StaggeredGridCells.Fixed(3), modifier = Modifier.height(180.dp)) {
            items(badges) { badge ->
                Card(
                    modifier = Modifier
                        .padding(4.dp)
                        .clickable { selectedBadge = badge },
                    colors = CardDefaults.cardColors(containerColor = if (badge.progress >= badge.goal) Color(0xFFBBDEFB) else Color(0xFFF5F5F5)),
                ) {
                    Column(Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(badge.name, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        Text(if (badge.progress >= badge.goal) "已解锁" else "未解锁", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }

        if (showPicker) {
            AlertDialog(onDismissRequest = { showPicker = false }, title = { Text("选择目标步数") }, text = {
                Column { listOf(4000, 6000, 8000, 10000, 15000).forEach { v ->
                    Text("$v 步", modifier = Modifier.clickable { target = v; showPicker = false }.padding(8.dp))
                } }
            }, confirmButton = { TextButton(onClick = { showPicker = false }) { Text("关闭") } })
        }

        selectedBadge?.let { badge ->
            AlertDialog(onDismissRequest = { selectedBadge = null }, title = { Text(badge.name) }, text = {
                Text("获取条件: ${badge.requirement}")
                Text("当前进度: ${badge.progress}/${badge.goal}")
            }, confirmButton = { TextButton(onClick = { selectedBadge = null }) { Text("确定") } })
        }
    }
}

data class WalkRoute(val name: String, val city: String, val distanceKm: Int, val durationMinutes: Int, val checkpoints: Int, val difficulty: String, val tags: List<String>, val points: List<String>)

data class Badge(val name: String, val requirement: String, val progress: Int, val goal: Int)

private fun demoWalkRoutes() = listOf(
    WalkRoute("西湖文化漫步", "杭州市", 5, 90, 8, "简单", listOf("历史人文", "美食地图"), listOf("断桥", "苏堤", "雷峰塔")),
    WalkRoute("老城文艺路线", "杭州市", 7, 120, 10, "中等", listOf("文艺探店", "亲子休闲"), listOf("河坊街", "南宋御街", "吴山市场")),
    WalkRoute("江湾夜景灯光", "上海市", 6, 80, 7, "中等", listOf("夜景灯光"), listOf("外滩", "滨江大道", "豫园夜市")),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BikeModuleScreen(onBack: () -> Unit) {
    var section by remember { mutableStateOf(0) }
    Column(Modifier.fillMaxSize().padding(12.dp)) {
        TopAppBar(
            title = { Text("骑行") },
            navigationIcon = {
                IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, "返回") }
            },
        )
        Spacer(Modifier.height(8.dp))
        TabRow(selectedTabIndex = section) {
            listOf("路线推荐", "租车点地图", "组队骑行").forEachIndexed { index, text ->
                Tab(selected = section == index, onClick = { section = index }, text = { Text(text) })
            }
        }
        Spacer(Modifier.height(8.dp))
        when (section) {
            0 -> BikeRouteRecommend()
            1 -> BikeRentPointMap()
            2 -> BikeTeamRide()
        }
    }
}

private data class BikeRoute(val name: String, val distance: Int, val duration: Int, val slope: Double, val scenery: Int, val level: String)

@Composable
private fun BikeRouteRecommend() {
    var difficulty by remember { mutableStateOf("休闲") }
    var distanceFilter by remember { mutableStateOf("5km内") }
    var selectedRoute by remember { mutableStateOf<BikeRoute?>(null) }
    val allRoutes = demoBikeRoutes()
    val routes = remember(difficulty, distanceFilter) {
        allRoutes.filter { route ->
            (difficulty == "全部" || route.level == difficulty) &&
                (distanceFilter == "全部" ||
                    (distanceFilter == "5km内" && route.distance <= 5) ||
                    (distanceFilter == "5-15km" && route.distance in 6..15) ||
                    (distanceFilter == "15km以上" && route.distance > 15))
        }
    }
    Column(Modifier.fillMaxSize()) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            FilterChip(selected = difficulty == "休闲", onClick = { difficulty = "休闲" }, label = { Text("休闲") })
            FilterChip(selected = difficulty == "进阶", onClick = { difficulty = "进阶" }, label = { Text("进阶") })
            FilterChip(selected = difficulty == "挑战", onClick = { difficulty = "挑战" }, label = { Text("挑战") })
        }
        Spacer(Modifier.height(4.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            listOf("5km内", "5-15km", "15km以上").forEach { label ->
                FilterChip(selected = distanceFilter == label, onClick = { distanceFilter = label }, label = { Text(label) })
            }
        }
        Spacer(Modifier.height(8.dp))
        // Placeholder map
        Box(Modifier.height(180.dp).fillMaxWidth().background(Color.Gray.copy(alpha = 0.2f))) {
            Text("地图视图（路线颜色区分）", Modifier.align(Alignment.Center))
        }
        Spacer(Modifier.height(8.dp))
        LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(routes) { route ->
                Card(Modifier.fillMaxWidth().clickable { selectedRoute = route }) {
                    Column(Modifier.padding(10.dp)) {
                        Text(route.name, fontWeight = FontWeight.SemiBold)
                        Text("${route.distance}km ｜ ${route.duration}min ｜ 坡度 ${route.slope}° ｜ 风景 ${route.scenery}星")
                    }
                }
            }
        }
        selectedRoute?.let { route ->
            Card(Modifier.fillMaxWidth().padding(8.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Column(Modifier.padding(12.dp)) {
                    Text("${route.name} 预览", fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(4.dp))
                    Text("全屏地图 + 海拔曲线 + 途经景点" )
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = {}) { Text("预览路线") }
                        Button(onClick = {}) { Text("开始骑行") }
                    }
                }
            }
        }
    }
}

@Composable
private fun BikeRentPointMap() {
    Column(Modifier.fillMaxSize()) {
        Box(Modifier.height(280.dp).fillMaxWidth().background(Color.LightGray.copy(alpha = 0.35f))) {
            Text("租车点地图（共享/景区/电动车标记）", Modifier.align(Alignment.Center))
        }
        Spacer(Modifier.height(8.dp))
        LazyColumn(Modifier.padding(horizontal = 12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(demoRentPoints()) { point ->
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(10.dp)) {
                        Text(point.name, fontWeight = FontWeight.SemiBold)
                        Text("${point.distance}m ｜ ${point.type} ｜ ${point.remain}车辆")
                        Text("${point.hours} ｜ ${point.fee}")
                        Spacer(Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(onClick = {}) { Text("导航前往") }
                            OutlinedButton(onClick = {}) { Text("扫码用车") }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BikeTeamRide() {
    var isCreateDialog by remember { mutableStateOf(false) }
    var selectedTeam by remember { mutableStateOf<TeamRide?>(null) }
    Column(Modifier.fillMaxSize().padding(12.dp)) {
        Button(onClick = { isCreateDialog = true }) { Text("发起组队") }
        Spacer(Modifier.height(8.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxSize()) {
            items(demoTeamRides()) { team ->
                Card(Modifier.fillMaxWidth().clickable { selectedTeam = team }) {
                    Column(Modifier.padding(10.dp)) {
                        Text(team.routeName, fontWeight = FontWeight.SemiBold)
                        Text("${team.distance}km ｜ ${team.departureTime} ｜ ${team.current}/${team.limit}")
                        Text("距离: ${team.distanceMeters}m")
                    }
                }
            }
        }

        if (isCreateDialog) {
            AlertDialog(onDismissRequest = { isCreateDialog = false }, title = { Text("发起组队") }, text = {
                Text("填写队伍信息界面（简化占位）")
            }, confirmButton = { TextButton(onClick = { isCreateDialog = false }) { Text("确定") } })
        }

        selectedTeam?.let { team ->
            AlertDialog(onDismissRequest = { selectedTeam = null }, title = { Text("队伍详情") }, text = {
                Column { Text("${team.routeName} ${team.departureTime}"); Text("成员: ${team.current}/${team.limit}") }
            }, confirmButton = { TextButton(onClick = { selectedTeam = null }) { Text("加入") } })
        }
    }
}

private fun demoBikeRoutes() = listOf(
    BikeRoute("湖滨休闲骑行", 4, 40, 2.1, 4, "休闲"),
    BikeRoute("林间进阶路线", 12, 70, 4.8, 5, "进阶"),
    BikeRoute("山地挑战赛", 22, 110, 8.6, 5, "挑战"),
)

private data class RentPoint(val name: String, val distance: Int, val type: String, val remain: Int, val hours: String, val fee: String)
private fun demoRentPoints() = listOf(
    RentPoint("共享单车点A", 150, "共享单车", 12, "8:00-22:00", "1元/30分钟"),
    RentPoint("景区租车点B", 560, "景区租车", 6, "7:00-19:00", "15元/小时"),
    RentPoint("电动车租赁C", 900, "电动车", 3, "24小时", "30元/小时"),
)

private data class TeamRide(val routeName: String, val distance: Int, val departureTime: String, val current: Int, val limit: Int, val distanceMeters: Int)
private fun demoTeamRides() = listOf(
    TeamRide("湖滨轻松骑行", 8, "08:30", 6, 12, 350),
    TeamRide("夜景城市骑", 10, "19:00", 3, 15, 1200),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarTourModuleScreen(onBack: () -> Unit) {
    var step by remember { mutableStateOf(0) }
    var from by remember { mutableStateOf("当前定位") }
    var to by remember { mutableStateOf("") }
    var days by remember { mutableStateOf(3) }
    var interests by remember { mutableStateOf(setOf<String>()) }
    var maxDrive by remember { mutableStateOf(4f) }
    var lodging by remember { mutableStateOf("舒适") }
    var avoidHighway by remember { mutableStateOf(false) }
    var routeResult by remember { mutableStateOf<CarPlanResult?>(null) }

    Column(Modifier.fillMaxSize().padding(12.dp)) {
        TopAppBar(title = { Text("乘车游") }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, "返回") } })
        Spacer(Modifier.height(8.dp))
        when {
            routeResult != null -> CarTourResultPage(result = routeResult!!, onBackHome = { routeResult = null })
            step == 0 -> {
                Column(Modifier.padding(8.dp)) {
                    OutlinedTextField(value = from, onValueChange = { from = it }, label = { Text("出发地") }, singleLine = true)
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(value = to, onValueChange = { to = it }, label = { Text("目的地") }, singleLine = true)
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(value = days.toString(), onValueChange = { days = it.toIntOrNull() ?: days }, label = { Text("出行天数") }, singleLine = true)
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { step = 1 }, modifier = Modifier.align(Alignment.End)) { Text("下一步") }
                }
            }
            step == 1 -> {
                Column(Modifier.padding(8.dp)) {
                    Text("兴趣选择（至少选一项）")
                    val options = listOf("自然风光", "历史古迹", "美食体验", "亲子娱乐", "购物休闲", "极限运动")
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        options.chunked(3).forEach { rowOptions ->
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                rowOptions.forEach { option ->
                                    FilterChip(selected = interests.contains(option), onClick = {
                                        interests = if (interests.contains(option)) interests - option else interests + option
                                    }, label = { Text(option) })
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = { if (interests.isNotEmpty()) step = 2 }) { Text("下一步") }
                }
            }
            step == 2 -> {
                Column(Modifier.padding(8.dp)) {
                    Text("单日最大驾驶时长: ${maxDrive.toInt()}小时")
                    Slider(value = maxDrive, onValueChange = { maxDrive = it }, valueRange = 2f..8f, steps = 6)
                    Spacer(Modifier.height(8.dp))
                    Text("住宿标准")
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("经济", "舒适", "豪华").forEach { level ->
                            FilterChip(selected = lodging == level, onClick = { lodging = level }, label = { Text(level) })
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("避开高速")
                        Switch(checked = avoidHighway, onCheckedChange = { avoidHighway = it })
                    }
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = { routeResult = CarPlanResult(totalDistance = 450, totalTime = 8, cities = 3, plans = demoDailyPlans()) }, modifier = Modifier.align(Alignment.End)) { Text("生成行程") }
                }
            }
        }
    }
}

data class CarPlanResult(val totalDistance: Int, val totalTime: Int, val cities: Int, val plans: List<DayPlan>)

data class DayPlan(val day: String, val driveTime: Int, val spots: List<String>, val stayArea: String)

@Composable
private fun CarTourResultPage(result: CarPlanResult, onBackHome: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(12.dp)) {
        Text("结果：总里程 ${result.totalDistance}km，总时长 ${result.totalTime}h，途经城市 ${result.cities}", fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(8.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(result.plans) { plan ->
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(10.dp)) {
                        Text(plan.day, fontWeight = FontWeight.SemiBold)
                        Text("驾驶 ${plan.driveTime}h，景点: ${plan.spots.joinToString()}，住宿: ${plan.stayArea}")
                    }
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = onBackHome) { Text("保存行程") }
            Button(onClick = {}) { Text("开始导航") }
        }
    }
}

private fun demoDailyPlans() = listOf(
    DayPlan("Day1", 3, listOf("景点A:1h", "景点B:1.5h"), "市中心"),
    DayPlan("Day2", 2, listOf("景点C:1h", "景点D:1h"), "古城区域"),
    DayPlan("Day3", 3, listOf("景点E:2h", "景点F:1h"), "度假区"),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupModuleScreen(onBack: () -> Unit) {
    var tabIndex by remember { mutableStateOf(0) }
    var selectedGroup by remember { mutableStateOf<GroupTrip?>(null) }
    var createDialog by remember { mutableStateOf(false) }

    val groups = remember {
        listOf(
            GroupTrip("海南三亚", "4月25日", 3, 2500, 8, 12, "张队长"),
            GroupTrip("云南大理", "5月5日", 5, 1800, 5, 10, "李队长"),
        )
    }

    Column(Modifier.fillMaxSize().padding(12.dp)) {
        TopAppBar(title = { Text("组团") }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, "返回") } })
        Spacer(Modifier.height(8.dp))
        TabRow(selectedTabIndex = tabIndex) {
            listOf("发现拼团", "我的团队").forEachIndexed { index, text ->
                Tab(selected = tabIndex == index, onClick = { tabIndex = index }, text = { Text(text) })
            }
        }
        Spacer(Modifier.height(8.dp))
        when (tabIndex) {
            0 -> {
                Column(Modifier.fillMaxSize()) {
                    OutlinedTextField(value = "", onValueChange = {}, label = { Text("搜索目的地") }, modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { createDialog = true }) { Text("发布拼团") }
                    Spacer(Modifier.height(8.dp))
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(groups) { group ->
                            Card(Modifier.fillMaxWidth().clickable { selectedGroup = group }) {
                                Column(Modifier.padding(10.dp)) {
                                    Text(group.destination, fontWeight = FontWeight.SemiBold)
                                    Text("出发 ${group.departureDate} | ${group.current}/${group.max} 人 | ${group.budget} 元")
                                }
                            }
                        }
                    }
                }
            }
            1 -> {
                Text("进行中团队列表", fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(groups) { group ->
                        Card(Modifier.fillMaxWidth().clickable { selectedGroup = group }) {
                            Column(Modifier.padding(10.dp)) {
                                Text(group.destination, fontWeight = FontWeight.SemiBold)
                                Text("出发 ${group.departureDate} | 成员 ${group.current}/${group.max}")
                            }
                        }
                    }
                }
            }
        }

        if (createDialog) {
            AlertDialog(onDismissRequest = { createDialog = false }, title = { Text("发布拼团") }, text = {Text("表单弹窗占位")}, confirmButton = {TextButton(onClick = {createDialog=false}){Text("确定")}})
        }

        selectedGroup?.let { group ->
            AlertDialog(onDismissRequest = { selectedGroup = null }, title = { Text("队伍详情") }, text = {
                Column { Text("路线: ${group.destination}"); Text("${group.departureDate} ${group.current}/${group.max}") }
            }, confirmButton = { TextButton(onClick = { selectedGroup = null }) { Text("申请加入") }})
        }
    }
}

data class GroupTrip(val destination: String, val departureDate: String, val days: Int, val budget: Int, val current: Int, val max: Int, val captain: String)
