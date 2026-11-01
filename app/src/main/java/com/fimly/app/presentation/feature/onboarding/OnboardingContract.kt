package com.fimly.app.presentation.feature.onboarding

import com.fimly.app.presentation.core.UiEffect
import com.fimly.app.presentation.core.UiIntent
import com.fimly.app.presentation.core.UiState

data object OnboardingState : UiState

sealed interface OnboardingIntent : UiIntent {
    data object CompleteOnboardingClick : OnboardingIntent
}

sealed interface OnboardingEffect : UiEffect {
    data object NavigateToMainApp : OnboardingEffect
}