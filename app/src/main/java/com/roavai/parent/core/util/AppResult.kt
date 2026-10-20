package com.roavai.parent.core.util

sealed interface AppResult<out T> {
    data class Success<T>(val data: T) : AppResult<T>
    data class Failure(val message: String) : AppResult<Nothing>
    data object NoConnection : AppResult<Nothing>
}
