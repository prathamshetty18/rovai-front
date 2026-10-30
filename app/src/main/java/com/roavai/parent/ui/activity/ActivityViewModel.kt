package com.roavai.parent.ui.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.roavai.parent.core.util.AppResult
import com.roavai.parent.core.util.UiState
import com.roavai.parent.data.fake.DemoSettings
import com.roavai.parent.domain.model.Activity
import com.roavai.parent.domain.model.ActivityPeriod
import com.roavai.parent.domain.model.Child
import com.roavai.parent.domain.repository.ActivityRepository
import com.roavai.parent.domain.repository.ChildRepository
import com.roavai.parent.domain.repository.NotificationsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

data class ActivityUiData(
    val child: Child,
    val selectedPeriod: ActivityPeriod,
    val selectedSubject: String?,
    val groupedActivities: Map<LocalDate, List<Activity>>
)

@HiltViewModel
class ActivityViewModel @Inject constructor(
    private val activityRepository: ActivityRepository,
    private val childRepository: ChildRepository,
    private val notificationsRepository: NotificationsRepository,
    private val demoSettings: DemoSettings
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<ActivityUiData>>(UiState.Loading)
    val uiState: StateFlow<UiState<ActivityUiData>> = _uiState.asStateFlow()

    val unreadNotificationsCount: StateFlow<Int> = notificationsRepository.unreadCountFlow

    private var currentPeriod = ActivityPeriod.THIS_WEEK
    private var currentSubject: String? = null

    private var lastSuccessfulData: ActivityUiData? = null

    init {
        viewModelScope.launch {
            demoSettings.state.collect {
                loadActivities()
            }
        }
    }

    fun setPeriod(period: ActivityPeriod) {
        if (currentPeriod != period) {
            currentPeriod = period
            loadActivities()
        }
    }

    fun setSubject(subject: String?) {
        if (currentSubject != subject) {
            currentSubject = subject
            loadActivities()
        }
    }

    fun retry() {
        loadActivities()
    }

    private fun loadActivities() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading

            val childResult = childRepository.currentChild()
            val child = when (childResult) {
                is AppResult.Success -> childResult.data
                else -> null
            }

            when (val result = activityRepository.activities(currentPeriod, currentSubject)) {
                is AppResult.Success -> {
                    val list = result.data
                    if (list.isEmpty() || child == null) {
                        _uiState.value = UiState.Empty
                    } else {
                        // Group by date (descending)
                        val grouped = list.groupBy {
                            it.startedAt.atZone(ZoneId.systemDefault()).toLocalDate()
                        }.toSortedMap(compareByDescending { it })

                        val data = ActivityUiData(
                            child = child,
                            selectedPeriod = currentPeriod,
                            selectedSubject = currentSubject,
                            groupedActivities = grouped
                        )
                        lastSuccessfulData = data
                        _uiState.value = UiState.Success(data)
                    }
                }
                is AppResult.Failure -> {
                    _uiState.value = UiState.Error(result.message)
                }
                is AppResult.NoConnection -> {
                    _uiState.value = UiState.Offline(cached = lastSuccessfulData)
                }
            }
        }
    }
}
