package com.roavai.parent.ui.activity

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.roavai.parent.core.util.AppResult
import com.roavai.parent.core.util.UiState
import com.roavai.parent.data.fake.DemoSettings
import com.roavai.parent.domain.model.Activity
import com.roavai.parent.domain.repository.ActivityRepository
import com.roavai.parent.domain.repository.NotificationsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActivityDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val activityRepository: ActivityRepository,
    private val notificationsRepository: NotificationsRepository,
    private val demoSettings: DemoSettings
) : ViewModel() {

    val activityId: String = checkNotNull(savedStateHandle["id"])

    private val _uiState = MutableStateFlow<UiState<Activity>>(UiState.Loading)
    val uiState: StateFlow<UiState<Activity>> = _uiState.asStateFlow()

    val unreadNotificationsCount: StateFlow<Int> = notificationsRepository.unreadCountFlow

    init {
        viewModelScope.launch {
            demoSettings.state.collect {
                loadActivity()
            }
        }
    }

    fun retry() {
        loadActivity()
    }

    private fun loadActivity() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            when (val result = activityRepository.activity(activityId)) {
                is AppResult.Success -> {
                    val activity = result.data
                    if (activity == null) {
                        _uiState.value = UiState.Empty
                    } else {
                        _uiState.value = UiState.Success(activity)
                    }
                }
                is AppResult.Failure -> {
                    _uiState.value = UiState.Error(result.message)
                }
                is AppResult.NoConnection -> {
                    _uiState.value = UiState.Offline()
                }
            }
        }
    }
}
