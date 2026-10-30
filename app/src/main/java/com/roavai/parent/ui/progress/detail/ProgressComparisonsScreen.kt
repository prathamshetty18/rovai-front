package com.roavai.parent.ui.progress.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Remove
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
import com.roavai.parent.domain.model.ComparisonRow
import com.roavai.parent.ui.components.*
import com.roavai.parent.ui.navigation.NavRoutes
import com.roavai.parent.ui.navigation.RoavaiBottomBar
import com.roavai.parent.ui.theme.RoavaiTheme

@Composable
fun ProgressComparisonsScreen(
    viewModel: ProgressComparisonsViewModel,
    onNavigateToRoute: (String) -> Unit,
    onOpenDemoControls: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val unreadCount by viewModel.unreadCount.collectAsStateWithLifecycle()

    ScreenScaffold(
        title = stringResource(id = R.string.progress_comparisons_title),
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
        ) { comparisons ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
            ) {
                item {
                    PeriodSelector(
                        selectedPeriod = viewModel.selectedPeriod,
                        onPeriodSelected = { viewModel.setPeriod(it) }
                    )
                }

                // Table headers
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Metric",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(2f)
                        )
                        Text(
                            text = stringResource(id = R.string.progress_comparisons_current),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(1.2f)
                        )
                        Text(
                            text = stringResource(id = R.string.progress_comparisons_previous),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(1.2f)
                        )
                        Text(
                            text = stringResource(id = R.string.progress_comparisons_change),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(1.6f)
                        )
                    }
                }

                // Overall Metrics Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            ComparisonRowView(row = comparisons.learningTime)
                            HorizontalDivider()
                            ComparisonRowView(row = comparisons.overallProgress)
                            HorizontalDivider()
                            ComparisonRowView(row = comparisons.testAverage)
                        }
                    }
                }

                if (comparisons.subjects.isNotEmpty()) {
                    item {
                        Text(
                            text = stringResource(id = R.string.progress_comparisons_subjects),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
                        )
                    }

                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.medium,
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                comparisons.subjects.forEachIndexed { index, row ->
                                    if (index > 0) HorizontalDivider()
                                    ComparisonRowView(row = row)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ComparisonRowView(row: ComparisonRow, modifier: Modifier = Modifier) {
    val isUp = row.change.startsWith("up", ignoreCase = true)
    val isDown = row.change.startsWith("down", ignoreCase = true)
    val tint = when {
        isUp -> MaterialTheme.colorScheme.primary
        isDown -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = row.label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(2f)
        )
        Text(
            text = row.current,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.weight(1.2f)
        )
        Text(
            text = row.previous,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1.2f)
        )
        Row(
            modifier = Modifier.weight(1.6f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val icon = when {
                isUp -> Icons.Default.ArrowUpward
                isDown -> Icons.Default.ArrowDownward
                else -> Icons.Default.Remove
            }
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = row.change,
                style = MaterialTheme.typography.bodySmall,
                color = tint,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProgressComparisonsScreenPreview() {
    RoavaiTheme {
        Card(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            ComparisonRowView(row = ComparisonRow("Learning time", "300 min", "215 min", "up 85 min"))
        }
    }
}
