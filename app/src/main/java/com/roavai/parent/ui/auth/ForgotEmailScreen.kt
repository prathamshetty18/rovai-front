package com.roavai.parent.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.roavai.parent.R
import com.roavai.parent.ui.components.ScreenScaffold
import com.roavai.parent.ui.navigation.NavRoutes
import com.roavai.parent.ui.theme.RoavaiTheme

@Composable
fun ForgotEmailScreen(
    viewModel: ForgotPasswordViewModel,
    onNavigateToOtp: () -> Unit,
    onOpenDemoControls: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState) {
        if (uiState is ForgotPasswordState.OtpSent) {
            onNavigateToOtp()
        }
    }

    ForgotEmailContent(
        isLoading = uiState is ForgotPasswordState.Loading,
        onSendOtp = { email -> viewModel.sendOtp(email) },
        onOpenDemoControls = onOpenDemoControls,
        modifier = modifier
    )
}

@Composable
fun ForgotEmailContent(
    isLoading: Boolean,
    onSendOtp: (String) -> Unit,
    onOpenDemoControls: () -> Unit,
    modifier: Modifier = Modifier
) {
    var email by remember { mutableStateOf("parent@example.com") }

    ScreenScaffold(
        title = stringResource(id = R.string.forgot_email_title),
        onTitleLongClick = onOpenDemoControls,
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(id = R.string.forgot_email_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(text = stringResource(id = R.string.login_field_email)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { onSendOtp(email.trim()) },
                enabled = !isLoading && email.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 48.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(text = stringResource(id = R.string.forgot_email_btn_send))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ForgotEmailScreenPreview() {
    RoavaiTheme {
        ForgotEmailContent(
            isLoading = false,
            onSendOtp = {},
            onOpenDemoControls = {}
        )
    }
}
