package com.roavai.parent.ui.progress.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.roavai.parent.core.util.AppResult
import com.roavai.parent.core.util.UiState
import com.roavai.parent.data.fake.DemoSettings
import com.roavai.parent.domain.model.*
import com.roavai.parent.domain.repository.NotificationsRepository
import com.roavai.parent.domain.repository.ProgressRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// 1. Overall Progress ViewModel
@HiltViewModel
class ProgressOverallViewModel @Inject constructor(
    private val progressRepository: ProgressRepository,
    private val notificationsRepository: NotificationsRepository,
    private val demoSettings: DemoSettings
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<OverallProgress>>(UiState.Loading)
    val uiState: StateFlow<UiState<OverallProgress>> = _uiState.asStateFlow()
    val unreadCount: StateFlow<Int> = notificationsRepository.unreadCountFlow

    var selectedPeriod = Period.THIS_WEEK
        private set

    private var lastSuccessfulData: OverallProgress? = null

    init {
        viewModelScope.launch {
            demoSettings.state.collect { loadData() }
        }
    }

    fun setPeriod(period: Period) {
        if (selectedPeriod != period) {
            selectedPeriod = period
            loadData()
        }
    }

    fun retry() = loadData()

    private fun loadData() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            when (val res = progressRepository.overall(selectedPeriod)) {
                is AppResult.Success -> {
                    if (res.data.percent == 0 && res.data.completedActivities == 0) _uiState.value = UiState.Empty
                    else {
                        lastSuccessfulData = res.data
                        _uiState.value = UiState.Success(res.data)
                    }
                }
                is AppResult.Failure -> _uiState.value = UiState.Error(res.message)
                is AppResult.NoConnection -> _uiState.value = UiState.Offline(cached = lastSuccessfulData)
            }
        }
    }
}

// 2. Learning Time ViewModel
@HiltViewModel
class ProgressLearningTimeViewModel @Inject constructor(
    private val progressRepository: ProgressRepository,
    private val notificationsRepository: NotificationsRepository,
    private val demoSettings: DemoSettings
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<LearningTime>>(UiState.Loading)
    val uiState: StateFlow<UiState<LearningTime>> = _uiState.asStateFlow()
    val unreadCount: StateFlow<Int> = notificationsRepository.unreadCountFlow

    var selectedPeriod = Period.THIS_WEEK
        private set

    private var lastSuccessfulData: LearningTime? = null

    init {
        viewModelScope.launch {
            demoSettings.state.collect { loadData() }
        }
    }

    fun setPeriod(period: Period) {
        if (selectedPeriod != period) {
            selectedPeriod = period
            loadData()
        }
    }

    fun retry() = loadData()

    private fun loadData() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            when (val res = progressRepository.learningTime(selectedPeriod)) {
                is AppResult.Success -> {
                    if (res.data.totalMinutes == 0 && res.data.perDay.isEmpty()) _uiState.value = UiState.Empty
                    else {
                        lastSuccessfulData = res.data
                        _uiState.value = UiState.Success(res.data)
                    }
                }
                is AppResult.Failure -> _uiState.value = UiState.Error(res.message)
                is AppResult.NoConnection -> _uiState.value = UiState.Offline(cached = lastSuccessfulData)
            }
        }
    }
}

// 3. Subjects Progress ViewModel (no period selector per spec)
@HiltViewModel
class ProgressSubjectsViewModel @Inject constructor(
    private val progressRepository: ProgressRepository,
    private val notificationsRepository: NotificationsRepository,
    private val demoSettings: DemoSettings
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<List<SubjectProgress>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<SubjectProgress>>> = _uiState.asStateFlow()
    val unreadCount: StateFlow<Int> = notificationsRepository.unreadCountFlow

    private var lastSuccessfulData: List<SubjectProgress>? = null

    init {
        viewModelScope.launch {
            demoSettings.state.collect { loadData() }
        }
    }

    fun retry() = loadData()

    private fun loadData() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            when (val res = progressRepository.subjects(Period.THIS_WEEK)) {
                is AppResult.Success -> {
                    if (res.data.isEmpty()) _uiState.value = UiState.Empty
                    else {
                        lastSuccessfulData = res.data
                        _uiState.value = UiState.Success(res.data)
                    }
                }
                is AppResult.Failure -> _uiState.value = UiState.Error(res.message)
                is AppResult.NoConnection -> _uiState.value = UiState.Offline(cached = lastSuccessfulData)
            }
        }
    }
}

