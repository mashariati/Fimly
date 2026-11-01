package com.fimly.app.domain.usecase

import com.fimly.app.domain.repository.UserDataRepository

class SaveOnboardingStateUseCase(
    private val userDataRepository: UserDataRepository
) {
    suspend operator fun invoke(isCompleted: Boolean) {
        userDataRepository.saveOnboardingState(isCompleted)
    }
}