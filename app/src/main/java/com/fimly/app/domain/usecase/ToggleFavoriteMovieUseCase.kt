package com.fimly.app.domain.usecase

import com.fimly.app.domain.model.Movie
import com.fimly.app.domain.repository.MovieRepository

class ToggleFavoriteMovieUseCase(
    private val movieRepository: MovieRepository
) {
    suspend operator fun invoke(movie: Movie) {
        movieRepository.toggleFavorite(movie)
    }
}