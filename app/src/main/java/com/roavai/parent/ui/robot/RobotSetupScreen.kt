package com.roavai.parent.ui.robot

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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

            OutlinedTextField(
                value = code,
                onValueChange = {
                    code = it.uppercase()
                    errorMessage = null
                },
                label = { Text(text = "Pairing Code") },
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
                                successMessage = "Robot connected successfully!"
                                onNavigateBack()
                            }
                            is AppResult.Failure -> {
                                errorMessage = when (code) {
                                    "EXPIRED1" -> "This code has expired. Restart your robot to get a new one."
                                    "USED0001" -> "This code has already been used."
                                    else -> "Code not found. Check the code on your robot."
                                }
                            }
                            is AppResult.NoConnection -> {
                                errorMessage = "No network connection. Please check your internet."
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
