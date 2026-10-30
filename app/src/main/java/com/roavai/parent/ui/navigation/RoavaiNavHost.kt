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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.roavai.parent.data.fake.DemoSettings
import com.roavai.parent.domain.repository.NotificationsRepository
import com.roavai.parent.ui.achievements.AchievementsScreen
import com.roavai.parent.ui.activity.ActivityDetailScreen
import com.roavai.parent.ui.activity.ActivityScreen
import com.roavai.parent.ui.components.DemoControlsBottomSheet
import com.roavai.parent.ui.components.ScreenScaffold
import com.roavai.parent.ui.goals.GoalsScreen
import com.roavai.parent.ui.home.HomeScreen
import com.roavai.parent.ui.notifications.NotificationsScreen
import com.roavai.parent.ui.profile.ProfileScreen
import com.roavai.parent.ui.progress.ProgressScreen
import com.roavai.parent.ui.progress.detail.*
import com.roavai.parent.ui.robot.RobotScreen
import com.roavai.parent.ui.robot.RobotSetupScreen

@Composable
fun RoavaiNavHost(
    demoSettings: DemoSettings,
    notificationsRepository: NotificationsRepository,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    var showDemoControls by remember { mutableStateOf(false) }
    val liveUnreadCount by notificationsRepository.unreadCountFlow.collectAsStateWithLifecycle()

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

    val onNavigate: (String) -> Unit = { targetRoute ->
        if (targetRoute == currentRoute) {
            // Already on target route
        } else if (targetRoute in NavRoutes.TAB_ROUTES) {
            navController.navigate(targetRoute) {
                popUpTo(NavRoutes.HOME) { saveState = true }
                launchSingleTop = true
                restoreState = true
            }
        } else {
            navController.navigate(targetRoute) {
                launchSingleTop = true
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = NavRoutes.HOME,
        modifier = modifier
    ) {
        composable(NavRoutes.HOME) {
            HomeScreen(
                viewModel = hiltViewModel(),
                onNavigateToRoute = onNavigate,
                onOpenDemoControls = { showDemoControls = true }
            )
        }

        composable(NavRoutes.ACTIVITY) {
            ActivityScreen(
                viewModel = hiltViewModel(),
                onNavigateToRoute = onNavigate,
                onOpenDemoControls = { showDemoControls = true }
            )
        }

        composable(NavRoutes.ACTIVITY_DETAIL) {
            ActivityDetailScreen(
                viewModel = hiltViewModel(),
                onNavigateToRoute = onNavigate,
                onOpenDemoControls = { showDemoControls = true }
            )
        }

        composable(NavRoutes.PROGRESS) {
            ProgressScreen(
                viewModel = hiltViewModel(),
                onNavigateToRoute = onNavigate,
                onOpenDemoControls = { showDemoControls = true }
            )
        }

        composable(NavRoutes.PROGRESS_OVERALL) {
            ProgressOverallScreen(
                viewModel = hiltViewModel(),
                onNavigateToRoute = onNavigate,
                onOpenDemoControls = { showDemoControls = true }
            )
        }

        composable(NavRoutes.PROGRESS_LEARNING_TIME) {
            ProgressLearningTimeScreen(
                viewModel = hiltViewModel(),
                onNavigateToRoute = onNavigate,
                onOpenDemoControls = { showDemoControls = true }
            )
        }

        composable(NavRoutes.PROGRESS_SUBJECTS) {
            ProgressSubjectsScreen(
                viewModel = hiltViewModel(),
                onNavigateToRoute = onNavigate,
                onOpenDemoControls = { showDemoControls = true }
            )
        }

        composable(NavRoutes.PROGRESS_SUBJECT_DETAIL) {
            ProgressSubjectDetailScreen(
                viewModel = hiltViewModel(),
                onNavigateToRoute = onNavigate,
                onOpenDemoControls = { showDemoControls = true }
            )
        }

        composable(NavRoutes.PROGRESS_TOPICS) {
            ProgressTopicsScreen(
                viewModel = hiltViewModel(),
                onNavigateToRoute = onNavigate,
                onOpenDemoControls = { showDemoControls = true }
            )
        }

        composable(NavRoutes.PROGRESS_TESTS) {
            ProgressTestsScreen(
                viewModel = hiltViewModel(),
                onNavigateToRoute = onNavigate,
                onOpenDemoControls = { showDemoControls = true }
            )
        }

        composable(NavRoutes.PROGRESS_COMPARISONS) {
            ProgressComparisonsScreen(
                viewModel = hiltViewModel(),
                onNavigateToRoute = onNavigate,
                onOpenDemoControls = { showDemoControls = true }
            )
        }

        composable(NavRoutes.PROGRESS_CREDITS) {
            ProgressCreditsScreen(
                viewModel = hiltViewModel(),
                onNavigateToRoute = onNavigate,
                onOpenDemoControls = { showDemoControls = true }
            )
        }

        composable(NavRoutes.GOALS_EDIT) {
            GoalsScreen(
                viewModel = hiltViewModel(),
                onNavigateToRoute = onNavigate,
                onOpenDemoControls = { showDemoControls = true }
            )
        }

        composable(NavRoutes.NOTIFICATIONS) {
            NotificationsScreen(
                viewModel = hiltViewModel(),
                onNavigateToRoute = onNavigate,
                onOpenDemoControls = { showDemoControls = true }
            )
        }

        composable(NavRoutes.ACHIEVEMENTS) {
            AchievementsScreen(
                viewModel = hiltViewModel(),
                onNavigateToRoute = onNavigate,
                onOpenDemoControls = { showDemoControls = true }
            )
        }

        composable(NavRoutes.ROBOT) {
            RobotScreen(
                viewModel = hiltViewModel(),
                onNavigateToRoute = onNavigate,
                onOpenDemoControls = { showDemoControls = true }
            )
        }

        composable(NavRoutes.ROBOT_SETUP) {
            RobotSetupScreen(
                viewModel = hiltViewModel(),
                onNavigateBack = { navController.popBackStack() },
                onOpenDemoControls = { showDemoControls = true }
            )
        }

        composable(NavRoutes.PROFILE) {
            ProfileScreen(
                viewModel = hiltViewModel(),
                onNavigateToRoute = onNavigate,
                onOpenDemoControls = { showDemoControls = true }
            )
        }

        // Placeholders for remaining unbuilt routes
        val remainingPlaceholderRoutes = listOf(
            NavRoutes.SPLASH to "Splash",
            NavRoutes.ONBOARDING to "Onboarding",
            NavRoutes.SIGNUP to "Sign Up",
            NavRoutes.LOGIN to "Login",
            NavRoutes.FORGOT_EMAIL to "Forgot Password (Email)",
            NavRoutes.FORGOT_OTP to "Forgot Password (OTP)",
            NavRoutes.FORGOT_NEW_PASSWORD to "Forgot Password (New Password)",
            NavRoutes.ADD_CHILD to "Add Child"
        )

        remainingPlaceholderRoutes.forEach { (route, screenName) ->
            composable(route) {
                ScreenScaffold(
                    title = screenName,
                    unreadNotifications = liveUnreadCount,
                    onTitleLongClick = { showDemoControls = true },
                    onNotificationsClick = { onNavigate(NavRoutes.NOTIFICATIONS) },
                    onProfileClick = { onNavigate(NavRoutes.PROFILE) },
                    bottomBar = {
                        if (showBottomBar) {
                            RoavaiBottomBar(
                                currentRoute = currentRoute,
                                onNavigateToRoute = onNavigate
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
