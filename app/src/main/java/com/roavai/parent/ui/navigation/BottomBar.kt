package com.roavai.parent.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.roavai.parent.R

sealed class BottomNavItem(
    val route: String,
    val titleRes: Int,
    val icon: ImageVector
) {
    data object Home : BottomNavItem(NavRoutes.HOME, R.string.nav_home, Icons.Default.Home)
    data object Progress : BottomNavItem(NavRoutes.PROGRESS, R.string.nav_progress, Icons.Default.BarChart)
    data object Activity : BottomNavItem(NavRoutes.ACTIVITY, R.string.nav_activity, Icons.Default.History)
    data object Robot : BottomNavItem(NavRoutes.ROBOT, R.string.nav_robot, Icons.Default.SmartToy)
    data object Profile : BottomNavItem(NavRoutes.PROFILE, R.string.nav_profile, Icons.Default.Person)
}

val BottomNavItems = listOf(
    BottomNavItem.Home,
    BottomNavItem.Progress,
    BottomNavItem.Activity,
    BottomNavItem.Robot,
    BottomNavItem.Profile
)

@Composable
fun RoavaiBottomBar(
    currentRoute: String?,
    onNavigateToRoute: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(modifier = modifier) {
        BottomNavItems.forEach { item ->
            val selected = currentRoute == item.route
            NavigationBarItem(
                selected = selected,
                onClick = { onNavigateToRoute(item.route) },
                icon = { Icon(item.icon, contentDescription = stringResource(id = item.titleRes)) },
                label = { Text(stringResource(id = item.titleRes)) }
            )
        }
    }
}
