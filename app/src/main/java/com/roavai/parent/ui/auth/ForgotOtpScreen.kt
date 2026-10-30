package com.roavai.parent.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.roavai.parent.R
import com.roavai.parent.ui.components.ScreenScaffold
import com.roavai.parent.ui.theme.RoavaiTheme

@Composable
fun ForgotOtpScreen(
    viewModel: ForgotPasswordViewModel,
    onNavigateToNewPassword: () -> Unit,
    onOpenDemoControls: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val countdown by viewModel.resendCountdown.collectAsStateWithLifecycle()

    LaunchedEffect(uiState) {
        if (uiState is ForgotPasswordState.OtpVerified) {
            onNavigateToNewPassword()
        }
    }

    ForgotOtpContent(
        uiState = uiState,
        countdown = countdown,
        onVerifyOtp = { otp -> viewModel.verifyOtp(otp) },
        onResendOtp = { viewModel.resendOtp() },
        onOpenDemoControls = onOpenDemoControls,
        modifier = modifier
    )
}

@Composable
fun ForgotOtpContent(
    uiState: ForgotPasswordState,
    countdown: Int,
    onVerifyOtp: (String) -> Unit,
    onResendOtp: () -> Unit,
    onOpenDemoControls: () -> Unit,
    modifier: Modifier = Modifier
) {
    var otp by remember { mutableStateOf("123456") }

    ScreenScaffold(
        title = stringResource(id = R.string.forgot_otp_title),
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
                text = stringResource(id = R.string.forgot_otp_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OutlinedTextField(
                value = otp,
                onValueChange = {
                    if (it.length <= 6) {
                        otp = it
                    }
                },
                label = { Text(text = stringResource(id = R.string.forgot_otp_field_label)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            if (uiState is ForgotPasswordState.Error) {
                Text(
                    text = stringResource(id = R.string.forgot_otp_error_incorrect),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { onVerifyOtp(otp.trim()) },
                enabled = uiState !is ForgotPasswordState.Loading && otp.length == 6,
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 48.dp)
            ) {
                if (uiState is ForgotPasswordState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(text = stringResource(id = R.string.forgot_otp_btn_verify))
                }
            }

            // Resend Countdown or Resend Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (countdown > 0) {
                    Text(
                        text = stringResource(id = R.string.forgot_otp_resend_countdown, countdown),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    TextButton(
                        onClick = onResendOtp,
                        modifier = Modifier.defaultMinSize(minHeight = 48.dp)
                    ) {
                        Text(text = stringResource(id = R.string.forgot_otp_resend_button))
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ForgotOtpScreenPreview() {
    RoavaiTheme {
        ForgotOtpContent(
            uiState = ForgotPasswordState.Idle,
            countdown = 24,
            onVerifyOtp = {},
            onResendOtp = {},
            onOpenDemoControls = {}
        )
    }
}
