package com.roavai.parent.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.roavai.parent.core.util.AppResult
import com.roavai.parent.domain.model.Parent
import com.roavai.parent.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface SignUpUiState {
    data object Idle : SignUpUiState
    data object Loading : SignUpUiState
    data class Success(val parent: Parent) : SignUpUiState
    data class Error(val message: String) : SignUpUiState
}

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<SignUpUiState>(SignUpUiState.Idle)
    val uiState: StateFlow<SignUpUiState> = _uiState.asStateFlow()

    fun signUp(fullName: String, email: String, mobile: String?, password: String) {
        viewModelScope.launch {
            _uiState.value = SignUpUiState.Loading
            when (val result = authRepository.signUp(fullName, email, mobile, password)) {
                is AppResult.Success -> {
                    _uiState.value = SignUpUiState.Success(result.data)
                }
                is AppResult.Failure -> {
                    _uiState.value = SignUpUiState.Error(result.message)
                }
                is AppResult.NoConnection -> {
                    _uiState.value = SignUpUiState.Error("No network connection. Please check your internet.")
                }
            }
        }
    }

    fun resetState() {
        _uiState.value = SignUpUiState.Idle
    }
}
