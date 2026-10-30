package com.roavai.parent.ui.activity

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.roavai.parent.R
import com.roavai.parent.core.util.UiState
import com.roavai.parent.domain.model.Activity
import com.roavai.parent.domain.model.ActivityPeriod
import com.roavai.parent.domain.model.ActivityStatus
import com.roavai.parent.domain.model.ActivityType
import com.roavai.parent.domain.model.Child
import com.roavai.parent.ui.components.ActivityRow
import com.roavai.parent.ui.components.ChildHeader
import com.roavai.parent.ui.components.EmptyState
import com.roavai.parent.ui.components.ScreenScaffold
import com.roavai.parent.ui.components.StateHost
import com.roavai.parent.ui.navigation.NavRoutes
import com.roavai.parent.ui.navigation.RoavaiBottomBar
import com.roavai.parent.ui.theme.RoavaiTheme
import java.time.Instant
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun ActivityScreen(
    viewModel: ActivityViewModel,
    onNavigateToRoute: (String) -> Unit,
    onOpenDemoControls: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val unreadCount by viewModel.unreadNotificationsCount.collectAsStateWithLifecycle()

    ActivityScreenContent(
        uiState = uiState,
        unreadNotifications = unreadCount,
        onPeriodSelected = { viewModel.setPeriod(it) },
        onSubjectSelected = { viewModel.setSubject(it) },
        onActivityClick = { activityId -> onNavigateToRoute("activity/$activityId") },
        onNavigateToRoute = onNavigateToRoute,
        onNotificationsClick = { onNavigateToRoute(NavRoutes.NOTIFICATIONS) },
        onProfileClick = { onNavigateToRoute(NavRoutes.PROFILE) },
        onTitleLongClick = onOpenDemoControls,
        onRetry = { viewModel.retry() },
        modifier = modifier
    )
}

@Composable
fun ActivityScreenContent(
    uiState: UiState<ActivityUiData>,
    unreadNotifications: Int,
    onPeriodSelected: (ActivityPeriod) -> Unit,
    onSubjectSelected: (String?) -> Unit,
    onActivityClick: (String) -> Unit,
    onNavigateToRoute: (String) -> Unit,
    onNotificationsClick: () -> Unit,
    onProfileClick: () -> Unit,
    onTitleLongClick: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    ScreenScaffold(
        title = stringResource(id = R.string.activity_title),
        description = stringResource(id = R.string.activity_description),
        unreadNotifications = unreadNotifications,
        onNotificationsClick = onNotificationsClick,
        onProfileClick = onProfileClick,
        onTitleLongClick = onTitleLongClick,
        bottomBar = {
            RoavaiBottomBar(
                currentRoute = NavRoutes.ACTIVITY,
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
                    title = stringResource(id = R.string.activity_empty_title),
                    message = stringResource(id = R.string.activity_empty_message)
                )
            },
            offlineMessage = stringResource(id = R.string.activity_offline_message),
            modifier = Modifier.padding(innerPadding)
        ) { data ->
            ActivityContentBody(
                uiData = data,
                onPeriodSelected = onPeriodSelected,
                onSubjectSelected = onSubjectSelected,
                onActivityClick = onActivityClick
            )
        }
    }
}

@Composable
fun ActivityContentBody(
    uiData: ActivityUiData,
    onPeriodSelected: (ActivityPeriod) -> Unit,
    onSubjectSelected: (String?) -> Unit,
    onActivityClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val periods = listOf(
        ActivityPeriod.TODAY to R.string.activity_filter_today,
        ActivityPeriod.THIS_WEEK to R.string.activity_filter_this_week,
        ActivityPeriod.THIS_MONTH to R.string.activity_filter_this_month,
        ActivityPeriod.ALL_TIME to R.string.activity_filter_all_time
    )

    val subjects = listOf(
        null to stringResource(id = R.string.activity_filter_all_subjects),
        "Mathematics" to stringResource(id = R.string.subject_mathematics),
        "Science" to stringResource(id = R.string.subject_science),
        "Social Science" to stringResource(id = R.string.subject_social_science),
        "English" to stringResource(id = R.string.subject_english)
    )

    val todayDate = LocalDate.now()
    val yesterdayDate = todayDate.minusDays(1)
    val dateFormatter = DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy")

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
    ) {
        item {
            ChildHeader(
                name = uiData.child.name,
                grade = uiData.child.grade,
                curriculum = uiData.child.curriculum
            )
        }

        // Time filter chips
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                periods.forEach { (period, labelRes) ->
                    FilterChip(
                        selected = uiData.selectedPeriod == period,
                        onClick = { onPeriodSelected(period) },
                        label = { Text(stringResource(id = labelRes)) },
                        modifier = Modifier.defaultMinSize(minHeight = 48.dp)
                    )
                }
            }
        }

        // Subject filter chips
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                subjects.forEach { (subj, label) ->
                    FilterChip(
                        selected = uiData.selectedSubject == subj,
                        onClick = { onSubjectSelected(subj) },
                        label = { Text(label) },
                        modifier = Modifier.defaultMinSize(minHeight = 48.dp)
                    )
                }
            }
        }

        // Grouped activities
        uiData.groupedActivities.forEach { (date, activities) ->
            item {
                val groupTitle = when (date) {
                    todayDate -> stringResource(id = R.string.activity_group_today)
                    yesterdayDate -> stringResource(id = R.string.activity_group_yesterday)
                    else -> date.format(dateFormatter)
                }
                Text(
                    text = groupTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
                )
            }

            items(activities, key = { it.id }) { activity ->
                ActivityRow(
                    activity = activity,
                    onClick = { onActivityClick(activity.id) }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ActivityScreenPreview() {
    RoavaiTheme {
        ActivityScreenContent(
            uiState = UiState.Success(
                ActivityUiData(
                    child = Child("c1", "Aarav", "Class 10", "CBSE", null, 60),
                    selectedPeriod = ActivityPeriod.TODAY,
                    selectedSubject = null,
                    groupedActivities = mapOf(
                        LocalDate.now() to listOf(
                            Activity(
                                id = "a1",
                                title = "Quadratic Equations",
                                subject = "Mathematics",
                                topic = "Algebra",
                                summary = "Completed 15 questions",
                                type = ActivityType.PRACTICE,
                                status = ActivityStatus.COMPLETED,
                                startedAt = Instant.now(),
                                durationMinutes = 20
                            )
                        )
                    )
                )
            ),
            unreadNotifications = 2,
            onPeriodSelected = {},
            onSubjectSelected = {},
            onActivityClick = {},
            onNavigateToRoute = {},
            onNotificationsClick = {},
            onProfileClick = {},
            onTitleLongClick = {},
            onRetry = {}
        )
    }
}
