package com.travel.superapp.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.travel.superapp.data.auth.AuthManager
import com.travel.superapp.ui.screens.HomeScreen
import com.travel.superapp.ui.screens.LoginScreen
import com.travel.superapp.ui.screens.MineScreen
import com.travel.superapp.ui.screens.PostScreen
import com.travel.superapp.ui.screens.SettingsScreen
import com.travel.superapp.ui.screens.GuideCertificationScreen
import com.travel.superapp.ui.screens.GuideWithTagsScreen
import com.travel.superapp.ui.screens.BikeModuleScreen
import com.travel.superapp.ui.screens.CarTourModuleScreen
import com.travel.superapp.ui.screens.FoodScreen
import com.travel.superapp.ui.screens.GroupModuleScreen
import com.travel.superapp.ui.screens.MapLightsScreen
import com.travel.superapp.ui.screens.MyFootprintsScreen
import com.travel.superapp.ui.screens.RouteTrackingScreen
import com.travel.superapp.ui.screens.ShoppingScreen
import com.travel.superapp.ui.widgets.SuperBottomBar

private const val ROUTE_HOME = "tab/home"
private const val ROUTE_FIND_GUIDE = "tab/find_guide"
private const val ROUTE_AI = "tab/ai"
private const val ROUTE_POST = "tab/post"
private const val ROUTE_MINE = "tab/mine"
private const val ROUTE_SETTINGS = "settings"

@Composable
fun App() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    val currentUser by AuthManager.currentUser.collectAsState()

    val startDestination = if (currentUser != null) ROUTE_HOME else "login"

    val currentRoute = currentDestination?.route
    val isTabRoute = currentRoute == ROUTE_HOME ||
            currentRoute == ROUTE_FIND_GUIDE ||
            currentRoute == ROUTE_POST ||
            currentRoute == ROUTE_MINE
    val showBottomBar = currentUser != null && isTabRoute

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                val selectedRoute = when {
                    currentDestination?.hierarchy?.any { it.route == ROUTE_HOME } == true -> ROUTE_HOME
                    currentDestination?.hierarchy?.any { it.route == ROUTE_FIND_GUIDE } == true -> ROUTE_FIND_GUIDE
                    currentDestination?.hierarchy?.any { it.route == ROUTE_POST } == true -> ROUTE_POST
                    currentDestination?.hierarchy?.any { it.route == ROUTE_MINE } == true -> ROUTE_MINE
                    else -> ROUTE_HOME
                }
                SuperBottomBar(
                    selectedRoute = selectedRoute,
                    onTabSelected = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onAiClick = {
                        navController.navigate(ROUTE_AI) {
                            launchSingleTop = true
                        }
                    },
                )
            }
        },
    ) { innerPadding ->
        Box(Modifier.fillMaxSize()) {
            AppNavHost(
                navController = navController,
                contentPadding = innerPadding,
                startDestination = startDestination,
            )
        }
    }
}

@Composable
private fun AppNavHost(
    navController: androidx.navigation.NavHostController,
    contentPadding: PaddingValues,
    startDestination: String,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(ROUTE_HOME) {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onGuestMode = {
                    navController.navigate(ROUTE_HOME) {
                        popUpTo("login") { inclusive = true }
                    }
                },
            )
        }

        composable(ROUTE_HOME) {
            HomeScreen(
                onOpenEntry = { entryRoute ->
                    // 先进入模块页面，用户选择路线后再跳转到找导游页面
                    navController.navigate(entryRoute)
                },
            )
        }
        composable(ROUTE_FIND_GUIDE) {
            com.travel.superapp.ui.screens.FindGuideScreen(contentPadding)
        }
        composable(ROUTE_AI) {
            com.travel.superapp.ui.screens.AiScreen(contentPadding)
        }
        composable(ROUTE_POST) {
            PostScreen(contentPadding)
        }
        composable(ROUTE_MINE) {
            MineScreen(
                contentPadding = contentPadding,
                onOpenSettings = { navController.navigate(ROUTE_SETTINGS) },
                onLogout = {
                    kotlinx.coroutines.runBlocking {
                        AuthManager.signOut()
                    }
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onOpenGuideCertification = {
                    navController.navigate("guide_certification")
                },
            )
        }
        composable(ROUTE_SETTINGS) {
            SettingsScreen(
                contentPadding = contentPadding,
                onBack = { navController.popBackStack() },
            )
        }

        composable("entry/map_lights") {
            MapLightsScreen(contentPadding = contentPadding, onBack = { navController.popBackStack() })
        }
        composable("entry/my_footprints") {
            MyFootprintsScreen(contentPadding = contentPadding, onBack = { navController.popBackStack() })
        }
        composable("entry/food") {
            FoodScreen(contentPadding = contentPadding, onBack = { navController.popBackStack() })
        }
        composable("entry/shopping") {
            ShoppingScreen(contentPadding = contentPadding, onBack = { navController.popBackStack() })
        }
        composable("entry/route_tracking") {
            RouteTrackingScreen(contentPadding = contentPadding, onBack = { navController.popBackStack() })
        }
        composable("entry/bike") {
            BikeModuleScreen(
                onBack = { navController.popBackStack() },
                onNavigateToGuide = { type, time, location, tags ->
                    // 导航到找导游页面并传递需求信息和标签
                    navController.navigate("guide_with_tags/$tags") {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
        composable("entry/car") {
            CarTourModuleScreen(
                onBack = { navController.popBackStack() },
                onNavigateToGuide = { type, time, location, tags ->
                    navController.navigate("guide_with_tags/$tags") {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
        composable("entry/group") {
            GroupModuleScreen(
                onBack = { navController.popBackStack() },
                onNavigateToGuide = { type, time, location, tags ->
                    navController.navigate("guide_with_tags/$tags") {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
        composable("guide_with_tags/{tags}") { backStackEntry ->
            val tags = backStackEntry.arguments?.getString("tags") ?: ""
            // 导航到导游列表页面，带上预选的标签
            GuideWithTagsScreen(
                contentPadding = contentPadding,
                preSelectedTags = tags.split(",").filter { it.isNotBlank() },
                onBack = { navController.popBackStack() },
            )
        }
        composable("guide_certification") {
            GuideCertificationScreen(
                contentPadding = contentPadding,
                onBack = { navController.popBackStack() },
                onComplete = { level, tags ->
                    navController.popBackStack()
                },
            )
        }
    }
}
