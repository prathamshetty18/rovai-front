package com.roavai.parent.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.roavai.parent.data.fake.DemoSettings
import com.roavai.parent.ui.components.DemoControlsBottomSheet
import com.roavai.parent.ui.components.ScreenScaffold

@Composable
fun RoavaiNavHost(
    demoSettings: DemoSettings,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    var showDemoControls by remember { mutableStateOf(false) }

    val showBottomBar = currentRoute in setOf(
        NavRoutes.HOME,
        NavRoutes.PROGRESS,
        NavRoutes.ACTIVITY,
        NavRoutes.ROBOT,
        NavRoutes.PROFILE,
        NavRoutes.PROGRESS_OVERALL,
        NavRoutes.PROGRESS_LEARNING_TIME,
        NavRoutes.PROGRESS_SUBJECTS,
        NavRoutes.PROGRESS_SUBJECT_DETAIL,
        NavRoutes.PROGRESS_TOPICS,
        NavRoutes.PROGRESS_TESTS,
        NavRoutes.PROGRESS_COMPARISONS,
        NavRoutes.PROGRESS_CREDITS,
        NavRoutes.ACTIVITY_DETAIL,
        NavRoutes.GOALS_EDIT,
        NavRoutes.ACHIEVEMENTS
    )

    NavHost(
        navController = navController,
        startDestination = NavRoutes.HOME,
        modifier = modifier
    ) {
        val allRoutes = listOf(
            NavRoutes.SPLASH to "Splash",
            NavRoutes.ROBOT_SETUP to "Robot Setup",
            NavRoutes.ONBOARDING to "Onboarding",
            NavRoutes.SIGNUP to "Sign Up",
            NavRoutes.LOGIN to "Login",
            NavRoutes.FORGOT_EMAIL to "Forgot Password (Email)",
            NavRoutes.FORGOT_OTP to "Forgot Password (OTP)",
            NavRoutes.FORGOT_NEW_PASSWORD to "Forgot Password (New Password)",
            NavRoutes.ADD_CHILD to "Add Child",
            NavRoutes.HOME to "Home",
            NavRoutes.PROGRESS to "Progress",
            NavRoutes.ACTIVITY to "Activity",
            NavRoutes.ROBOT to "Robot",
            NavRoutes.PROFILE to "Profile",
            NavRoutes.PROGRESS_OVERALL to "Overall Progress Details",
            NavRoutes.PROGRESS_LEARNING_TIME to "Learning Time Details",
            NavRoutes.PROGRESS_SUBJECTS to "Subject Progress Details",
            NavRoutes.PROGRESS_SUBJECT_DETAIL to "Subject Topics Details",
            NavRoutes.PROGRESS_TOPICS to "Topics Mastery Details",
            NavRoutes.PROGRESS_TESTS to "Test Performance Details",
            NavRoutes.PROGRESS_COMPARISONS to "Comparisons Details",
            NavRoutes.PROGRESS_CREDITS to "Credits Details",
            NavRoutes.ACTIVITY_DETAIL to "Activity Details",
            NavRoutes.GOALS_EDIT to "Add/Edit Goal",
            NavRoutes.NOTIFICATIONS to "Notifications",
            NavRoutes.ACHIEVEMENTS to "Achievements"
        )

        allRoutes.forEach { (route, screenName) ->
            composable(route) {
                ScreenScaffold(
                    title = screenName,
                    onTitleLongClick = { showDemoControls = true },
                    onNotificationsClick = { navController.navigate(NavRoutes.NOTIFICATIONS) },
                    onProfileClick = { navController.navigate(NavRoutes.PROFILE) },
                    bottomBar = {
                        if (showBottomBar) {
                            RoavaiBottomBar(
                                currentRoute = currentRoute,
                                onNavigateToRoute = { targetRoute ->
                                    navController.navigate(targetRoute) {
                                        popUpTo(NavRoutes.HOME) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = screenName,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }
    }

    if (showDemoControls) {
        DemoControlsBottomSheet(
            demoSettings = demoSettings,
            onDismissRequest = { showDemoControls = false }
        )
    }
}
