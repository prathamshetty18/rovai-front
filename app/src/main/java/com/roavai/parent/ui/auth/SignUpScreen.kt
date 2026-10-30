package com.roavai.parent.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.roavai.parent.R
import com.roavai.parent.domain.model.Parent
import com.roavai.parent.ui.components.ScreenScaffold
import com.roavai.parent.ui.navigation.NavRoutes
import com.roavai.parent.ui.theme.RoavaiTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SignUpScreen(
    viewModel: SignUpViewModel,
    onNavigateToRoute: (String) -> Unit,
    onOpenDemoControls: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SignUpScreenContent(
        uiState = uiState,
        onSignUp = { name, email, mobile, password ->
            viewModel.signUp(name, email, mobile, password)
        },
        onNavigateToRoute = onNavigateToRoute,
        onOpenDemoControls = onOpenDemoControls,
        modifier = modifier
    )
}

@Composable
fun SignUpScreenContent(
    uiState: SignUpUiState,
    onSignUp: (String, String, String?, String) -> Unit,
    onNavigateToRoute: (String) -> Unit,
    onOpenDemoControls: () -> Unit,
    modifier: Modifier = Modifier
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var mobile by remember { mutableStateOf("") }

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    var nameTouched by remember { mutableStateOf(false) }
    var emailTouched by remember { mutableStateOf(false) }
    var passwordTouched by remember { mutableStateOf(false) }
    var confirmPasswordTouched by remember { mutableStateOf(false) }

    var submitted by remember { mutableStateOf(false) }

    val nameError = if (submitted || nameTouched) {
        if (fullName.isBlank()) stringResource(id = R.string.signup_error_name_required) else null
    } else null

    val emailError = if (submitted || emailTouched) {
        when {
            email.isBlank() -> stringResource(id = R.string.signup_error_email_required)
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches() ->
                stringResource(id = R.string.signup_error_email_invalid)
            else -> null
        }
    } else null

    val passwordError = if (submitted || passwordTouched) {
        if (password.length < 8) stringResource(id = R.string.signup_error_password_length) else null
    } else null

    val confirmPasswordError = if (submitted || confirmPasswordTouched) {
        if (confirmPassword != password) stringResource(id = R.string.signup_error_password_match) else null
    } else null

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val successMessage = stringResource(id = R.string.signup_success_created)

    LaunchedEffect(uiState) {
        if (uiState is SignUpUiState.Success) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar(successMessage)
            }
            delay(500)
            onNavigateToRoute(NavRoutes.ADD_CHILD)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier
    ) { scaffoldPadding ->
        ScreenScaffold(
            title = stringResource(id = R.string.signup_title),
            onTitleLongClick = onOpenDemoControls,
            modifier = Modifier.padding(scaffoldPadding)
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
                    text = stringResource(id = R.string.signup_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Full Name
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text(text = stringResource(id = R.string.signup_field_name)) },
                    isError = nameError != null,
                    supportingText = nameError?.let { { Text(text = it) } },
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { if (!it.isFocused && fullName.isNotEmpty()) nameTouched = true }
                )

                // Email Address
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text(text = stringResource(id = R.string.signup_field_email)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    isError = emailError != null || (uiState is SignUpUiState.Error && email.trim() == "taken@example.com"),
                    supportingText = {
                        if (emailError != null) {
                            Text(text = emailError)
                        } else if (uiState is SignUpUiState.Error && email.trim() == "taken@example.com") {
                            Text(text = stringResource(id = R.string.signup_error_email_taken))
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { if (!it.isFocused && email.isNotEmpty()) emailTouched = true }
                )

                // Password
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text(text = stringResource(id = R.string.signup_field_password)) },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(
                            onClick = { passwordVisible = !passwordVisible },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = null
                            )
                        }
                    },
                    isError = passwordError != null,
                    supportingText = passwordError?.let { { Text(text = it) } },
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { if (!it.isFocused && password.isNotEmpty()) passwordTouched = true }
                )

                // Confirm Password
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text(text = stringResource(id = R.string.signup_field_confirm_password)) },
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(
                            onClick = { confirmPasswordVisible = !confirmPasswordVisible },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                imageVector = if (confirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = null
                            )
                        }
                    },
                    isError = confirmPasswordError != null,
                    supportingText = confirmPasswordError?.let { { Text(text = it) } },
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { if (!it.isFocused && confirmPassword.isNotEmpty()) confirmPasswordTouched = true }
                )

                // Mobile (optional)
                OutlinedTextField(
                    value = mobile,
                    onValueChange = { mobile = it },
                    label = { Text(text = stringResource(id = R.string.signup_field_mobile)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth()
                )

                // General Error
                if (uiState is SignUpUiState.Error && email.trim() != "taken@example.com") {
                    Text(
                        text = uiState.message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        submitted = true
                        nameTouched = true
                        emailTouched = true
                        passwordTouched = true
                        confirmPasswordTouched = true

                        val valid = fullName.isNotBlank() &&
                                email.isNotBlank() &&
                                android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches() &&
                                password.length >= 8 &&
                                confirmPassword == password

                        if (valid) {
                            onSignUp(fullName.trim(), email.trim(), mobile.trim().ifEmpty { null }, password)
                        }
                    },
                    enabled = uiState !is SignUpUiState.Loading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 48.dp)
                ) {
                    if (uiState is SignUpUiState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text(text = stringResource(id = R.string.signup_btn_submit))
                    }
                }

                TextButton(
                    onClick = { onNavigateToRoute(NavRoutes.LOGIN) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 48.dp)
                ) {
                    Text(text = stringResource(id = R.string.signup_to_login_prompt))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SignUpScreenPreview() {
    RoavaiTheme {
        SignUpScreenContent(
            uiState = SignUpUiState.Idle,
            onSignUp = { _, _, _, _ -> },
            onNavigateToRoute = {},
            onOpenDemoControls = {}
        )
    }
}
