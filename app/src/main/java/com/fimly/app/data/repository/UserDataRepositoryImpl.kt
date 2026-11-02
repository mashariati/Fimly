package com.fimly.app.data.repository

import com.fimly.app.data.local.preferences.PreferencesDataSource
import com.fimly.app.domain.repository.UserDataRepository
import kotlinx.coroutines.flow.Flow

class UserDataRepositoryImpl(
    private val preferencesDataSource: PreferencesDataSource
) : UserDataRepository {

    override val isOnboarded: Flow<Boolean>
        get() = preferencesDataSource.isOnboarded

    override suspend fun saveOnboardingState(isCompleted: Boolean) {
        preferencesDataSource.saveOnboardingState(isCompleted)
    }
}