package com.fimly.app.presentation.core

/**
 * Base marker interface for UI State.
 * Represents the complete state of a screen at any given moment.
 */
interface UiState

/**
 * Base marker interface for UI Intents.
 * Represents user actions (e.g., clicks) or system events (e.g., lifecycle).
 */
interface UiIntent

/**
 * Base marker interface for UI Effects (Side Effects).
 * Represents one-time events that should not be persisted in state
 * (e.g., Navigation, showing a Toast or Snackbar).
 */
interface UiEffect

/**
 * Reusable generic state wrapper for independent UI content.
 * that follow the standard Loading / Empty / Error / Success lifecycle.
 */
sealed interface ContentState<out T> {
    data object Loading : ContentState<Nothing>
    data object Empty : ContentState<Nothing>
    data class Error(val message: String) : ContentState<Nothing>
    data class Success<T>(val data: T) : ContentState<T>
}