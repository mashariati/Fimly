package com.fimly.app.presentation.feature.onboarding

import androidx.lifecycle.viewModelScope
import com.fimly.app.domain.usecase.SaveOnboardingStateUseCase
import com.fimly.app.presentation.core.BaseViewModel
import kotlinx.coroutines.launch

class OnboardingViewModel(
    private val saveOnboardingStateUseCase: SaveOnboardingStateUseCase
) : BaseViewModel<OnboardingState, OnboardingIntent, OnboardingEffect>(OnboardingState) {

    override fun handleIntent(intent: OnboardingIntent) {
        when (intent) {
            is OnboardingIntent.CompleteOnboardingClick -> completeOnboarding()
        }
    }

    private fun completeOnboarding() {
        viewModelScope.launch {
            saveOnboardingStateUseCase(isCompleted = true)
            sendEffect{OnboardingEffect.NavigateToMainApp}
        }
    }
}