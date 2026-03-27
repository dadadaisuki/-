package com.travel.superapp.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.travel.superapp.ui.nav.AppRoute
import com.travel.superapp.ui.screens.AiScreen
import com.travel.superapp.ui.screens.FindGuideScreen
import com.travel.superapp.ui.screens.HomeScreen
import com.travel.superapp.ui.screens.MineScreen
import com.travel.superapp.ui.screens.PostScreen
import com.travel.superapp.ui.screens.SimpleEntryScreen
import com.travel.superapp.ui.widgets.SuperBottomBar

@Composable
fun App() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    Scaffold(
        bottomBar = {
            SuperBottomBar(
                selectedRoute = AppRoute.tabRoutes.firstOrNull { tab ->
                    currentDestination?.hierarchy?.any { it.route == tab.route } == true
                }?.route ?: AppRoute.Home.route,
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
                    navController.navigate(AppRoute.Ai.route) {
                        launchSingleTop = true
                    }
                },
            )
        },
    ) { innerPadding ->
        Box(Modifier.fillMaxSize()) {
            AppNavHost(
                navController = navController,
                contentPadding = innerPadding,
            )
        }
    }
}

@Composable
private fun AppNavHost(
    navController: NavHostController,
    contentPadding: PaddingValues,
) {
    NavHost(
        navController = navController,
        startDestination = AppRoute.Home.route,
    ) {
        composable(AppRoute.Home.route) {
            HomeScreen(
                contentPadding = contentPadding,
                onOpenEntry = { entryRoute ->
                    navController.navigate(entryRoute)
                },
            )
        }
        composable(AppRoute.FindGuide.route) { FindGuideScreen(contentPadding) }
        composable(AppRoute.Ai.route) { AiScreen(contentPadding) }
        composable(AppRoute.Post.route) { PostScreen(contentPadding) }
        composable(AppRoute.Mine.route) { MineScreen(contentPadding) }

        AppRoute.entryRoutes.forEach { route ->
            composable(route) { backStack ->
                SimpleEntryScreen(
                    title = backStack.destination.route ?: "页面",
                    contentPadding = contentPadding,
                    onBack = { navController.popBackStack() },
                )
            }
        }
    }
}

