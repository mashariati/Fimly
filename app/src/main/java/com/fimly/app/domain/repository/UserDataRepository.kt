package com.fimly.app.domain.repository

import com.fimly.app.domain.model.Movie
import com.fimly.app.domain.model.MovieSortBy
import com.fimly.app.domain.util.AppResult
import com.fimly.app.domain.util.DataError
import kotlinx.coroutines.flow.Flow

interface UserDataRepository {
    val isOnboarded: Flow<Boolean>
    suspend fun saveOnboardingState(isCompleted: Boolean)
}