package com.roavai.parent.data.fake

import com.roavai.parent.core.util.AppResult
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

enum class DemoState {
    NORMAL,
    LOADING,
    EMPTY,
    ERROR,
    OFFLINE
}

@Singleton
class DemoSettings @Inject constructor() {
    private val _state = MutableStateFlow(DemoState.NORMAL)
    val state: StateFlow<DemoState> = _state.asStateFlow()

    private val _robotDisconnected = MutableStateFlow(false)
    val robotDisconnected: StateFlow<Boolean> = _robotDisconnected.asStateFlow()

    fun setDemoState(newState: DemoState) {
        _state.value = newState
    }

    fun setRobotDisconnected(disconnected: Boolean) {
        _robotDisconnected.value = disconnected
    }

    /**
     * Intercepts repository calls to simulate demo states.
     * - NORMAL: Delays 600ms then executes real sample fetch.
     * - LOADING: Calls awaitCancellation() to simulate indefinite loading.
     * - EMPTY: Returns Success with zero values and empty lists (robot = null). Screens convert this into the Empty UI state.
     * - ERROR: Returns Failure with a debug message (screens map to localized error strings).
     * - OFFLINE: Returns NoConnection (screens map to offline state).
     */
    suspend fun <T> applyDemoState(
        emptyValue: () -> T,
        fetch: suspend () -> AppResult<T>
    ): AppResult<T> {
        return when (_state.value) {
            DemoState.NORMAL -> {
                delay(600)
                fetch()
            }
            DemoState.LOADING -> {
                awaitCancellation()
            }
            DemoState.EMPTY -> {
                AppResult.Success(emptyValue())
            }
            DemoState.ERROR -> {
                AppResult.Failure("Debug: Simulated error from DemoSettings")
            }
            DemoState.OFFLINE -> {
                AppResult.NoConnection
            }
        }
    }
}
