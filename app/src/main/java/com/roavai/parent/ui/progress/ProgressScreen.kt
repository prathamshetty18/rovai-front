package com.roavai.parent.ui.progress

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

@Composable
fun ProgressScreen(
    viewModel: ProgressViewModel,
    onNavigateToRoute: (String) -> Unit,
    onOpenDemoControls: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val unreadCount by viewModel.unreadNotificationsCount.collectAsStateWithLifecycle()

    ProgressScreenContent(
        uiState = uiState,
        unreadNotifications = unreadCount,
        onPeriodSelected = { viewModel.setPeriod(it) },
        onNavigateToRoute = onNavigateToRoute,
        onNotificationsClick = { onNavigateToRoute(NavRoutes.NOTIFICATIONS) },
        onProfileClick = { onNavigateToRoute(NavRoutes.PROFILE) },
        onTitleLongClick = onOpenDemoControls,
        onRetry = { viewModel.retry() },
        modifier = modifier
    )
}

@Composable
fun ProgressScreenContent(
    uiState: UiState<ProgressHubUiData>,
    unreadNotifications: Int,
    onPeriodSelected: (Period) -> Unit,
    onNavigateToRoute: (String) -> Unit,
    onNotificationsClick: () -> Unit,
    onProfileClick: () -> Unit,
    onTitleLongClick: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    ScreenScaffold(
        title = stringResource(id = R.string.progress_title),
        description = stringResource(id = R.string.progress_description),
        unreadNotifications = unreadNotifications,
        onNotificationsClick = onNotificationsClick,
        onProfileClick = onProfileClick,
        onTitleLongClick = onTitleLongClick,
        bottomBar = {
            RoavaiBottomBar(
                currentRoute = NavRoutes.PROGRESS,
                onNavigateToRoute = onNavigateToRoute
            )
        },
        modifier = modifier
    ) { innerPadding ->
        StateHost(
            state = uiState,
            onRetry = onRetry,
            emptyContent = {
                EmptyState(
                    title = stringResource(id = R.string.common_empty_title),
                    message = stringResource(id = R.string.common_empty_message)
                )
            },
            modifier = Modifier.padding(innerPadding)
        ) { data ->
            ProgressHubBody(
                uiData = data,
                onPeriodSelected = onPeriodSelected,
                onNavigateToRoute = onNavigateToRoute
            )
        }
    }
}

@Composable
fun ProgressHubBody(
    uiData: ProgressHubUiData,
    onPeriodSelected: (Period) -> Unit,
    onNavigateToRoute: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ChildHeader(
            name = uiData.child.name,
            grade = uiData.child.grade,
            curriculum = uiData.child.curriculum
        )

        PeriodSelector(
            selectedPeriod = uiData.selectedPeriod,
            onPeriodSelected = onPeriodSelected
        )

        // Motivational Credits Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToRoute(NavRoutes.PROGRESS_CREDITS) }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Stars,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(id = R.string.progress_credits_banner_format, uiData.credits.total),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = stringResource(id = R.string.common_view_details),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        // 1. Overall Progress Card
        HubCard(
            title = stringResource(id = R.string.progress_card_overall),
            subtitle = "${uiData.overall.percent}% • ${uiData.overall.statusLabel}",
            onClick = { onNavigateToRoute(NavRoutes.PROGRESS_OVERALL) }
        ) {
            LabeledProgressBar(
                label = stringResource(id = R.string.progress_card_overall),
                percent = uiData.overall.percent
            )
        }

        // 2. Learning Time Card
        HubCard(
            title = stringResource(id = R.string.progress_card_learning_time),
            subtitle = "${uiData.learningTime.totalMinutes} minutes",
            onClick = { onNavigateToRoute(NavRoutes.PROGRESS_LEARNING_TIME) }
        ) {
            Text(
                text = stringResource(id = R.string.progress_learning_time_daily_goal, uiData.learningTime.dailyGoalMinutes),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // 3. Subject Progress Card
        HubCard(
            title = stringResource(id = R.string.progress_card_subject),
            subtitle = "${uiData.subjects.size} subjects",
            onClick = { onNavigateToRoute(NavRoutes.PROGRESS_SUBJECTS) }
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                uiData.subjects.take(3).forEach { sp ->
                    LabeledProgressBar(label = sp.subject, percent = sp.percent)
                }
            }
        }

        // 4. Topics Mastery Card
        val completedCount = uiData.topics.count { it.status == TopicStatus.COMPLETED }
        val learningCount = uiData.topics.count { it.status == TopicStatus.LEARNING }
        val practiceCount = uiData.topics.count { it.status == TopicStatus.NEEDS_PRACTICE }
        HubCard(
            title = stringResource(id = R.string.progress_card_topics),
            subtitle = "$completedCount completed, $learningCount learning, $practiceCount need practice",
            onClick = { onNavigateToRoute(NavRoutes.PROGRESS_TOPICS) }
        )

        // 5. Test Performance Card
        val avgScore = if (uiData.tests.isNotEmpty()) uiData.tests.map { it.percentage }.average().toInt() else 0
        HubCard(
            title = stringResource(id = R.string.progress_card_tests),
            subtitle = "${uiData.tests.size} tests taken • Average: $avgScore%",
            onClick = { onNavigateToRoute(NavRoutes.PROGRESS_TESTS) }
        )

        // 6. Comparisons Card
        HubCard(
            title = stringResource(id = R.string.progress_card_comparisons),
            subtitle = "${uiData.comparisons.learningTime.change} learning time, ${uiData.comparisons.overallProgress.change} overall",
            onClick = { onNavigateToRoute(NavRoutes.PROGRESS_COMPARISONS) }
        )

        // 7. Credits Card
        HubCard(
            title = stringResource(id = R.string.progress_card_credits),
            subtitle = stringResource(id = R.string.progress_credits_total_format, uiData.credits.total),
            onClick = { onNavigateToRoute(NavRoutes.PROGRESS_CREDITS) }
        )
    }
}

@Composable
fun HubCard(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: (@Composable () -> Unit)? = null
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = stringResource(id = R.string.common_view_details),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (content != null) {
                Spacer(modifier = Modifier.height(12.dp))
                content()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProgressScreenPreview() {
    RoavaiTheme {
        ProgressScreenContent(
            uiState = UiState.Success(
                ProgressHubUiData(
                    child = Child("c1", "Aarav", "Class 10", "CBSE", null, 60),
                    selectedPeriod = Period.THIS_WEEK,
                    overall = OverallProgress(60, "Keep going", 28, 2, emptyList()),
                    learningTime = LearningTime(300, 60, emptyList()),
                    subjects = listOf(SubjectProgress("Mathematics", 70, 7, 10, 1)),
                    topics = emptyList(),
                    tests = emptyList(),
                    comparisons = Comparisons(
                        ComparisonRow("Learning time", "300 min", "215 min", "up 85 min"),
                        ComparisonRow("Overall progress", "60%", "55%", "up 5 points"),
                        ComparisonRow("Test average", "70%", "78%", "down 8 points"),
                        emptyList()
                    ),
                    credits = CreditSummary(130, emptyList(), "Great job!")
                )
            ),
            unreadNotifications = 1,
            onPeriodSelected = {},
            onNavigateToRoute = {},
            onNotificationsClick = {},
            onProfileClick = {},
            onTitleLongClick = {},
            onRetry = {}
        )
    }
}
