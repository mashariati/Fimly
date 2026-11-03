package com.fimly.app.presentation.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
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

    private val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<S> = _uiState.asStateFlow()

    private val _effect = Channel<E>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    protected val currentState: S
        get() = _uiState.value


    fun onIntent(intent: I) {
        handleIntent(intent)
    }

    protected abstract fun handleIntent(intent: I)

    protected fun updateState(reducer: S.() -> S) {
        _uiState.update { it.reducer() }
    }

    protected fun sendEffect(effectBuilder: () -> E) {
        viewModelScope.launch {
            _effect.send(effectBuilder())
        }
    }
}