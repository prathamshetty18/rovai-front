package com.roavai.parent.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.roavai.parent.R
import com.roavai.parent.core.util.UiState
import com.roavai.parent.domain.model.*
import com.roavai.parent.ui.components.*
import com.roavai.parent.ui.navigation.NavRoutes
import com.roavai.parent.ui.navigation.RoavaiBottomBar
import com.roavai.parent.ui.theme.RoavaiTheme
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToRoute: (String) -> Unit,
    onOpenDemoControls: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val liveUnreadCount by viewModel.unreadCount.collectAsStateWithLifecycle()

    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                viewModel.refresh()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    HomeScreenContent(
        uiState = uiState,
        unreadNotifications = liveUnreadCount,
        onRetry = { viewModel.retry() },
        onNavigateToRoute = onNavigateToRoute,
        onOpenDemoControls = onOpenDemoControls,
        modifier = modifier
    )
}

@Composable
fun HomeScreenContent(
    uiState: UiState<HomeUiData>,
    unreadNotifications: Int,
    onRetry: () -> Unit,
    onNavigateToRoute: (String) -> Unit,
    onOpenDemoControls: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentData = when (uiState) {
        is UiState.Success -> uiState.data
        is UiState.Offline -> uiState.cached as? HomeUiData
        else -> null
    }
    val parentName = currentData?.parentName ?: "Meera"
    val childName = currentData?.homeData?.child?.name ?: "Aarav D'Souza"

    ScreenScaffold(
        title = stringResource(id = R.string.home_welcome_format, parentName),
        description = childName,
        unreadNotifications = unreadNotifications,
        onMenuClick = {},
        onNotificationsClick = { onNavigateToRoute(NavRoutes.NOTIFICATIONS) },
        onProfileClick = { onNavigateToRoute(NavRoutes.PROFILE) },
        onTitleLongClick = onOpenDemoControls,
        bottomBar = {
            RoavaiBottomBar(
                currentRoute = NavRoutes.HOME,
                onNavigateToRoute = onNavigateToRoute
            )
        },
        modifier = modifier
    ) { innerPadding ->
        StateHost(
            state = uiState,
            onRetry = onRetry,
            modifier = Modifier.padding(innerPadding)
        ) { data ->
            HomeSuccessBody(
                uiData = data,
                onNavigateToRoute = onNavigateToRoute
            )
        }
    }
}

