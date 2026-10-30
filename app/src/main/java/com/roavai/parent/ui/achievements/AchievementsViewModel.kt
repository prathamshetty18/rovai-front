package com.roavai.parent.ui.achievements

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.roavai.parent.core.util.AppResult
import com.roavai.parent.core.util.UiState
import com.roavai.parent.data.fake.DemoSettings
import com.roavai.parent.data.fake.SampleData
import com.roavai.parent.domain.model.Achievement
import com.roavai.parent.domain.repository.NotificationsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AchievementsViewModel @Inject constructor(
    private val sampleData: SampleData,
    private val notificationsRepository: NotificationsRepository,
    private val demoSettings: DemoSettings
) : ViewModel() {

    val unreadCount: StateFlow<Int> = notificationsRepository.unreadCountFlow

    private val _uiState = MutableStateFlow<UiState<List<Achievement>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<Achievement>>> = _uiState.asStateFlow()

    private var lastSuccessfulList: List<Achievement>? = null

    init {
        viewModelScope.launch {
            demoSettings.state.collectLatest {
                loadAchievements()
            }
        }
    }

    fun retry() {
        viewModelScope.launch {
            loadAchievements()
        }
    }

    fun loadAchievements() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val result = demoSettings.applyDemoState(
                emptyValue = { emptyList<Achievement>() }
            ) {
                AppResult.Success(sampleData.achievements)
            }

            when (result) {
                is AppResult.Failure -> {
                    _uiState.value = UiState.Error(result.message)
                }
                is AppResult.NoConnection -> {
                    _uiState.value = UiState.Offline(cached = lastSuccessfulList)
                }
                is AppResult.Success -> {
                    if (result.data.isEmpty()) {
                        _uiState.value = UiState.Empty
                    } else {
                        lastSuccessfulList = result.data
                        _uiState.value = UiState.Success(result.data)
                    }
                }
            }
        }
    }
}
