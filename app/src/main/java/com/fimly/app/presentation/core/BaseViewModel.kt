package com.fimly.app.presentation.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

/**
 * Base ViewModel for MVI architecture.
 *
 * @param S The type of the UI State.
 * @param I The type of the UI Intent.
 * @param E The type of the UI Effect (One-time events).
 */
abstract class BaseViewModel<S : UiState, I : UiIntent, E : UiEffect>(
    initialState: S
) : ViewModel() {

    // 1. State Management
    private val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<S> = _uiState.asStateFlow()

    // 2. Effect Management (Using Channel guarantees no events are lost in Compose)
    private val _effect = Channel<E>()
    val effect = _effect.receiveAsFlow()

    /**
     * Gets the current UI state securely.
     */
    protected val currentState: S
        get() = _uiState.value

    /**
     * Abstract function to be implemented by child ViewModels.
     * All UI actions (Intents) pass through this single entry point.
     */
    abstract fun handleIntent(intent: I)

    /**
     * Updates the UI state using a reducer approach.
     */
    protected fun updateState(reducer: S.() -> S) {
        _uiState.value = currentState.reducer()
    }

    /**
     * Sends a one-time side effect to the UI securely.
     */
    protected fun sendEffect(effectBuilder: () -> E) {
        viewModelScope.launch {
            _effect.send(effectBuilder())
        }
    }
}