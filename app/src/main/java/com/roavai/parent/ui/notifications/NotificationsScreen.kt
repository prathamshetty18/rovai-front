package com.roavai.parent.ui.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.roavai.parent.domain.model.AppNotification
import com.roavai.parent.domain.model.NotificationType
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
fun NotificationsScreen(
    viewModel: NotificationsViewModel,
    onNavigateToRoute: (String) -> Unit,
    onOpenDemoControls: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val unreadCount by viewModel.unreadCount.collectAsStateWithLifecycle()

    NotificationsScreenContent(
        uiState = uiState,
        unreadNotifications = unreadCount,
        onMarkRead = { viewModel.markRead(it) },
        onMarkAllRead = { viewModel.markAllRead() },
        onRetry = { viewModel.retry() },
        onNavigateToRoute = onNavigateToRoute,
        onOpenDemoControls = onOpenDemoControls,
        modifier = modifier
    )
}

@Composable
fun NotificationsScreenContent(
    uiState: UiState<List<AppNotification>>,
    unreadNotifications: Int,
    onMarkRead: (String) -> Unit,
    onMarkAllRead: () -> Unit,
    onRetry: () -> Unit,
    onNavigateToRoute: (String) -> Unit,
    onOpenDemoControls: () -> Unit,
    modifier: Modifier = Modifier
) {
    ScreenScaffold(
        title = stringResource(id = R.string.notifications_title),
        unreadNotifications = unreadNotifications,
        onMenuClick = {},
        onNotificationsClick = {},
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
            emptyContent = {
                EmptyState(
                    title = stringResource(id = R.string.notifications_empty_title),
                    message = stringResource(id = R.string.notifications_empty_message)
                )
            },
            modifier = Modifier.padding(innerPadding)
        ) { list ->
            NotificationsListBody(
                notifications = list,
                unreadCount = unreadNotifications,
                onMarkRead = onMarkRead,
                onMarkAllRead = onMarkAllRead
            )
        }
    }
}

@Composable
fun NotificationsListBody(
    notifications: List<AppNotification>,
    unreadCount: Int,
    onMarkRead: (String) -> Unit,
    onMarkAllRead: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (unreadCount > 0) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onMarkAllRead,
                        modifier = Modifier.defaultMinSize(minHeight = 48.dp)
                    ) {
                        Icon(imageVector = Icons.Default.DoneAll, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = stringResource(id = R.string.notifications_mark_all_read))
                    }
                }
            }
        }

        items(notifications, key = { it.id }) { notification ->
            NotificationCard(
                notification = notification,
                onClick = {
                    if (!notification.read) {
                        onMarkRead(notification.id)
                    }
                }
            )
        }
    }
}

@Composable
fun NotificationCard(
    notification: AppNotification,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor = if (notification.read) {
        MaterialTheme.colorScheme.surface
    } else {
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            val (icon, tint) = when (notification.type) {
                NotificationType.ACHIEVEMENT -> Icons.Default.EmojiEvents to MaterialTheme.colorScheme.primary
                NotificationType.LEARNING_UPDATE -> Icons.Default.School to MaterialTheme.colorScheme.tertiary
                NotificationType.ROBOT_UPDATE -> Icons.Default.SmartToy to MaterialTheme.colorScheme.secondary
                NotificationType.IMPORTANT -> Icons.Default.Info to MaterialTheme.colorScheme.error
            }

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(tint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = tint,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = notification.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = if (notification.read) FontWeight.Medium else FontWeight.Bold
                    )
                    if (!notification.read) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = notification.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))
                val formatter = DateTimeFormatter.ofPattern("MMM dd, hh:mm a").withZone(ZoneId.systemDefault())
                Text(
                    text = formatter.format(notification.createdAt),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NotificationsScreenPreview() {
    val sampleNotifications = listOf(
        AppNotification("n1", "New achievement", "Aarav reached a 7-day learning streak.", NotificationType.ACHIEVEMENT, false, Instant.now()),
        AppNotification("n2", "Today's learning", "Aarav completed 3 activities today.", NotificationType.LEARNING_UPDATE, true, Instant.now().minusSeconds(7200))
    )

    RoavaiTheme {
        NotificationsScreenContent(
            uiState = UiState.Success(sampleNotifications),
            unreadNotifications = 1,
            onMarkRead = {},
            onMarkAllRead = {},
            onRetry = {},
            onNavigateToRoute = {},
            onOpenDemoControls = {}
        )
    }
}
