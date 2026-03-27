package com.travel.superapp.ui.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.travel.superapp.ui.nav.AppRoute

@Composable
fun SuperBottomBar(
    selectedRoute: String,
    onTabSelected: (String) -> Unit,
    onAiClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background),
    ) {
        NavigationBar(
            modifier = Modifier
                .height(64.dp)
                .windowInsetsPadding(WindowInsets.navigationBars),
            tonalElevation = 3.dp,
        ) {
            BottomItem(
                selected = selectedRoute == AppRoute.Home.route,
                label = "主页",
                icon = AppRoute.tabRoutes[0].icon,
                onClick = { onTabSelected(AppRoute.Home.route) },
            )
            BottomItem(
                selected = selectedRoute == AppRoute.FindGuide.route,
                label = "找导游",
                icon = AppRoute.tabRoutes[1].icon,
                onClick = { onTabSelected(AppRoute.FindGuide.route) },
            )

            // spacer for center FAB
            NavigationBarItem(
                selected = false,
                onClick = {},
                icon = { },
                label = { },
                enabled = false,
            )

            BottomItem(
                selected = selectedRoute == AppRoute.Post.route,
                label = "投稿",
                icon = AppRoute.tabRoutes[2].icon,
                onClick = { onTabSelected(AppRoute.Post.route) },
            )
            BottomItem(
                selected = selectedRoute == AppRoute.Mine.route,
                label = "我的",
                icon = AppRoute.tabRoutes[3].icon,
                onClick = { onTabSelected(AppRoute.Mine.route) },
            )
        }

        FloatingActionButton(
            onClick = onAiClick,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(PaddingValues(top = 0.dp))
                .size(72.dp)
                .clip(CircleShape),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White,
        ) {
            Icon(Icons.Filled.SmartToy, contentDescription = "问AI")
        }
    }
}

@Composable
private fun RowScope.BottomItem(
    selected: Boolean,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
) {
    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = { Icon(icon, contentDescription = label) },
        label = { Text(label) },
    )
}

