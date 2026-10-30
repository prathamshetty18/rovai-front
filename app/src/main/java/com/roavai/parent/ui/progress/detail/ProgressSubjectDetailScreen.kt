package com.roavai.parent.ui.progress.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.roavai.parent.domain.model.TopicMastery
import com.roavai.parent.domain.model.TopicStatus
import com.roavai.parent.ui.components.*
import com.roavai.parent.ui.navigation.NavRoutes
import com.roavai.parent.ui.navigation.RoavaiBottomBar
import com.roavai.parent.ui.theme.RoavaiTheme

@Composable
fun ProgressSubjectDetailScreen(
    viewModel: ProgressSubjectDetailViewModel,
    onNavigateToRoute: (String) -> Unit,
    onOpenDemoControls: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val unreadCount by viewModel.unreadCount.collectAsStateWithLifecycle()

    ScreenScaffold(
        title = stringResource(id = R.string.progress_subject_detail_title, viewModel.subject),
        unreadNotifications = unreadCount,
        onNotificationsClick = { onNavigateToRoute(NavRoutes.NOTIFICATIONS) },
        onProfileClick = { onNavigateToRoute(NavRoutes.PROFILE) },
        onTitleLongClick = onOpenDemoControls,
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
            onRetry = { viewModel.retry() },
            emptyContent = {
                EmptyState(
                    title = stringResource(id = R.string.common_empty_title),
                    message = stringResource(id = R.string.common_empty_message)
                )
            },
            modifier = Modifier.padding(innerPadding)
        ) { topics ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
            ) {
                items(topics, key = { it.topic }) { tm ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = tm.topic,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = "${tm.percent}%",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            LabeledProgressBar(label = "", percent = tm.percent)
                            Spacer(modifier = Modifier.height(8.dp))
                            val statusLabel = when (tm.status) {
                                TopicStatus.COMPLETED -> stringResource(id = R.string.progress_topics_status_completed)
                                TopicStatus.LEARNING -> stringResource(id = R.string.progress_topics_status_learning)
                                TopicStatus.NEEDS_PRACTICE -> stringResource(id = R.string.progress_topics_status_practice)
                            }
                            Text(
                                text = statusLabel,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (tm.status == TopicStatus.NEEDS_PRACTICE) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProgressSubjectDetailScreenPreview() {
    RoavaiTheme {
        Card(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Polynomials", fontWeight = FontWeight.Bold)
                Text("88%")
            }
        }
    }
}
