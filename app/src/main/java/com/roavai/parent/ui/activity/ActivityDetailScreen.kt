package com.roavai.parent.ui.activity

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.roavai.parent.domain.model.ActivityStatus
import com.roavai.parent.domain.model.ActivityType
import com.roavai.parent.ui.components.EmptyState
import com.roavai.parent.ui.components.ScreenScaffold
import com.roavai.parent.ui.components.StateHost
import com.roavai.parent.ui.navigation.NavRoutes
import com.roavai.parent.ui.navigation.RoavaiBottomBar
import com.roavai.parent.ui.theme.RoavaiTheme
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun ActivityDetailScreen(
    viewModel: ActivityDetailViewModel,
    onNavigateToRoute: (String) -> Unit,
    onOpenDemoControls: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val unreadCount by viewModel.unreadNotificationsCount.collectAsStateWithLifecycle()

    ActivityDetailScreenContent(
        uiState = uiState,
        unreadNotifications = unreadCount,
        onNavigateToRoute = onNavigateToRoute,
        onNotificationsClick = { onNavigateToRoute(NavRoutes.NOTIFICATIONS) },
        onProfileClick = { onNavigateToRoute(NavRoutes.PROFILE) },
        onTitleLongClick = onOpenDemoControls,
        onRetry = { viewModel.retry() },
        modifier = modifier
    )
}

@Composable
fun ActivityDetailScreenContent(
    uiState: UiState<Activity>,
    unreadNotifications: Int,
    onNavigateToRoute: (String) -> Unit,
    onNotificationsClick: () -> Unit,
    onProfileClick: () -> Unit,
    onTitleLongClick: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    ScreenScaffold(
        title = stringResource(id = R.string.activity_detail_title),
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
                    title = stringResource(id = R.string.common_empty_title),
                    message = stringResource(id = R.string.common_empty_message)
                )
            },
            modifier = Modifier.padding(innerPadding)
        ) { activity ->
            ActivityDetailBody(activity = activity)
        }
    }
}

@Composable
fun ActivityDetailBody(
    activity: Activity,
    modifier: Modifier = Modifier
) {
    val formatter = DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy 'at' hh:mm a")
    val formattedDateTime = activity.startedAt.atZone(ZoneId.systemDefault()).format(formatter)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Title
            DetailField(
                label = stringResource(id = R.string.activity_field_title),
                value = activity.title,
                isLarge = true
            )

            HorizontalDivider()

            // 2. Subject
            DetailField(
                label = stringResource(id = R.string.activity_field_subject),
                value = activity.subject
            )

            // 3. Topic
            activity.topic?.let { topic ->
                DetailField(
                    label = stringResource(id = R.string.activity_field_topic),
                    value = topic
                )
            }

            // 4. What the child did
            DetailField(
                label = stringResource(id = R.string.activity_field_summary),
                value = activity.summary
            )

            // 5. Activity Type
            DetailField(
                label = stringResource(id = R.string.activity_field_type),
                value = activity.type.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() }
            )

            // 6. Date & Time
            DetailField(
                label = stringResource(id = R.string.activity_field_datetime),
                value = formattedDateTime
            )

            // 7. Completion Status
            DetailField(
                label = stringResource(id = R.string.activity_field_status),
                value = activity.status.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() }
            )
        }
    }
}

@Composable
private fun DetailField(
    label: String,
    value: String,
    isLarge: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = if (isLarge) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyLarge,
            fontWeight = if (isLarge) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ActivityDetailScreenPreview() {
    RoavaiTheme {
        ActivityDetailScreenContent(
            uiState = UiState.Success(
                Activity(
                    id = "a1",
                    title = "Mathematics: Quadratic Equations",
                    subject = "Mathematics",
                    topic = "Quadratic Equations",
                    summary = "Completed 15 practice questions",
                    type = ActivityType.PRACTICE,
                    status = ActivityStatus.COMPLETED,
                    startedAt = Instant.now(),
                    durationMinutes = 15
                )
            ),
            unreadNotifications = 0,
            onNavigateToRoute = {},
            onNotificationsClick = {},
            onProfileClick = {},
            onTitleLongClick = {},
            onRetry = {}
        )
    }
}
