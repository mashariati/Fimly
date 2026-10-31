package com.fimly.app.domain.util

/**
 * Categorized domain-level errors to decouple the UI and ViewModel from data-layer exceptions.
 */
enum class DataError {
    NO_INTERNET,
    REQUEST_TIMEOUT,
    UNAUTHORIZED,
    SERVER_ERROR,
    SERIALIZATION_ERROR,
    UNKNOWN
}

/**
 * A generic sealed interface representing the outcome of an operation.
 */
sealed interface AppResult<out D, out E> {
    data class Success<out D>(val data: D) : AppResult<D, Nothing>
    data class Error<out E>(val error: E, val message: String? = null) : AppResult<Nothing, E>
}

/**
 * Helper extension to transform the success data type (e.g., mapping DTO to Domain model)
 * while preserving the error state.
 */
inline fun <T, E, R> AppResult<T, E>.map(transform: (T) -> R): AppResult<R, E> {
    return when (this) {
        is AppResult.Success -> AppResult.Success(transform(data))
        is AppResult.Error -> AppResult.Error(error, message)
    }
}