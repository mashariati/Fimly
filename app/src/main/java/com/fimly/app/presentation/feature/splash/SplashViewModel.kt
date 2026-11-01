package com.fimly.app.presentation.feature.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fimly.app.domain.usecase.ReadOnboardingStateUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class SplashViewModel(
    readOnboardingStateUseCase: ReadOnboardingStateUseCase
) : ViewModel() {

    val isOnboarded: StateFlow<Boolean?> = readOnboardingStateUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )
}