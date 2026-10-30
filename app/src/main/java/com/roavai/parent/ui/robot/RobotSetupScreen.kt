package com.roavai.parent.ui.robot

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.roavai.parent.R
import com.roavai.parent.domain.model.RobotStatus
import com.roavai.parent.domain.model.WifiSignal
import com.roavai.parent.ui.components.ScreenScaffold
import com.roavai.parent.ui.theme.RoavaiTheme
import java.time.Instant

@Composable
fun RobotSetupScreen(
    viewModel: RobotSetupViewModel,
    onContinue: () -> Unit,
    onOpenDemoControls: () -> Unit,
    modifier: Modifier = Modifier
) {
    val setupState by viewModel.setupState.collectAsStateWithLifecycle()

    RobotSetupScreenContent(
        setupState = setupState,
        onConnect = { code -> viewModel.connect(code) },
        onRetry = { viewModel.retry() },
        onContinue = onContinue,
        onOpenDemoControls = onOpenDemoControls,
        modifier = modifier
    )
}

@Composable
fun RobotSetupScreenContent(
    setupState: RobotSetupState,
    onConnect: (String) -> Unit,
    onRetry: () -> Unit,
    onContinue: () -> Unit,
    onOpenDemoControls: () -> Unit,
    modifier: Modifier = Modifier
) {
    var code by remember { mutableStateOf("") }

    ScreenScaffold(
        title = stringResource(id = R.string.robot_setup_main_title),
        onTitleLongClick = onOpenDemoControls,
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.SmartToy,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(40.dp)
                )
            }

            Text(
                text = stringResource(id = R.string.robot_setup_instruction),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OutlinedTextField(
                value = code,
                onValueChange = {
                    if (it.length <= 8) {
                        code = it.uppercase()
                    }
                },
                enabled = setupState !is RobotSetupState.Connecting && setupState !is RobotSetupState.Success,
                label = { Text(text = stringResource(id = R.string.robot_setup_code_label)) },
                placeholder = { Text(text = stringResource(id = R.string.robot_setup_code_hint)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            when (setupState) {
                RobotSetupState.Idle -> {
                    Button(
                        onClick = { onConnect(code) },
                        enabled = code.isNotBlank(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = 48.dp)
                    ) {
                        Text(text = stringResource(id = R.string.robot_setup_connect_button))
                    }
                }
                RobotSetupState.Connecting -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(36.dp))
                        Text(
                            text = stringResource(id = R.string.robot_setup_connecting_message),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                is RobotSetupState.Success -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = stringResource(id = R.string.robot_setup_connected_success),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Button(
                            onClick = onContinue,
                            modifier = Modifier
                                .fillMaxWidth()
                                .defaultMinSize(minHeight = 48.dp)
                        ) {
                            Text(text = stringResource(id = R.string.robot_setup_continue_button))
                        }
                    }
                }
                is RobotSetupState.Error -> {
                    val displayError = when {
                        code == "EXPIRED1" || setupState.message.contains("expired", ignoreCase = true) ->
                            stringResource(id = R.string.robot_setup_error_expired)
                        code == "USED0001" || setupState.message.contains("already been used", ignoreCase = true) ->
                            stringResource(id = R.string.robot_setup_error_used)
                        setupState.message.contains("network", ignoreCase = true) || setupState.message.contains("offline", ignoreCase = true) ->
                            stringResource(id = R.string.robot_setup_error_offline)
                        else ->
                            stringResource(id = R.string.robot_setup_error_not_found)
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = displayError,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center
                            )
                        }

                        Button(
                            onClick = onRetry,
                            modifier = Modifier
                                .fillMaxWidth()
                                .defaultMinSize(minHeight = 48.dp)
                        ) {
                            Text(text = stringResource(id = R.string.common_retry))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(id = R.string.robot_setup_alt_note),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RobotSetupScreenIdlePreview() {
    RoavaiTheme {
        RobotSetupScreenContent(
            setupState = RobotSetupState.Idle,
            onConnect = {},
            onRetry = {},
            onContinue = {},
            onOpenDemoControls = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RobotSetupScreenSuccessPreview() {
    val sampleStatus = RobotStatus(
        connected = true,
        wifiConnected = true,
        wifiSignal = WifiSignal.STRONG,
        batteryPercent = 82,
        lastActive = Instant.now(),
        firmwareVersion = "1.0.3"
    )

    RoavaiTheme {
        RobotSetupScreenContent(
            setupState = RobotSetupState.Success(sampleStatus),
            onConnect = {},
            onRetry = {},
            onContinue = {},
            onOpenDemoControls = {}
        )
    }
}
