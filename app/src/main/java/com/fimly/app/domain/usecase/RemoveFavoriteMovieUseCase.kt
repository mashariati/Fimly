package com.fimly.app.domain.usecase

import com.fimly.app.domain.repository.MovieRepository

class RemoveFavoriteMovieUseCase(
    private val movieRepository: MovieRepository
) {
    suspend operator fun invoke(movieId: Int) {
        movieRepository.removeFavorite(movieId)
    }
}