@Composable
fun HomeSuccessBody(
    uiData: HomeUiData,
    onNavigateToRoute: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val homeData = uiData.homeData
    val child = homeData.child

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 2. Child info & Motivational message
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val initials = child.name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString("")
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = initials,
                            color = MaterialTheme.colorScheme.onPrimary,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = child.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = stringResource(id = R.string.child_header_grade_board_format, child.grade, child.curriculum),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.SmartToy,
                        contentDescription = stringResource(id = R.string.home_robot_image_cd),
                        modifier = Modifier.size(36.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Motivational message from SAMPLE_DATA.md section 16
                Text(
                    text = stringResource(id = R.string.home_motivational_message, child.name.split(" ").firstOrNull() ?: child.name),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = { onNavigateToRoute(NavRoutes.PROFILE) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 48.dp)
                ) {
                    Text(text = stringResource(id = R.string.home_view_profile))
                }
            }
        }

        // 3. Robot status
        RobotStatusCard(
            status = homeData.robot,
            onViewDetailsClick = { onNavigateToRoute(NavRoutes.ROBOT) }
        )

        // 4. Today's learning
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(id = R.string.home_today_learning_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Learning time and goal progress bar
                val today = homeData.today
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(id = R.string.home_learning_time_label),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = stringResource(id = R.string.home_daily_goal_format, today.learningMinutes, today.dailyGoalMinutes),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                val goalProgress = if (today.dailyGoalMinutes > 0) {
                    (today.learningMinutes.toFloat() / today.dailyGoalMinutes).coerceIn(0f, 1f)
                } else 0f
                LinearProgressIndicator(
                    progress = { goalProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(MaterialTheme.shapes.small)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Stats grid: Topics, Subjects, Questions, Overall Progress
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard(
                        label = stringResource(id = R.string.home_topics_covered_label),
                        value = "${today.topicsCovered}",
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        label = stringResource(id = R.string.home_overall_progress_label),
                        value = "${today.overallProgressPercent}%",
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Subjects covered
                Text(
                    text = stringResource(id = R.string.home_subjects_covered_format, today.subjects.joinToString(", ")),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Questions answered
                Text(
                    text = stringResource(
                        id = R.string.home_questions_detail_format,
                        today.questionsAnswered,
                        today.correct,
                        today.incorrect
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // 5. Recent activity (latest 3)
        Column {
            SectionHeader(
                title = stringResource(id = R.string.home_recent_activity_title),
                onViewAllClick = { onNavigateToRoute(NavRoutes.ACTIVITY) }
            )
            homeData.recentActivity.take(3).forEach { activity ->
                Spacer(modifier = Modifier.height(8.dp))
                ActivityRow(
                    activity = activity,
                    onClick = { onNavigateToRoute("activity/${activity.id}") }
                )
            }
        }

        // 6. Subject progress
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                SectionHeader(
                    title = stringResource(id = R.string.home_subject_progress_title),
                    onViewAllClick = { onNavigateToRoute(NavRoutes.PROGRESS_SUBJECTS) }
                )
                homeData.subjectProgress.forEach { sp ->
                    Spacer(modifier = Modifier.height(12.dp))
                    LabeledProgressBar(
                        label = sp.subject,
                        percent = sp.percent
                    )
                }
            }
        }

        // 7. Upcoming goals
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.home_upcoming_goals_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                TextButton(
                    onClick = { onNavigateToRoute(NavRoutes.GOALS_EDIT) },
                    modifier = Modifier.defaultMinSize(minHeight = 48.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = stringResource(id = R.string.home_add_goal))
                }
            }
            homeData.upcomingGoals.forEach { goal ->
                Spacer(modifier = Modifier.height(8.dp))
                GoalRow(
                    goal = goal,
                    onClick = { onNavigateToRoute(NavRoutes.GOALS_EDIT) }
                )
            }
        }

        // 8. Achievements (latest 3)
        Column {
            SectionHeader(
                title = stringResource(id = R.string.home_achievements_title),
                onViewAllClick = { onNavigateToRoute(NavRoutes.ACHIEVEMENTS) }
            )
            homeData.achievements.take(3).forEach { achievement ->
                Spacer(modifier = Modifier.height(8.dp))
                AchievementChip(
                    achievement = achievement,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    val child = Child(
        id = "c1",
        name = "Aarav D'Souza",
        grade = "Class 10",
        curriculum = "CBSE",
        avatarUrl = null,
        dailyGoalMinutes = 60
    )
    val today = TodaySummary(
        learningMinutes = 45,
        dailyGoalMinutes = 60,
        topicsCovered = 3,
        subjects = listOf("Mathematics", "Science", "English"),
        questionsAnswered = 25,
        correct = 20,
        incorrect = 5,
        overallProgressPercent = 60
    )
    val robot = RobotStatus(
        connected = true,
        wifiConnected = true,
        wifiSignal = WifiSignal.STRONG,
        batteryPercent = 82,
        lastActive = Instant.now(),
        firmwareVersion = "1.0.3"
    )
    val activities = listOf(
        Activity(
            id = "a01",
            title = "Mathematics: Quadratic Equations",
            subject = "Mathematics",
            topic = "Quadratic Equations",
            summary = "Completed 15 questions",
            type = ActivityType.PRACTICE,
            status = ActivityStatus.COMPLETED,
            startedAt = Instant.now(),
            durationMinutes = 15
        )
    )
    val subjectProgress = listOf(
        SubjectProgress("Mathematics", 70, 7, 10, 1),
        SubjectProgress("Science", 60, 6, 10, 2)
    )
    val goals = listOf(
        Goal("g1", "Practice Algebra", "Mathematics", "Pair of Linear Equations", LocalDate.now(), LocalTime.of(17, 0), false)
    )
    val achievements = listOf(
        Achievement("ac1", AchievementKind.STREAK, "7-day learning streak", "Learned every day this week", Instant.now())
    )

    val homeData = HomeData(
        child = child,
        robot = robot,
        today = today,
        recentActivity = activities,
        subjectProgress = subjectProgress,
        upcomingGoals = goals,
        achievements = achievements,
        unreadNotifications = 3
    )

    RoavaiTheme {
        HomeScreenContent(
            uiState = UiState.Success(HomeUiData("Meera", homeData)),
            unreadNotifications = 3,
            onRetry = {},
            onNavigateToRoute = {},
            onOpenDemoControls = {}
        )
    }
}
