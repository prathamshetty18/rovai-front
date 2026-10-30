package com.roavai.parent.ui.robot

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.roavai.parent.domain.model.RobotStatus
import com.roavai.parent.domain.model.WifiSignal
import com.roavai.parent.ui.components.ScreenScaffold
import com.roavai.parent.ui.components.StateHost
import com.roavai.parent.ui.navigation.NavRoutes
import com.roavai.parent.ui.navigation.RoavaiBottomBar
import com.roavai.parent.ui.theme.RoavaiTheme
import java.time.Instant

@Composable
fun RobotScreen(
    viewModel: RobotViewModel,
    onNavigateToRoute: (String) -> Unit,
    onOpenDemoControls: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val unreadCount by viewModel.unreadCount.collectAsStateWithLifecycle()

    RobotScreenContent(
        uiState = uiState,
        unreadNotifications = unreadCount,
        onReconnect = { onNavigateToRoute(NavRoutes.ROBOT_SETUP) },
        onUnpair = { viewModel.unpair() },
        onRetry = { viewModel.retry() },
        onNavigateToRoute = onNavigateToRoute,
        onOpenDemoControls = onOpenDemoControls,
        modifier = modifier
    )
}

@Composable
fun RobotScreenContent(
    uiState: UiState<RobotStatus?>,
    unreadNotifications: Int,
    onReconnect: () -> Unit,
    onUnpair: () -> Unit,
    onRetry: () -> Unit,
    onNavigateToRoute: (String) -> Unit,
    onOpenDemoControls: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showUnpairDialog by remember { mutableStateOf(false) }

    ScreenScaffold(
        title = stringResource(id = R.string.robot_tab_title),
        description = stringResource(id = R.string.robot_tab_description),
        unreadNotifications = unreadNotifications,
        onMenuClick = {},
        onNotificationsClick = { onNavigateToRoute(NavRoutes.NOTIFICATIONS) },
        onProfileClick = { onNavigateToRoute(NavRoutes.PROFILE) },
        onTitleLongClick = onOpenDemoControls,
        bottomBar = {
            RoavaiBottomBar(
                currentRoute = NavRoutes.ROBOT,
                onNavigateToRoute = onNavigateToRoute
            )
        },
        modifier = modifier
    ) { innerPadding ->
        StateHost(
            state = uiState,
            onRetry = onRetry,
            modifier = Modifier.padding(innerPadding)
        ) { status ->
            if (status == null) {
                RobotUnpairedBody(
                    onConnectClick = onReconnect
                )
            } else {
                RobotStatusBody(
                    status = status,
                    onReconnectClick = onReconnect,
                    onUnpairClick = { showUnpairDialog = true }
                )
            }
        }
    }

    if (showUnpairDialog) {
        AlertDialog(
            onDismissRequest = { showUnpairDialog = false },
            title = { Text(text = stringResource(id = R.string.robot_unpair_confirm_title)) },
            text = { Text(text = stringResource(id = R.string.robot_unpair_confirm_message)) },
            confirmButton = {
                Button(
                    onClick = {
                        onUnpair()
                        showUnpairDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(text = stringResource(id = R.string.robot_unpair_confirm_button))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showUnpairDialog = false },
                    modifier = Modifier.defaultMinSize(minHeight = 48.dp)
                ) {
                    Text(text = stringResource(id = R.string.common_cancel))
                }
            }
        )
    }
}

@Composable
fun RobotUnpairedBody(
    onConnectClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
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
                Icon(
                    imageVector = Icons.Default.SmartToy,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.outline
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(id = R.string.robot_empty_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(id = R.string.robot_empty_message),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = onConnectClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 48.dp)
                ) {
                    Text(text = stringResource(id = R.string.robot_connect_button))
                }
            }
        }
    }
}

@Composable
fun RobotStatusBody(
    status: RobotStatus,
    onReconnectClick: () -> Unit,
    onUnpairClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDisconnected = !status.connected
    val statusContainerColor = if (isDisconnected) {
        MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
    } else {
        MaterialTheme.colorScheme.surface
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Status Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(containerColor = statusContainerColor)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val statusColor = if (isDisconnected) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(statusColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isDisconnected) Icons.Default.Warning else Icons.Default.SmartToy,
                            contentDescription = null,
                            tint = statusColor,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isDisconnected) {
                                stringResource(id = R.string.robot_disconnected)
                            } else {
                                stringResource(id = R.string.robot_connected)
                            },
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = statusColor
                        )
                        val lastActiveText = if (isDisconnected) "3 hours ago" else "5 minutes ago"
                        Text(
                            text = stringResource(id = R.string.robot_last_active_format, lastActiveText),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))

                // Connection details grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Wi-Fi
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (status.wifiConnected) Icons.Default.Wifi else Icons.Default.WifiOff,
                            contentDescription = null,
                            tint = if (status.wifiConnected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (status.wifiConnected) stringResource(id = R.string.robot_wifi_connected) else stringResource(id = R.string.robot_wifi_disconnected),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    // Battery
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.BatteryFull,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(id = R.string.robot_battery_format, status.batteryPercent),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(id = R.string.robot_firmware_format, status.firmwareVersion ?: "1.0.3"),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Action buttons
        Button(
            onClick = onReconnectClick,
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 48.dp)
        ) {
            Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = stringResource(id = R.string.robot_reconnect))
        }

        OutlinedButton(
            onClick = onUnpairClick,
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 48.dp)
        ) {
            Text(text = stringResource(id = R.string.robot_unpair))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RobotScreenConnectedPreview() {
    val sampleStatus = RobotStatus(
        connected = true,
        wifiConnected = true,
        wifiSignal = WifiSignal.STRONG,
        batteryPercent = 82,
        lastActive = Instant.now(),
        firmwareVersion = "1.0.3"
    )

    RoavaiTheme {
        RobotScreenContent(
            uiState = UiState.Success(sampleStatus),
            unreadNotifications = 1,
            onReconnect = {},
            onUnpair = {},
            onRetry = {},
            onNavigateToRoute = {},
            onOpenDemoControls = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RobotScreenDisconnectedPreview() {
    val sampleStatus = RobotStatus(
        connected = false,
        wifiConnected = false,
        wifiSignal = WifiSignal.WEAK,
        batteryPercent = 82,
        lastActive = Instant.now().minusSeconds(10800),
        firmwareVersion = "1.0.3"
    )

    RoavaiTheme {
        RobotScreenContent(
            uiState = UiState.Success(sampleStatus),
            unreadNotifications = 0,
            onReconnect = {},
            onUnpair = {},
            onRetry = {},
            onNavigateToRoute = {},
            onOpenDemoControls = {}
        )
    }
}
