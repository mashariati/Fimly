package com.fimly.app.domain.usecase

import com.fimly.app.domain.repository.UserDataRepository
import kotlinx.coroutines.flow.Flow

class ReadOnboardingStateUseCase(
    private val userDataRepository: UserDataRepository
) {
    operator fun invoke(): Flow<Boolean> {
        return userDataRepository.isOnboarded
    }
}