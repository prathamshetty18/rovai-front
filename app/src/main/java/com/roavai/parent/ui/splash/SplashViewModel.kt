package com.roavai.parent.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.roavai.parent.core.util.AppResult
import com.roavai.parent.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface SplashNavigationTarget {
    data object Idle : SplashNavigationTarget
    data object Home : SplashNavigationTarget
    data object RobotSetup : SplashNavigationTarget
}

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _navigationTarget = MutableStateFlow<SplashNavigationTarget>(SplashNavigationTarget.Idle)
    val navigationTarget: StateFlow<SplashNavigationTarget> = _navigationTarget.asStateFlow()

    init {
        checkAuthAndNavigate()
    }

    private fun checkAuthAndNavigate() {
        viewModelScope.launch {
            val startTime = System.currentTimeMillis()
            val isLoggedInResult = authRepository.isLoggedIn()
            val elapsed = System.currentTimeMillis() - startTime
            val remainingDelay = (1500L - elapsed).coerceAtLeast(0L)
            delay(remainingDelay)

            val loggedIn = when (isLoggedInResult) {
                is AppResult.Success -> isLoggedInResult.data
                else -> false
            }

            _navigationTarget.value = if (loggedIn) {
                SplashNavigationTarget.Home
            } else {
                SplashNavigationTarget.RobotSetup
            }
        }
    }
}
