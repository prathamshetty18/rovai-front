package com.roavai.parent.ui.achievements

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.roavai.parent.R
import com.roavai.parent.core.util.UiState
import com.roavai.parent.domain.model.Achievement
import com.roavai.parent.domain.model.AchievementKind
import com.roavai.parent.ui.components.AchievementChip
import com.roavai.parent.ui.components.EmptyState
import com.roavai.parent.ui.components.ScreenScaffold
import com.roavai.parent.ui.components.StateHost
import com.roavai.parent.ui.navigation.NavRoutes
import com.roavai.parent.ui.navigation.RoavaiBottomBar
import com.roavai.parent.ui.theme.RoavaiTheme
import java.time.Instant

@Composable
fun AchievementsScreen(
    viewModel: AchievementsViewModel,
    onNavigateToRoute: (String) -> Unit,
    onOpenDemoControls: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val unreadCount by viewModel.unreadCount.collectAsStateWithLifecycle()

    AchievementsScreenContent(
        uiState = uiState,
        unreadNotifications = unreadCount,
        onRetry = { viewModel.retry() },
        onNavigateToRoute = onNavigateToRoute,
        onOpenDemoControls = onOpenDemoControls,
        modifier = modifier
    )
}

@Composable
fun AchievementsScreenContent(
    uiState: UiState<List<Achievement>>,
    unreadNotifications: Int,
    onRetry: () -> Unit,
    onNavigateToRoute: (String) -> Unit,
    onOpenDemoControls: () -> Unit,
    modifier: Modifier = Modifier
) {
    ScreenScaffold(
        title = stringResource(id = R.string.achievements_screen_title),
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
            emptyContent = {
                EmptyState(
                    title = stringResource(id = R.string.achievements_empty_title),
                    message = stringResource(id = R.string.achievements_empty_message)
                )
            },
            modifier = Modifier.padding(innerPadding)
        ) { achievements ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(achievements, key = { it.id }) { achievement ->
                    AchievementChip(
                        achievement = achievement,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AchievementsScreenPreview() {
    val sampleAchievements = listOf(
        Achievement("ac1", AchievementKind.STREAK, "7-day learning streak", "Learned every day this week", Instant.now()),
        Achievement("ac2", AchievementKind.SUBJECT_ACHIEVEMENT, "Topic Master: Quadratic Equations", "Mastered a Mathematics topic", Instant.now()),
        Achievement("ac3", AchievementKind.CONSISTENT_LEARNING, "Consistent Learner", "Learned 5 days in a row", Instant.now())
    )

    RoavaiTheme {
        AchievementsScreenContent(
            uiState = UiState.Success(sampleAchievements),
            unreadNotifications = 2,
            onRetry = {},
            onNavigateToRoute = {},
            onOpenDemoControls = {}
        )
    }
}
