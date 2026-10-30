package com.roavai.parent.ui.child

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.roavai.parent.core.util.AppResult
import com.roavai.parent.domain.model.Child
import com.roavai.parent.domain.repository.ChildRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface AddChildUiState {
    data object Idle : AddChildUiState
    data object Loading : AddChildUiState
    data class Success(val child: Child) : AddChildUiState
    data class Error(val message: String) : AddChildUiState
}

@HiltViewModel
class AddChildViewModel @Inject constructor(
    private val childRepository: ChildRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AddChildUiState>(AddChildUiState.Idle)
    val uiState: StateFlow<AddChildUiState> = _uiState.asStateFlow()

    fun addChild(name: String, grade: String, curriculum: String, consent: Boolean) {
        viewModelScope.launch {
            _uiState.value = AddChildUiState.Loading
            when (val result = childRepository.addChild(name.trim(), grade, curriculum, consent)) {
                is AppResult.Success -> {
                    _uiState.value = AddChildUiState.Success(result.data)
                }
                is AppResult.Failure -> {
                    _uiState.value = AddChildUiState.Error(result.message)
                }
                is AppResult.NoConnection -> {
                    _uiState.value = AddChildUiState.Error("No network connection. Please check your internet.")
                }
            }
        }
    }
}