// 3b. Subject Detail Topics ViewModel
@HiltViewModel
class ProgressSubjectDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val progressRepository: ProgressRepository,
    private val notificationsRepository: NotificationsRepository,
    private val demoSettings: DemoSettings
) : ViewModel() {
    val subject: String = checkNotNull(savedStateHandle["subject"])
    private val _uiState = MutableStateFlow<UiState<List<TopicMastery>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<TopicMastery>>> = _uiState.asStateFlow()
    val unreadCount: StateFlow<Int> = notificationsRepository.unreadCountFlow

    private var lastSuccessfulData: List<TopicMastery>? = null

    init {
        viewModelScope.launch {
            demoSettings.state.collect { loadData() }
        }
    }

    fun retry() = loadData()

    private fun loadData() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            when (val res = progressRepository.subjectTopics(subject)) {
                is AppResult.Success -> {
                    if (res.data.isEmpty()) _uiState.value = UiState.Empty
                    else {
                        lastSuccessfulData = res.data
                        _uiState.value = UiState.Success(res.data)
                    }
                }
                is AppResult.Failure -> _uiState.value = UiState.Error(res.message)
                is AppResult.NoConnection -> _uiState.value = UiState.Offline(cached = lastSuccessfulData)
            }
        }
    }
}

// 4. Topics Mastery ViewModel (subject filter)
@HiltViewModel
class ProgressTopicsViewModel @Inject constructor(
    private val progressRepository: ProgressRepository,
    private val notificationsRepository: NotificationsRepository,
    private val demoSettings: DemoSettings
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<List<TopicMastery>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<TopicMastery>>> = _uiState.asStateFlow()
    val unreadCount: StateFlow<Int> = notificationsRepository.unreadCountFlow

    var selectedSubject: String? = null
        private set

    private var lastSuccessfulData: List<TopicMastery>? = null

    init {
        viewModelScope.launch {
            demoSettings.state.collect { loadData() }
        }
    }

    fun setSubject(subject: String?) {
        if (selectedSubject != subject) {
            selectedSubject = subject
            loadData()
        }
    }

    fun retry() = loadData()

    private fun loadData() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            when (val res = progressRepository.topics(selectedSubject)) {
                is AppResult.Success -> {
                    if (res.data.isEmpty()) _uiState.value = UiState.Empty
                    else {
                        lastSuccessfulData = res.data
                        _uiState.value = UiState.Success(res.data)
                    }
                }
                is AppResult.Failure -> _uiState.value = UiState.Error(res.message)
                is AppResult.NoConnection -> _uiState.value = UiState.Offline(cached = lastSuccessfulData)
            }
        }
    }
}

