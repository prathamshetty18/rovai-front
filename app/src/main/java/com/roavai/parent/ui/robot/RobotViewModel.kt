package com.roavai.parent.ui.robot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.roavai.parent.core.util.AppResult
import com.roavai.parent.core.util.UiState
import com.roavai.parent.data.fake.DemoSettings
import com.roavai.parent.domain.model.RobotStatus
import com.roavai.parent.domain.repository.NotificationsRepository
import com.roavai.parent.domain.repository.RobotRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RobotViewModel @Inject constructor(
    private val robotRepository: RobotRepository,
    private val notificationsRepository: NotificationsRepository,
    private val demoSettings: DemoSettings
) : ViewModel() {

    val unreadCount: StateFlow<Int> = notificationsRepository.unreadCountFlow

    private val _uiState = MutableStateFlow<UiState<RobotStatus?>>(UiState.Loading)
    val uiState: StateFlow<UiState<RobotStatus?>> = _uiState.asStateFlow()

    private var lastSuccessfulStatus: RobotStatus? = null

    init {
        viewModelScope.launch {
            combine(demoSettings.state, demoSettings.robotDisconnected) { state, disconnected ->
                Pair(state, disconnected)
            }.collectLatest {
                loadStatus()
            }
        }
    }

    fun retry() {
        viewModelScope.launch {
            loadStatus()
        }
    }

    fun loadStatus() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            when (val result = robotRepository.status()) {
                is AppResult.Failure -> {
                    _uiState.value = UiState.Error(result.message)
                }
                is AppResult.NoConnection -> {
                    _uiState.value = UiState.Offline(cached = lastSuccessfulStatus)
                }
                is AppResult.Success -> {
                    lastSuccessfulStatus = result.data
                    _uiState.value = UiState.Success(result.data)
                }
            }
        }
    }

    fun unpair(onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            robotRepository.unpair()
            loadStatus()
            onComplete()
        }
    }

    fun pair(code: String, onResult: (AppResult<RobotStatus>) -> Unit) {
        viewModelScope.launch {
            val result = robotRepository.pair(code)
            if (result is AppResult.Success) {
                loadStatus()
            }
            onResult(result)
        }
    }
}
