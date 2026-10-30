package com.roavai.parent.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
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
import com.roavai.parent.domain.model.Child
import com.roavai.parent.domain.model.Parent
import com.roavai.parent.ui.components.ScreenScaffold
import com.roavai.parent.ui.components.StateHost
import com.roavai.parent.ui.navigation.NavRoutes
import com.roavai.parent.ui.navigation.RoavaiBottomBar
import com.roavai.parent.ui.theme.RoavaiTheme
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onNavigateToRoute: (String) -> Unit,
    onOpenDemoControls: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val unreadCount by viewModel.unreadCount.collectAsStateWithLifecycle()

    ProfileScreenContent(
        uiState = uiState,
        unreadNotifications = unreadCount,
        onToggleVoice = { viewModel.toggleVoiceConsent(it) },
        onToggleExpression = { viewModel.toggleExpressionConsent(it) },
        onToggleReasoning = { viewModel.toggleReasoningConsent(it) },
        onToggleTraining = { viewModel.toggleTrainingConsent(it) },
        onLogout = {
            viewModel.logout {
                onNavigateToRoute(NavRoutes.LOGIN)
            }
        },
        onRetry = { viewModel.retry() },
        onNavigateToRoute = onNavigateToRoute,
        onOpenDemoControls = onOpenDemoControls,
        modifier = modifier
    )
}

@Composable
fun ProfileScreenContent(
    uiState: UiState<ProfileUiData>,
    unreadNotifications: Int,
    onToggleVoice: (Boolean) -> Unit,
    onToggleExpression: (Boolean) -> Unit,
    onToggleReasoning: (Boolean) -> Unit,
    onToggleTraining: (Boolean) -> Unit,
    onLogout: () -> Unit,
    onRetry: () -> Unit,
    onNavigateToRoute: (String) -> Unit,
    onOpenDemoControls: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showExportDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val exportSuccessMsg = stringResource(id = R.string.profile_export_success_message)
    val deleteSuccessMsg = stringResource(id = R.string.profile_delete_success_message)

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            RoavaiBottomBar(
                currentRoute = NavRoutes.PROFILE,
                onNavigateToRoute = onNavigateToRoute
            )
        },
        modifier = modifier
    ) { scaffoldPadding ->
        ScreenScaffold(
            title = stringResource(id = R.string.profile_screen_title),
            description = stringResource(id = R.string.profile_screen_description),
            unreadNotifications = unreadNotifications,
            onMenuClick = {},
            onNotificationsClick = { onNavigateToRoute(NavRoutes.NOTIFICATIONS) },
            onProfileClick = {},
            onTitleLongClick = onOpenDemoControls,
            modifier = Modifier.padding(scaffoldPadding)
        ) { innerPadding ->
            StateHost(
                state = uiState,
                onRetry = onRetry,
                modifier = Modifier.padding(innerPadding)
            ) { data ->
                ProfileSuccessBody(
                    data = data,
                    onToggleVoice = onToggleVoice,
                    onToggleExpression = onToggleExpression,
                    onToggleReasoning = onToggleReasoning,
                    onToggleTraining = onToggleTraining,
                    onExportClick = { showExportDialog = true },
                    onDeleteClick = { showDeleteDialog = true },
                    onLogoutClick = { showLogoutDialog = true },
                    onDemoControlsClick = onOpenDemoControls
                )
            }
        }
    }

    if (showExportDialog) {
        AlertDialog(
            onDismissRequest = { showExportDialog = false },
            title = { Text(text = stringResource(id = R.string.profile_export_dialog_title)) },
            text = { Text(text = stringResource(id = R.string.profile_export_dialog_message)) },
            confirmButton = {
                Button(
                    onClick = {
                        showExportDialog = false
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(exportSuccessMsg)
                        }
                    },
                    modifier = Modifier.defaultMinSize(minHeight = 48.dp)
                ) {
                    Text(text = stringResource(id = R.string.profile_export_confirm_button))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showExportDialog = false },
                    modifier = Modifier.defaultMinSize(minHeight = 48.dp)
                ) {
                    Text(text = stringResource(id = R.string.common_cancel))
                }
            }
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(text = stringResource(id = R.string.profile_delete_dialog_title)) },
            text = { Text(text = stringResource(id = R.string.profile_delete_dialog_message)) },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(deleteSuccessMsg)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.defaultMinSize(minHeight = 48.dp)
                ) {
                    Text(text = stringResource(id = R.string.profile_delete_confirm_button))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false },
                    modifier = Modifier.defaultMinSize(minHeight = 48.dp)
                ) {
                    Text(text = stringResource(id = R.string.common_cancel))
                }
            }
        )
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text(text = stringResource(id = R.string.profile_logout_dialog_title)) },
            text = { Text(text = stringResource(id = R.string.profile_logout_dialog_message)) },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.defaultMinSize(minHeight = 48.dp)
                ) {
                    Text(text = stringResource(id = R.string.profile_logout_button))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showLogoutDialog = false },
                    modifier = Modifier.defaultMinSize(minHeight = 48.dp)
                ) {
                    Text(text = stringResource(id = R.string.common_cancel))
                }
            }
        )
    }
}

