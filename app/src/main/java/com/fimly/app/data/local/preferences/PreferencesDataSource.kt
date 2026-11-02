package com.fimly.app.data.local.preferences

import kotlinx.coroutines.flow.Flow

interface PreferencesDataSource {
    val isOnboarded: Flow<Boolean>
    suspend fun saveOnboardingState(isCompleted: Boolean)
}