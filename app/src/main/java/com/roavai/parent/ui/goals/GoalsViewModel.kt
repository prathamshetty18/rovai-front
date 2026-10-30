package com.roavai.parent.ui.goals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.roavai.parent.core.util.AppResult
import com.roavai.parent.core.util.UiState
import com.roavai.parent.data.fake.DemoSettings
import com.roavai.parent.domain.model.Goal
import com.roavai.parent.domain.repository.ChildRepository
import com.roavai.parent.domain.repository.GoalsRepository
import com.roavai.parent.domain.repository.NotificationsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

data class GoalsUiData(
    val dailyGoalMinutes: Int,
    val goals: List<Goal>
)

@HiltViewModel
class GoalsViewModel @Inject constructor(
    private val goalsRepository: GoalsRepository,
    private val childRepository: ChildRepository,
    private val notificationsRepository: NotificationsRepository,
    private val demoSettings: DemoSettings
) : ViewModel() {

    val unreadCount: StateFlow<Int> = notificationsRepository.unreadCountFlow

    private val _uiState = MutableStateFlow<UiState<GoalsUiData>>(UiState.Loading)
    val uiState: StateFlow<UiState<GoalsUiData>> = _uiState.asStateFlow()

    private var lastSuccessfulData: GoalsUiData? = null

    init {
        viewModelScope.launch {
            demoSettings.state.collectLatest {
                loadGoals()
            }
        }
    }

    fun retry() {
        viewModelScope.launch {
            loadGoals()
        }
    }

    fun loadGoals() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            when (val goalsResult = goalsRepository.goals()) {
                is AppResult.Failure -> {
                    _uiState.value = UiState.Error(goalsResult.message)
                }
                is AppResult.NoConnection -> {
                    _uiState.value = UiState.Offline(cached = lastSuccessfulData)
                }
                is AppResult.Success -> {
                    val childResult = childRepository.currentChild()
                    val dailyGoal = when (childResult) {
                        is AppResult.Success -> childResult.data.dailyGoalMinutes
                        else -> 60
                    }
                    val uiData = GoalsUiData(dailyGoalMinutes = dailyGoal, goals = goalsResult.data)
                    lastSuccessfulData = uiData
                    _uiState.value = UiState.Success(uiData)
                }
            }
        }
    }

    fun updateDailyGoal(minutes: Int) {
        viewModelScope.launch {
            goalsRepository.setDailyGoal(minutes)
            loadGoals()
        }
    }

    fun addGoal(title: String, subject: String, topic: String?, date: LocalDate, time: LocalTime?) {
        viewModelScope.launch {
            goalsRepository.addGoal(title, subject, topic, date, time)
            loadGoals()
        }
    }

    fun updateGoal(goal: Goal) {
        viewModelScope.launch {
            goalsRepository.updateGoal(goal)
            loadGoals()
        }
    }

    fun deleteGoal(id: String) {
        viewModelScope.launch {
            goalsRepository.deleteGoal(id)
            loadGoals()
        }
    }
}
