package com.fimly.app.domain.usecase

import com.fimly.app.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow

class ObserveFavoriteStatusUseCase(
    private val movieRepository: MovieRepository
) {
    operator fun invoke(movieId: Int): Flow<Boolean> {
        return movieRepository.isMovieFavorite(movieId)
    }
}