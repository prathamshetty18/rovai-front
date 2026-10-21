package com.roavai.parent.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.roavai.parent.core.util.AppResult
import com.roavai.parent.core.util.UiState
import com.roavai.parent.data.fake.DemoSettings
import com.roavai.parent.domain.model.HomeData
import com.roavai.parent.domain.repository.AuthRepository
import com.roavai.parent.domain.repository.HomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiData(
    val parentName: String,
    val homeData: HomeData
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeRepository: HomeRepository,
    private val authRepository: AuthRepository,
    private val demoSettings: DemoSettings
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<HomeUiData>>(UiState.Loading)
    val uiState: StateFlow<UiState<HomeUiData>> = _uiState.asStateFlow()

    private var lastSuccessfulData: HomeUiData? = null

    init {
        viewModelScope.launch {
            combine(demoSettings.state, demoSettings.robotDisconnected) { state, disconnected ->
                Pair(state, disconnected)
            }.collectLatest {
                loadHome()
            }
        }
    }

    fun retry() {
        viewModelScope.launch {
            loadHome()
        }
    }

    private suspend fun loadHome() {
        _uiState.value = UiState.Loading

        val parentResult = authRepository.currentParent()
        val parentName = when (parentResult) {
            is AppResult.Success -> parentResult.data?.fullName ?: "Meera"
            else -> "Meera"
        }

        when (val homeResult = homeRepository.home()) {
            is AppResult.Failure -> {
                _uiState.value = UiState.Error(homeResult.message)
            }
            is AppResult.NoConnection -> {
                _uiState.value = UiState.Offline(cached = lastSuccessfulData)
            }
            is AppResult.Success -> {
                val data = homeResult.data
                val isEmpty = data.today.learningMinutes == 0 &&
                        data.recentActivity.isEmpty() &&
                        data.subjectProgress.isEmpty() &&
                        data.today.topicsCovered == 0

                if (isEmpty) {
                    _uiState.value = UiState.Empty
                } else {
                    val uiData = HomeUiData(parentName, data)
                    lastSuccessfulData = uiData
                    _uiState.value = UiState.Success(uiData)
                }
            }
        }
    }
}
