package com.roavai.parent.ui.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.roavai.parent.core.util.AppResult
import com.roavai.parent.core.util.UiState
import com.roavai.parent.data.fake.DemoSettings
import com.roavai.parent.domain.model.AppNotification
import com.roavai.parent.domain.repository.NotificationsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val notificationsRepository: NotificationsRepository,
    private val demoSettings: DemoSettings
) : ViewModel() {

    val unreadCount: StateFlow<Int> = notificationsRepository.unreadCountFlow

    private val _uiState = MutableStateFlow<UiState<List<AppNotification>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<AppNotification>>> = _uiState.asStateFlow()

    private var lastSuccessfulList: List<AppNotification>? = null

    init {
        viewModelScope.launch {
            demoSettings.state.collectLatest {
                loadNotifications()
            }
        }
    }

    fun retry() {
        viewModelScope.launch {
            loadNotifications()
        }
    }

    fun loadNotifications() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            when (val result = notificationsRepository.notifications()) {
                is AppResult.Failure -> {
                    _uiState.value = UiState.Error(result.message)
                }
                is AppResult.NoConnection -> {
                    _uiState.value = UiState.Offline(cached = lastSuccessfulList)
                }
                is AppResult.Success -> {
                    val list = result.data
                    if (list.isEmpty()) {
                        _uiState.value = UiState.Empty
                    } else {
                        lastSuccessfulList = list
                        _uiState.value = UiState.Success(list)
                    }
                }
            }
        }
    }

    fun markRead(id: String) {
        viewModelScope.launch {
            notificationsRepository.markRead(id)
            loadNotifications()
        }
    }

    fun markAllRead() {
        viewModelScope.launch {
            notificationsRepository.markAllRead()
            loadNotifications()
        }
    }
}
