package com.roavai.parent.ui.robot

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.roavai.parent.R
import com.roavai.parent.core.util.AppResult
import com.roavai.parent.ui.components.ScreenScaffold
import com.roavai.parent.ui.navigation.NavRoutes

@Composable
fun RobotSetupScreen(
    viewModel: RobotViewModel,
    onNavigateBack: () -> Unit,
    onOpenDemoControls: () -> Unit,
    modifier: Modifier = Modifier
) {
    var code by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    ScreenScaffold(
        title = stringResource(id = R.string.robot_setup_screen_title),
        onTitleLongClick = onOpenDemoControls,
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.robot_setup_instruction),
                style = MaterialTheme.typography.bodyLarge
            )

            val successStr = stringResource(id = R.string.robot_setup_success_message)
            val expiredStr = stringResource(id = R.string.robot_setup_error_expired)
            val usedStr = stringResource(id = R.string.robot_setup_error_used)
            val notFoundStr = stringResource(id = R.string.robot_setup_error_not_found)
            val offlineStr = stringResource(id = R.string.robot_setup_error_offline)

            OutlinedTextField(
                value = code,
                onValueChange = {
                    code = it.uppercase()
                    errorMessage = null
                },
                label = { Text(text = stringResource(id = R.string.robot_setup_code_label)) },
                placeholder = { Text(text = stringResource(id = R.string.robot_setup_code_hint)) },
                modifier = Modifier.fillMaxWidth()
            )

            errorMessage?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            successMessage?.let { success ->
                Text(
                    text = success,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    viewModel.pair(code) { result ->
                        when (result) {
                            is AppResult.Success -> {
                                successMessage = successStr
                                onNavigateBack()
                            }
                            is AppResult.Failure -> {
                                errorMessage = when (code) {
                                    "EXPIRED1" -> expiredStr
                                    "USED0001" -> usedStr
                                    else -> notFoundStr
                                }
                            }
                            is AppResult.NoConnection -> {
                                errorMessage = offlineStr
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 48.dp)
            ) {
                Text(text = stringResource(id = R.string.robot_setup_pair_button))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RobotSetupScreenPreview() {
    com.roavai.parent.ui.theme.RoavaiTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.robot_setup_instruction),
                style = MaterialTheme.typography.bodyLarge
            )
            OutlinedTextField(
                value = "WINI2026",
                onValueChange = {},
                label = { Text(text = stringResource(id = R.string.robot_setup_code_label)) },
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 48.dp)
            ) {
                Text(text = stringResource(id = R.string.robot_setup_pair_button))
            }
        }
    }
}

