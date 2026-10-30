package com.roavai.parent.ui.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.roavai.parent.core.util.AppResult
import com.roavai.parent.core.util.UiState
import com.roavai.parent.data.fake.DemoSettings
import com.roavai.parent.domain.model.*
import com.roavai.parent.domain.repository.ChildRepository
import com.roavai.parent.domain.repository.NotificationsRepository
import com.roavai.parent.domain.repository.ProgressRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProgressHubUiData(
    val child: Child,
    val selectedPeriod: Period,
    val overall: OverallProgress,
    val learningTime: LearningTime,
    val subjects: List<SubjectProgress>,
    val topics: List<TopicMastery>,
    val tests: List<TestResult>,
    val comparisons: Comparisons,
    val credits: CreditSummary
)

@HiltViewModel
class ProgressViewModel @Inject constructor(
    private val progressRepository: ProgressRepository,
    private val childRepository: ChildRepository,
    private val notificationsRepository: NotificationsRepository,
    private val demoSettings: DemoSettings
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<ProgressHubUiData>>(UiState.Loading)
    val uiState: StateFlow<UiState<ProgressHubUiData>> = _uiState.asStateFlow()

    val unreadNotificationsCount: StateFlow<Int> = notificationsRepository.unreadCountFlow

    var selectedPeriod = Period.THIS_WEEK
        private set

    init {
        viewModelScope.launch {
            demoSettings.state.collect {
                loadProgressData()
            }
        }
    }

    fun setPeriod(period: Period) {
        if (selectedPeriod != period) {
            selectedPeriod = period
            loadProgressData()
        }
    }

    fun retry() {
        loadProgressData()
    }

    private fun loadProgressData() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading

            val childResult = childRepository.currentChild()
            val child = when (childResult) {
                is AppResult.Success -> childResult.data
                else -> null
            }

            val overallResult = progressRepository.overall(selectedPeriod)
            val learningTimeResult = progressRepository.learningTime(selectedPeriod)
            val subjectsResult = progressRepository.subjects(selectedPeriod)
            val topicsResult = progressRepository.topics()
            val testsResult = progressRepository.tests(selectedPeriod)
            val comparisonsResult = progressRepository.comparisons(selectedPeriod)
            val creditsResult = progressRepository.credits()

            if (overallResult is AppResult.NoConnection || learningTimeResult is AppResult.NoConnection) {
                _uiState.value = UiState.Offline()
                return@launch
            }

            if (overallResult is AppResult.Failure) {
                _uiState.value = UiState.Error(overallResult.message)
                return@launch
            }

            if (child == null || overallResult !is AppResult.Success || learningTimeResult !is AppResult.Success) {
                _uiState.value = UiState.Empty
                return@launch
            }

            val overall = overallResult.data
            val learningTime = learningTimeResult.data
            val subjects = (subjectsResult as? AppResult.Success)?.data ?: emptyList()
            val topics = (topicsResult as? AppResult.Success)?.data ?: emptyList()
            val tests = (testsResult as? AppResult.Success)?.data ?: emptyList()
            val comparisons = (comparisonsResult as? AppResult.Success)?.data ?: Comparisons(
                ComparisonRow("", "", "", ""),
                ComparisonRow("", "", "", ""),
                ComparisonRow("", "", "", ""),
                emptyList()
            )
            val credits = (creditsResult as? AppResult.Success)?.data ?: CreditSummary(0, emptyList(), "")

            if (overall.percent == 0 && learningTime.totalMinutes == 0 && subjects.isEmpty()) {
                _uiState.value = UiState.Empty
            } else {
                _uiState.value = UiState.Success(
                    ProgressHubUiData(
                        child = child,
                        selectedPeriod = selectedPeriod,
                        overall = overall,
                        learningTime = learningTime,
                        subjects = subjects,
                        topics = topics,
                        tests = tests,
                        comparisons = comparisons,
                        credits = credits
                    )
                )
            }
        }
    }
}
