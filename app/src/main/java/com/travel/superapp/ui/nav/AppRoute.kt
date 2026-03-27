package com.travel.superapp.ui.nav

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.ui.graphics.vector.ImageVector

sealed class AppRoute(
    val route: String,
) {
    data object Home : AppRoute("tab/home")
    data object FindGuide : AppRoute("tab/find_guide")
    data object Ai : AppRoute("tab/ai")
    data object Post : AppRoute("tab/post")
    data object Mine : AppRoute("tab/mine")

    companion object {
        val tabRoutes: List<TabItem> = listOf(
            TabItem(Home.route, "主页", Icons.Filled.Home),
            TabItem(FindGuide.route, "找导游", Icons.Filled.PersonSearch),
            TabItem(Post.route, "投稿", Icons.Filled.EditNote),
            TabItem(Mine.route, "我的", Icons.Filled.AccountCircle),
        )

        val entryRoutes: List<String> = listOf(
            "entry/map_lights",
            "entry/my_footprints",
            "entry/food",
            "entry/shopping",
            "entry/walk",
            "entry/bike",
            "entry/car",
            "entry/group",
        )
    }
}

data class TabItem(
    val route: String,
    val label: String,
    val icon: ImageVector,
)

