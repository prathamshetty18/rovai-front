package com.roavai.parent.ui.robot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.roavai.parent.core.util.AppResult
import com.roavai.parent.core.util.UiState
import com.roavai.parent.data.fake.DemoSettings
import com.roavai.parent.domain.model.RobotStatus
import com.roavai.parent.domain.repository.RobotRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface RobotSetupState {
    data object Idle : RobotSetupState
    data object Connecting : RobotSetupState
    data class Success(val robotStatus: RobotStatus) : RobotSetupState
    data class Error(val message: String) : RobotSetupState
}

@HiltViewModel
class RobotSetupViewModel @Inject constructor(
    private val robotRepository: RobotRepository,
    private val demoSettings: DemoSettings
) : ViewModel() {

    private val _setupState = MutableStateFlow<RobotSetupState>(RobotSetupState.Idle)
    val setupState: StateFlow<RobotSetupState> = _setupState.asStateFlow()

    private var lastEnteredCode: String = ""

    init {
        viewModelScope.launch {
            demoSettings.state.collectLatest {
                // If in error or idle, can reset or retry
            }
        }
    }

    fun setCode(code: String) {
        lastEnteredCode = code
        if (_setupState.value is RobotSetupState.Error) {
            _setupState.value = RobotSetupState.Idle
        }
    }

    fun connect(code: String) {
        lastEnteredCode = code
        viewModelScope.launch {
            _setupState.value = RobotSetupState.Connecting
            delay(400) // slight realistic simulation
            val result = robotRepository.pair(code.trim().uppercase())
            when (result) {
                is AppResult.Success -> {
                    _setupState.value = RobotSetupState.Success(result.data)
                }
                is AppResult.Failure -> {
                    _setupState.value = RobotSetupState.Error(result.message)
                }
                is AppResult.NoConnection -> {
                    _setupState.value = RobotSetupState.Error("No network connection. Please check your internet.")
                }
            }
        }
    }

    fun retry() {
        if (lastEnteredCode.isNotBlank()) {
            connect(lastEnteredCode)
        } else {
            _setupState.value = RobotSetupState.Idle
        }
    }

    fun reset() {
        _setupState.value = RobotSetupState.Idle
    }
}
