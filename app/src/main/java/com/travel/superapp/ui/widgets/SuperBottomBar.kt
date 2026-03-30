package com.travel.superapp.ui.widgets

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.travel.superapp.ui.nav.AppRoute

data class TabConfig(
    val label: String,
    val icon: ImageVector,
    val route: String,
)

private val tabList = listOf(
    TabConfig("主页", Icons.Filled.Home, AppRoute.Home.route),
    TabConfig("找导游", Icons.Filled.Search, AppRoute.FindGuide.route),
    TabConfig("投稿", Icons.Filled.Receipt, AppRoute.Post.route),
    TabConfig("我的", Icons.Filled.Person, AppRoute.Mine.route),
)

@Composable
fun SuperBottomBar(
    selectedRoute: String,
    onTabSelected: (String) -> Unit,
    onAiClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(vertical = 6.dp, horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // 左侧 2 个 tab
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.Start,
        ) {
            tabList.take(2).forEach { tab ->
                BottomTabItem(
                    label = tab.label,
                    icon = tab.icon,
                    selected = selectedRoute == tab.route,
                    onClick = { onTabSelected(tab.route) },
                )
            }
        }

        // 中间 AI 按钮 - 圆形浮出
        Box(
            modifier = Modifier.width(60.dp),
            contentAlignment = Alignment.TopCenter,
        ) {
            FloatingActionButton(
                onClick = onAiClick,
                modifier = Modifier
                    .size(52.dp)
                    .shadow(
                        elevation = 6.dp,
                        shape = CircleShape,
                    ),
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 4.dp),
            ) {
                Icon(
                    Icons.Filled.SmartToy,
                    contentDescription = "问AI",
                    modifier = Modifier.size(28.dp),
                )
            }
        }

        // 右侧 2 个 tab
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.End,
        ) {
            tabList.drop(2).forEach { tab ->
                BottomTabItem(
                    label = tab.label,
                    icon = tab.icon,
                    selected = selectedRoute == tab.route,
                    onClick = { onTabSelected(tab.route) },
                )
            }
        }
    }
}

@Composable
private fun BottomTabItem(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val animatedColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "tabColor",
    )

    Column(
        modifier = Modifier
            .width(70.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp, horizontal = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (selected) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        icon,
                        contentDescription = label,
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(Modifier.height(1.dp))
                    Text(
                        text = label,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp),
            ) {
                Icon(
                    icon,
                    contentDescription = label,
                    modifier = Modifier.size(24.dp),
                    tint = animatedColor,
                )
                Spacer(Modifier.height(1.dp))
                Text(
                    text = label,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal,
                    color = animatedColor,
                )
            }
        }
    }
}
