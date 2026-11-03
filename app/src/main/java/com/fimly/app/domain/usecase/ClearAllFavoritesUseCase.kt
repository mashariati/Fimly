package com.fimly.app.domain.usecase

import com.fimly.app.domain.repository.MovieRepository

class ClearAllFavoritesUseCase(
    private val movieRepository: MovieRepository
) {
    suspend operator fun invoke() {
        movieRepository.clearAllFavorites()
    }
}