@Composable
fun ProfileSuccessBody(
    data: ProfileUiData,
    onToggleVoice: (Boolean) -> Unit,
    onToggleExpression: (Boolean) -> Unit,
    onToggleReasoning: (Boolean) -> Unit,
    onToggleTraining: (Boolean) -> Unit,
    onExportClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onDemoControlsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Parent details card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(id = R.string.profile_parent_section),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
                ProfileInfoRow(label = stringResource(id = R.string.profile_label_name), value = data.parent.fullName)
                ProfileInfoRow(label = stringResource(id = R.string.profile_label_email), value = data.parent.email)
                ProfileInfoRow(label = stringResource(id = R.string.profile_label_mobile), value = data.parent.mobile ?: "—")
            }
        }

        // Child details card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(id = R.string.profile_child_section),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
                ProfileInfoRow(label = stringResource(id = R.string.profile_label_name), value = data.child.name)
                ProfileInfoRow(label = stringResource(id = R.string.profile_label_grade), value = data.child.grade)
                ProfileInfoRow(label = stringResource(id = R.string.profile_label_board), value = data.child.curriculum)
                ProfileInfoRow(
                    label = stringResource(id = R.string.profile_label_daily_goal),
                    value = stringResource(id = R.string.goals_daily_minutes_format, data.child.dailyGoalMinutes)
                )
            }
        }

        // Privacy & Consents card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(id = R.string.profile_privacy_section),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                ConsentSwitchRow(
                    label = stringResource(id = R.string.profile_consent_voice),
                    checked = data.voiceConsent,
                    onCheckedChange = onToggleVoice
                )
                ConsentSwitchRow(
                    label = stringResource(id = R.string.profile_consent_expression),
                    checked = data.expressionConsent,
                    onCheckedChange = onToggleExpression
                )
                ConsentSwitchRow(
                    label = stringResource(id = R.string.profile_consent_reasoning),
                    checked = data.reasoningConsent,
                    onCheckedChange = onToggleReasoning
                )
                ConsentSwitchRow(
                    label = stringResource(id = R.string.profile_consent_training),
                    checked = data.trainingConsent,
                    onCheckedChange = onToggleTraining
                )
            }
        }

        // Data Management & Account card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.profile_data_management_section),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                OutlinedButton(
                    onClick = onExportClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 48.dp)
                ) {
                    Icon(imageVector = Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = stringResource(id = R.string.profile_export_data_button))
                }

                OutlinedButton(
                    onClick = onDeleteClick,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 48.dp)
                ) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = stringResource(id = R.string.profile_delete_data_button))
                }

                OutlinedButton(
                    onClick = onDemoControlsClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 48.dp)
                ) {
                    Icon(imageVector = Icons.Default.Build, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = stringResource(id = R.string.profile_demo_controls_button))
                }

                Button(
                    onClick = onLogoutClick,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 48.dp)
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = stringResource(id = R.string.profile_logout_button))
                }
            }
        }
    }
}

@Composable
fun ProfileInfoRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun ConsentSwitchRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.defaultMinSize(minHeight = 48.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    val sampleParent = Parent("p1", "Meera D'Souza", "parent@example.com", "+91 90000 00000")
    val sampleChild = Child("c1", "Aarav D'Souza", "Class 10", "CBSE", null, 60)

    RoavaiTheme {
        ProfileScreenContent(
            uiState = UiState.Success(ProfileUiData(parent = sampleParent, child = sampleChild)),
            unreadNotifications = 1,
            onToggleVoice = {},
            onToggleExpression = {},
            onToggleReasoning = {},
            onToggleTraining = {},
            onLogout = {},
            onRetry = {},
            onNavigateToRoute = {},
            onOpenDemoControls = {}
        )
    }
}
