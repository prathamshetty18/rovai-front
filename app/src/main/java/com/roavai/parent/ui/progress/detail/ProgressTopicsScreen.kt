package com.roavai.parent.ui.progress.detail

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
fun ProgressTopicsScreen(
    viewModel: ProgressTopicsViewModel,
    onNavigateToRoute: (String) -> Unit,
    onOpenDemoControls: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val unreadCount by viewModel.unreadCount.collectAsStateWithLifecycle()

    val subjects = listOf(
        null to stringResource(id = R.string.activity_filter_all_subjects),
        "Mathematics" to stringResource(id = R.string.subject_mathematics),
        "Science" to stringResource(id = R.string.subject_science),
        "Social Science" to stringResource(id = R.string.subject_social_science),
        "English" to stringResource(id = R.string.subject_english)
    )

    ScreenScaffold(
        title = stringResource(id = R.string.progress_topics_title),
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
            val completed = topics.filter { it.status == TopicStatus.COMPLETED }
            val learning = topics.filter { it.status == TopicStatus.LEARNING }
            val needsPractice = topics.filter { it.status == TopicStatus.NEEDS_PRACTICE }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        subjects.forEach { (subj, label) ->
                            FilterChip(
                                selected = viewModel.selectedSubject == subj,
                                onClick = { viewModel.setSubject(subj) },
                                label = { Text(label) },
                                modifier = Modifier.defaultMinSize(minHeight = 48.dp)
                            )
                        }
                    }
                }

                if (completed.isNotEmpty()) {
                    item {
                        Text(
                            text = stringResource(id = R.string.progress_topics_status_completed) + " (${completed.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                    items(completed, key = { "${it.subject}_${it.topic}" }) { tm ->
                        TopicMasteryItem(topic = tm)
                    }
                }

                if (learning.isNotEmpty()) {
                    item {
                        Text(
                            text = stringResource(id = R.string.progress_topics_status_learning) + " (${learning.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.padding(top = 12.dp)
                        )
                    }
                    items(learning, key = { "${it.subject}_${it.topic}" }) { tm ->
                        TopicMasteryItem(topic = tm)
                    }
                }

                if (needsPractice.isNotEmpty()) {
                    item {
                        Text(
                            text = stringResource(id = R.string.progress_topics_status_practice) + " (${needsPractice.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(top = 12.dp)
                        )
                    }
                    items(needsPractice, key = { "${it.subject}_${it.topic}" }) { tm ->
                        TopicMasteryItem(topic = tm)
                    }
                }
            }
        }
    }
}

@Composable
fun TopicMasteryItem(topic: TopicMastery, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = topic.topic,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = topic.subject,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = "${topic.percent}%",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            LabeledProgressBar(label = "", percent = topic.percent)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProgressTopicsScreenPreview() {
    RoavaiTheme {
        TopicMasteryItem(topic = TopicMastery("Mathematics", "Quadratic Equations", 85, TopicStatus.COMPLETED))
    }
}
