package com.roavai.parent.ui.goals

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.roavai.parent.R
import com.roavai.parent.core.util.UiState
import com.roavai.parent.domain.model.Goal
import com.roavai.parent.ui.components.ScreenScaffold
import com.roavai.parent.ui.components.StateHost
import com.roavai.parent.ui.navigation.NavRoutes
import com.roavai.parent.ui.navigation.RoavaiBottomBar
import com.roavai.parent.ui.theme.RoavaiTheme
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun GoalsScreen(
    viewModel: GoalsViewModel,
    onNavigateToRoute: (String) -> Unit,
    onOpenDemoControls: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val unreadCount by viewModel.unreadCount.collectAsStateWithLifecycle()

    GoalsScreenContent(
        uiState = uiState,
        unreadNotifications = unreadCount,
        onUpdateDailyGoal = { viewModel.updateDailyGoal(it) },
        onAddGoal = { title, subject, topic, date, time ->
            viewModel.addGoal(title, subject, topic, date, time)
        },
        onUpdateGoal = { viewModel.updateGoal(it) },
        onDeleteGoal = { viewModel.deleteGoal(it) },
        onRetry = { viewModel.retry() },
        onNavigateToRoute = onNavigateToRoute,
        onOpenDemoControls = onOpenDemoControls,
        modifier = modifier
    )
}

@Composable
fun GoalsScreenContent(
    uiState: UiState<GoalsUiData>,
    unreadNotifications: Int,
    onUpdateDailyGoal: (Int) -> Unit,
    onAddGoal: (String, String, String?, LocalDate, LocalTime?) -> Unit,
    onUpdateGoal: (Goal) -> Unit,
    onDeleteGoal: (String) -> Unit,
    onRetry: () -> Unit,
    onNavigateToRoute: (String) -> Unit,
    onOpenDemoControls: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var editingGoal by remember { mutableStateOf<Goal?>(null) }
    var goalToDelete by remember { mutableStateOf<Goal?>(null) }

    ScreenScaffold(
        title = stringResource(id = R.string.goals_title),
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
            GoalsSuccessBody(
                data = data,
                onUpdateDailyGoal = onUpdateDailyGoal,
                onOpenAddDialog = { showAddDialog = true },
                onEditGoal = { editingGoal = it },
                onConfirmDeleteGoal = { goalToDelete = it }
            )
        }
    }

    if (showAddDialog) {
        GoalFormDialog(
            goal = null,
            onDismiss = { showAddDialog = false },
            onSave = { title, subject, topic, date, time ->
                onAddGoal(title, subject, topic, date, time)
                showAddDialog = false
            }
        )
    }

    editingGoal?.let { goal ->
        GoalFormDialog(
            goal = goal,
            onDismiss = { editingGoal = null },
            onSave = { title, subject, topic, date, time ->
                onUpdateGoal(goal.copy(title = title, subject = subject, topic = topic, date = date, time = time))
                editingGoal = null
            }
        )
    }

    goalToDelete?.let { goal ->
        AlertDialog(
            onDismissRequest = { goalToDelete = null },
            title = { Text(text = stringResource(id = R.string.goals_delete_title)) },
            text = { Text(text = stringResource(id = R.string.goals_delete_message, goal.title)) },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteGoal(goal.id)
                        goalToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(text = stringResource(id = R.string.goals_delete_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { goalToDelete = null }) {
                    Text(text = stringResource(id = R.string.common_cancel))
                }
            }
        )
    }
}

@Composable
fun GoalsSuccessBody(
    data: GoalsUiData,
    onUpdateDailyGoal: (Int) -> Unit,
    onOpenAddDialog: () -> Unit,
    onEditGoal: (Goal) -> Unit,
    onConfirmDeleteGoal: (Goal) -> Unit,
    modifier: Modifier = Modifier
) {
    var dailyGoalMinutes by remember(data.dailyGoalMinutes) { mutableFloatStateOf(data.dailyGoalMinutes.toFloat()) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Daily learning goal setting card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(id = R.string.goals_daily_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = stringResource(id = R.string.goals_daily_subtitle),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(id = R.string.goals_daily_minutes_format, dailyGoalMinutes.toInt()),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Slider(
                        value = dailyGoalMinutes,
                        onValueChange = { dailyGoalMinutes = (it / 5).toInt() * 5f },
                        valueRange = 5f..480f,
                        steps = ((480 - 5) / 5) - 1,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { onUpdateDailyGoal(dailyGoalMinutes.toInt()) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = 48.dp)
                    ) {
                        Text(text = stringResource(id = R.string.goals_save_daily_goal))
                    }
                }
            }
        }

        // Upcoming goals section header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.goals_upcoming_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Button(
                    onClick = onOpenAddDialog,
                    modifier = Modifier.defaultMinSize(minHeight = 48.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = stringResource(id = R.string.goals_add_goal_button))
                }
            }
        }

        // Goals list
        if (data.goals.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(id = R.string.goals_empty_title),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(id = R.string.goals_empty_message),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(data.goals, key = { it.id }) { goal ->
                GoalItemCard(
                    goal = goal,
                    onEdit = { onEditGoal(goal) },
                    onDelete = { onConfirmDeleteGoal(goal) }
                )
            }
        }
    }
}

