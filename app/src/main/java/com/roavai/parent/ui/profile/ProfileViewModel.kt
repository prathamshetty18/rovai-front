package com.roavai.parent.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.roavai.parent.core.util.AppResult
import com.roavai.parent.core.util.UiState
import com.roavai.parent.data.fake.DemoSettings
import com.roavai.parent.domain.model.Child
import com.roavai.parent.domain.model.Parent
import com.roavai.parent.domain.repository.AuthRepository
import com.roavai.parent.domain.repository.ChildRepository
import com.roavai.parent.domain.repository.NotificationsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiData(
    val parent: Parent,
    val child: Child,
    val voiceConsent: Boolean = true,
    val expressionConsent: Boolean = true,
    val reasoningConsent: Boolean = true,
    val trainingConsent: Boolean = false
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val childRepository: ChildRepository,
    private val notificationsRepository: NotificationsRepository,
    private val demoSettings: DemoSettings
) : ViewModel() {

    val unreadCount: StateFlow<Int> = notificationsRepository.unreadCountFlow

    private val _uiState = MutableStateFlow<UiState<ProfileUiData>>(UiState.Loading)
    val uiState: StateFlow<UiState<ProfileUiData>> = _uiState.asStateFlow()

    private var lastSuccessfulData: ProfileUiData? = null

    init {
        viewModelScope.launch {
            demoSettings.state.collectLatest {
                loadProfile()
            }
        }
    }

    fun retry() {
        viewModelScope.launch {
            loadProfile()
        }
    }

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val parentResult = authRepository.currentParent()
            val childResult = childRepository.currentChild()

            if (parentResult is AppResult.Failure) {
                _uiState.value = UiState.Error(parentResult.message)
                return@launch
            }
            if (parentResult is AppResult.NoConnection || childResult is AppResult.NoConnection) {
                _uiState.value = UiState.Offline(cached = lastSuccessfulData)
                return@launch
            }

            val parent = (parentResult as? AppResult.Success)?.data ?: Parent("p1", "Meera D'Souza", "parent@example.com", "+91 90000 00000")
            val child = (childResult as? AppResult.Success)?.data ?: Child("c1", "Aarav D'Souza", "Class 10", "CBSE", null, 60)

            val currentData = lastSuccessfulData
            val data = ProfileUiData(
                parent = parent,
                child = child,
                voiceConsent = currentData?.voiceConsent ?: true,
                expressionConsent = currentData?.expressionConsent ?: true,
                reasoningConsent = currentData?.reasoningConsent ?: true,
                trainingConsent = currentData?.trainingConsent ?: false
            )
            lastSuccessfulData = data
            _uiState.value = UiState.Success(data)
        }
    }

    fun toggleVoiceConsent(value: Boolean) {
        val current = (uiState.value as? UiState.Success)?.data ?: return
        val updated = current.copy(voiceConsent = value)
        lastSuccessfulData = updated
        _uiState.value = UiState.Success(updated)
    }

    fun toggleExpressionConsent(value: Boolean) {
        val current = (uiState.value as? UiState.Success)?.data ?: return
        val updated = current.copy(expressionConsent = value)
        lastSuccessfulData = updated
        _uiState.value = UiState.Success(updated)
    }

    fun toggleReasoningConsent(value: Boolean) {
        val current = (uiState.value as? UiState.Success)?.data ?: return
        val updated = current.copy(reasoningConsent = value)
        lastSuccessfulData = updated
        _uiState.value = UiState.Success(updated)
    }

    fun toggleTrainingConsent(value: Boolean) {
        val current = (uiState.value as? UiState.Success)?.data ?: return
        val updated = current.copy(trainingConsent = value)
        lastSuccessfulData = updated
        _uiState.value = UiState.Success(updated)
    }

    fun logout(onComplete: () -> Unit) {
        viewModelScope.launch {
            authRepository.logout()
            onComplete()
        }
    }
}
