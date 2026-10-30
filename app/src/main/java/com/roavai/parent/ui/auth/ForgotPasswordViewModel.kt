package com.roavai.parent.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.roavai.parent.core.util.AppResult
import com.roavai.parent.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ForgotPasswordState {
    data object Idle : ForgotPasswordState
    data object Loading : ForgotPasswordState
    data class OtpSent(val email: String) : ForgotPasswordState
    data object OtpVerified : ForgotPasswordState
    data object PasswordResetSuccess : ForgotPasswordState
    data class Error(val message: String) : ForgotPasswordState
}

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ForgotPasswordState>(ForgotPasswordState.Idle)
    val uiState: StateFlow<ForgotPasswordState> = _uiState.asStateFlow()

    private val _resendCountdown = MutableStateFlow(30)
    val resendCountdown: StateFlow<Int> = _resendCountdown.asStateFlow()

    var savedEmail: String = "parent@example.com"
        private set

    private var countdownJob: Job? = null

    fun sendOtp(email: String) {
        savedEmail = email.trim()
        viewModelScope.launch {
            _uiState.value = ForgotPasswordState.Loading
            when (val result = authRepository.requestOtp(savedEmail)) {
                is AppResult.Success -> {
                    _uiState.value = ForgotPasswordState.OtpSent(savedEmail)
                    startCountdown()
                }
                is AppResult.Failure -> {
                    _uiState.value = ForgotPasswordState.Error(result.message)
                }
                is AppResult.NoConnection -> {
                    _uiState.value = ForgotPasswordState.Error("No network connection. Please check your internet.")
                }
            }
        }
    }

    fun verifyOtp(otp: String) {
        viewModelScope.launch {
            _uiState.value = ForgotPasswordState.Loading
            when (val result = authRepository.verifyOtp(savedEmail, otp.trim())) {
                is AppResult.Success -> {
                    _uiState.value = ForgotPasswordState.OtpVerified
                }
                is AppResult.Failure -> {
                    _uiState.value = ForgotPasswordState.Error(result.message)
                }
                is AppResult.NoConnection -> {
                    _uiState.value = ForgotPasswordState.Error("No network connection. Please check your internet.")
                }
            }
        }
    }

    fun resendOtp() {
        if (_resendCountdown.value > 0) return
        sendOtp(savedEmail)
    }

    private fun startCountdown() {
        countdownJob?.cancel()
        _resendCountdown.value = 30
        countdownJob = viewModelScope.launch {
            while (_resendCountdown.value > 0) {
                delay(1000)
                _resendCountdown.value -= 1
            }
        }
    }

    fun resetPassword(newPassword: String) {
        viewModelScope.launch {
            _uiState.value = ForgotPasswordState.Loading
            when (val result = authRepository.resetPassword(savedEmail, newPassword)) {
                is AppResult.Success -> {
                    _uiState.value = ForgotPasswordState.PasswordResetSuccess
                }
                is AppResult.Failure -> {
                    _uiState.value = ForgotPasswordState.Error(result.message)
                }
                is AppResult.NoConnection -> {
                    _uiState.value = ForgotPasswordState.Error("No network connection. Please check your internet.")
                }
            }
        }
    }

    fun clearError() {
        if (_uiState.value is ForgotPasswordState.Error) {
            _uiState.value = ForgotPasswordState.Idle
        }
    }
}