@Composable
fun GoalItemCard(
    goal: Goal,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = goal.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        SuggestionChip(
                            onClick = {},
                            label = { Text(text = goal.subject) },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                        goal.topic?.let { topic ->
                            if (topic.isNotBlank()) {
                                SuggestionChip(
                                    onClick = {},
                                    label = { Text(text = topic) }
                                )
                            }
                        }
                    }
                }

                Row {
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = stringResource(id = R.string.goals_edit_action))
                    }
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = stringResource(id = R.string.goals_delete_action),
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            val timeText = goal.time?.format(DateTimeFormatter.ofPattern("h:mm a")) ?: ""
            val dateText = goal.date.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
            val scheduleText = if (timeText.isNotBlank()) "$dateText at $timeText" else dateText

            Text(
                text = scheduleText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalFormDialog(
    goal: Goal?,
    onDismiss: () -> Unit,
    onSave: (title: String, subject: String, topic: String?, date: LocalDate, time: LocalTime?) -> Unit
) {
    var title by remember { mutableStateOf(goal?.title ?: "") }
    var subject by remember { mutableStateOf(goal?.subject ?: "Mathematics") }
    var topic by remember { mutableStateOf(goal?.topic ?: "") }
    var daysOffset by remember { mutableIntStateOf(1) } // 1 = tomorrow, 2 = D+2, 3 = D+3
    var hour by remember { mutableIntStateOf(goal?.time?.hour ?: 17) }
    var minute by remember { mutableIntStateOf(goal?.time?.minute ?: 0) }

    var expandedSubjectDropdown by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val subjects = listOf("Mathematics", "Science", "Social Science", "English")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(
                    id = if (goal == null) R.string.goals_add_dialog_title else R.string.goals_edit_dialog_title
                )
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        errorMessage = null
                    },
                    label = { Text(text = stringResource(id = R.string.goals_field_title)) },
                    placeholder = { Text(text = stringResource(id = R.string.goals_field_title_hint)) },
                    modifier = Modifier.fillMaxWidth()
                )

                // Subject dropdown
                ExposedDropdownMenuBox(
                    expanded = expandedSubjectDropdown,
                    onExpandedChange = { expandedSubjectDropdown = it },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = subject,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(text = stringResource(id = R.string.goals_field_subject)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSubjectDropdown) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedSubjectDropdown,
                        onDismissRequest = { expandedSubjectDropdown = false }
                    ) {
                        subjects.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(text = item) },
                                onClick = {
                                    subject = item
                                    expandedSubjectDropdown = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = topic,
                    onValueChange = { topic = it },
                    label = { Text(text = stringResource(id = R.string.goals_field_topic)) },
                    placeholder = { Text(text = stringResource(id = R.string.goals_field_topic_hint)) },
                    modifier = Modifier.fillMaxWidth()
                )

                // Target Date picker / selector (cannot be in the past: today, tomorrow, etc.)
                Text(
                    text = stringResource(id = R.string.goals_field_date),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = daysOffset == 0,
                        onClick = { daysOffset = 0 },
                        label = { Text("Today") },
                        modifier = Modifier.defaultMinSize(minHeight = 48.dp)
                    )
                    FilterChip(
                        selected = daysOffset == 1,
                        onClick = { daysOffset = 1 },
                        label = { Text("Tomorrow") },
                        modifier = Modifier.defaultMinSize(minHeight = 48.dp)
                    )
                    FilterChip(
                        selected = daysOffset == 2,
                        onClick = { daysOffset = 2 },
                        label = { Text("+2 Days") },
                        modifier = Modifier.defaultMinSize(minHeight = 48.dp)
                    )
                }

                // Target Time selector
                Text(
                    text = stringResource(id = R.string.goals_field_time),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = hour == 16 && minute == 30,
                        onClick = { hour = 16; minute = 30 },
                        label = { Text("4:30 PM") },
                        modifier = Modifier.defaultMinSize(minHeight = 48.dp)
                    )
                    FilterChip(
                        selected = hour == 17 && minute == 0,
                        onClick = { hour = 17; minute = 0 },
                        label = { Text("5:00 PM") },
                        modifier = Modifier.defaultMinSize(minHeight = 48.dp)
                    )
                    FilterChip(
                        selected = hour == 18 && minute == 0,
                        onClick = { hour = 18; minute = 0 },
                        label = { Text("6:00 PM") },
                        modifier = Modifier.defaultMinSize(minHeight = 48.dp)
                    )
                }

                errorMessage?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isBlank()) {
                        errorMessage = "Please enter an activity name"
                        return@Button
                    }
                    val targetDate = LocalDate.now().plusDays(daysOffset.toLong())
                    if (targetDate.isBefore(LocalDate.now())) {
                        errorMessage = "Date cannot be in the past"
                        return@Button
                    }
                    val targetTime = LocalTime.of(hour, minute)
                    onSave(title.trim(), subject, topic.trim().ifEmpty { null }, targetDate, targetTime)
                },
                modifier = Modifier.defaultMinSize(minHeight = 48.dp)
            ) {
                Text(text = stringResource(id = R.string.goals_save))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.defaultMinSize(minHeight = 48.dp)
            ) {
                Text(text = stringResource(id = R.string.common_cancel))
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun GoalsScreenPreview() {
    val sampleGoals = listOf(
        Goal("g1", "Practice Algebra", "Mathematics", "Pair of Linear Equations", LocalDate.now().plusDays(1), LocalTime.of(17, 0), false),
        Goal("g2", "Revise Acids, Bases & Salts", "Science", "Acids, Bases & Salts", LocalDate.now().plusDays(2), LocalTime.of(16, 30), false)
    )

    RoavaiTheme {
        GoalsScreenContent(
            uiState = UiState.Success(GoalsUiData(dailyGoalMinutes = 60, goals = sampleGoals)),
            unreadNotifications = 2,
            onUpdateDailyGoal = {},
            onAddGoal = { _, _, _, _, _ -> },
            onUpdateGoal = {},
            onDeleteGoal = {},
            onRetry = {},
            onNavigateToRoute = {},
            onOpenDemoControls = {}
        )
    }
}
