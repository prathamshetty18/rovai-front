package com.roavai.parent.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.roavai.parent.data.fake.DemoSettings
import com.roavai.parent.domain.repository.NotificationsRepository
import com.roavai.parent.ui.achievements.AchievementsScreen
import com.roavai.parent.ui.activity.ActivityDetailScreen
import com.roavai.parent.ui.activity.ActivityScreen
import com.roavai.parent.ui.auth.*
import com.roavai.parent.ui.child.AddChildScreen
import com.roavai.parent.ui.components.DemoControlsBottomSheet
import com.roavai.parent.ui.goals.GoalsScreen
import com.roavai.parent.ui.home.HomeScreen
import com.roavai.parent.ui.notifications.NotificationsScreen
import com.roavai.parent.ui.onboarding.OnboardingScreen
import com.roavai.parent.ui.profile.ProfileScreen
import com.roavai.parent.ui.progress.ProgressScreen
import com.roavai.parent.ui.progress.detail.*
import com.roavai.parent.ui.robot.RobotScreen
import com.roavai.parent.ui.robot.RobotSetupScreen
import com.roavai.parent.ui.splash.SplashScreen

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
    var loginSnackbarMessage by remember { mutableStateOf<String?>(null) }
    val liveUnreadCount by notificationsRepository.unreadCountFlow.collectAsStateWithLifecycle()

    val forgotPasswordViewModel: ForgotPasswordViewModel = hiltViewModel()

    val onNavigate: (String) -> Unit = { targetRoute ->
        if (targetRoute == currentRoute) {
            // Already on target route
        } else if (targetRoute == NavRoutes.LOGIN) {
            navController.navigate(targetRoute) {
                popUpTo(0) { inclusive = true }
                launchSingleTop = true
            }
        } else if (targetRoute == NavRoutes.HOME && currentRoute in setOf(NavRoutes.SPLASH, NavRoutes.LOGIN, NavRoutes.ADD_CHILD)) {
            navController.navigate(targetRoute) {
                popUpTo(0) { inclusive = true }
                launchSingleTop = true
            }
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
        startDestination = NavRoutes.SPLASH,
        modifier = modifier
    ) {
        composable(NavRoutes.SPLASH) {
            SplashScreen(
                viewModel = hiltViewModel(),
                onNavigateToRoute = { target ->
                    navController.navigate(target) {
                        popUpTo(NavRoutes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        composable(NavRoutes.ROBOT_SETUP) {
            val previousRoute = navController.previousBackStackEntry?.destination?.route
            RobotSetupScreen(
                viewModel = hiltViewModel(),
                onContinue = {
                    if (previousRoute == NavRoutes.ROBOT) {
                        navController.popBackStack()
                    } else {
                        navController.navigate("onboarding/0") {
                            popUpTo(NavRoutes.ROBOT_SETUP) { inclusive = true }
                        }
                    }
                },
                onOpenDemoControls = { showDemoControls = true }
            )
        }

        composable(
            route = NavRoutes.ONBOARDING,
            arguments = listOf(navArgument("page") { type = NavType.IntType; defaultValue = 0 })
        ) { backStackEntry ->
            val page = backStackEntry.arguments?.getInt("page") ?: 0
            OnboardingScreen(
                initialPage = page,
                onNavigateToRoute = onNavigate
            )
        }

        composable(NavRoutes.SIGNUP) {
            SignUpScreen(
                viewModel = hiltViewModel(),
                onNavigateToRoute = onNavigate,
                onOpenDemoControls = { showDemoControls = true }
            )
        }

        composable(NavRoutes.LOGIN) {
            LoginScreen(
                viewModel = hiltViewModel(),
                onNavigateToRoute = onNavigate,
                onOpenDemoControls = { showDemoControls = true },
                snackbarMessage = loginSnackbarMessage
            )
        }

        composable(NavRoutes.FORGOT_EMAIL) {
            ForgotEmailScreen(
                viewModel = forgotPasswordViewModel,
                onNavigateToOtp = { navController.navigate(NavRoutes.FORGOT_OTP) },
                onOpenDemoControls = { showDemoControls = true }
            )
        }

        composable(NavRoutes.FORGOT_OTP) {
            ForgotOtpScreen(
                viewModel = forgotPasswordViewModel,
                onNavigateToNewPassword = { navController.navigate(NavRoutes.FORGOT_NEW_PASSWORD) },
                onOpenDemoControls = { showDemoControls = true }
            )
        }

        composable(NavRoutes.FORGOT_NEW_PASSWORD) {
            ForgotNewPasswordScreen(
                viewModel = forgotPasswordViewModel,
                onSuccess = { message ->
                    loginSnackbarMessage = message
                    navController.navigate(NavRoutes.LOGIN) {
                        popUpTo(NavRoutes.LOGIN) { inclusive = true }
                    }
                },
                onOpenDemoControls = { showDemoControls = true }
            )
        }

        composable(NavRoutes.ADD_CHILD) {
            AddChildScreen(
                viewModel = hiltViewModel(),
                onNavigateToHome = {
                    navController.navigate(NavRoutes.HOME) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onOpenDemoControls = { showDemoControls = true }
            )
        }

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

        composable(NavRoutes.PROFILE) {
            ProfileScreen(
                viewModel = hiltViewModel(),
                onNavigateToRoute = onNavigate,
                onOpenDemoControls = { showDemoControls = true }
            )
        }
    }

    if (showDemoControls) {
        DemoControlsBottomSheet(
            demoSettings = demoSettings,
            onDismissRequest = { showDemoControls = false }
        )
    }
}