// 5. Test Performance ViewModel
@HiltViewModel
class ProgressTestsViewModel @Inject constructor(
    private val progressRepository: ProgressRepository,
    private val notificationsRepository: NotificationsRepository,
    private val demoSettings: DemoSettings
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<List<TestResult>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<TestResult>>> = _uiState.asStateFlow()
    val unreadCount: StateFlow<Int> = notificationsRepository.unreadCountFlow

    var selectedPeriod = Period.THIS_WEEK
        private set

    private var lastSuccessfulData: List<TestResult>? = null

    init {
        viewModelScope.launch {
            demoSettings.state.collect { loadData() }
        }
    }

    fun setPeriod(period: Period) {
        if (selectedPeriod != period) {
            selectedPeriod = period
            loadData()
        }
    }

    fun retry() = loadData()

    private fun loadData() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            when (val res = progressRepository.tests(selectedPeriod)) {
                is AppResult.Success -> {
                    if (res.data.isEmpty()) _uiState.value = UiState.Empty
                    else {
                        lastSuccessfulData = res.data
                        _uiState.value = UiState.Success(res.data)
                    }
                }
                is AppResult.Failure -> _uiState.value = UiState.Error(res.message)
                is AppResult.NoConnection -> _uiState.value = UiState.Offline(cached = lastSuccessfulData)
            }
        }
    }
}

// 6. Comparisons ViewModel
@HiltViewModel
class ProgressComparisonsViewModel @Inject constructor(
    private val progressRepository: ProgressRepository,
    private val notificationsRepository: NotificationsRepository,
    private val demoSettings: DemoSettings
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<Comparisons>>(UiState.Loading)
    val uiState: StateFlow<UiState<Comparisons>> = _uiState.asStateFlow()
    val unreadCount: StateFlow<Int> = notificationsRepository.unreadCountFlow

    var selectedPeriod = Period.THIS_WEEK
        private set

    private var lastSuccessfulData: Comparisons? = null

    init {
        viewModelScope.launch {
            demoSettings.state.collect { loadData() }
        }
    }

    fun setPeriod(period: Period) {
        if (selectedPeriod != period) {
            selectedPeriod = period
            loadData()
        }
    }

    fun retry() = loadData()

    private fun loadData() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            when (val res = progressRepository.comparisons(selectedPeriod)) {
                is AppResult.Success -> {
                    val comp = res.data
                    if (comp.learningTime.current.isEmpty() && comp.overallProgress.current.isEmpty()) {
                        _uiState.value = UiState.Empty
                    } else {
                        lastSuccessfulData = comp
                        _uiState.value = UiState.Success(comp)
                    }
                }
                is AppResult.Failure -> _uiState.value = UiState.Error(res.message)
                is AppResult.NoConnection -> _uiState.value = UiState.Offline(cached = lastSuccessfulData)
            }
        }
    }
}

// 7. Credits ViewModel
@HiltViewModel
class ProgressCreditsViewModel @Inject constructor(
    private val progressRepository: ProgressRepository,
    private val notificationsRepository: NotificationsRepository,
    private val demoSettings: DemoSettings
) : ViewModel() {
    data class CreditsUiData(
        val totalCredits: Int,
        val history: List<CreditEntry>
    )

    private val _uiState = MutableStateFlow<UiState<CreditsUiData>>(UiState.Loading)
    val uiState: StateFlow<UiState<CreditsUiData>> = _uiState.asStateFlow()
    val unreadCount: StateFlow<Int> = notificationsRepository.unreadCountFlow

    private var lastSuccessfulData: CreditsUiData? = null

    init {
        viewModelScope.launch {
            demoSettings.state.collect { loadData() }
        }
    }

    fun retry() = loadData()

    private fun loadData() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val summaryRes = progressRepository.credits()
            val historyRes = progressRepository.creditHistory()

            if (summaryRes is AppResult.NoConnection || historyRes is AppResult.NoConnection) {
                _uiState.value = UiState.Offline(cached = lastSuccessfulData)
                return@launch
            }
            if (summaryRes is AppResult.Failure) {
                _uiState.value = UiState.Error(summaryRes.message)
                return@launch
            }

            val total = (summaryRes as? AppResult.Success)?.data?.total ?: 0
            val history = (historyRes as? AppResult.Success)?.data ?: emptyList()

            if (total == 0 && history.isEmpty()) {
                _uiState.value = UiState.Empty
            } else {
                val data = CreditsUiData(total, history)
                lastSuccessfulData = data
                _uiState.value = UiState.Success(data)
            }
        }
    }
}
