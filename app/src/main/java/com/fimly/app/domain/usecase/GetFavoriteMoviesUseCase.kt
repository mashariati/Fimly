package com.fimly.app.domain.usecase

import com.fimly.app.domain.model.Movie
import com.fimly.app.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow

class GetFavoriteMoviesUseCase(
    private val movieRepository: MovieRepository
) {
    operator fun invoke(): Flow<List<Movie>> {
        return movieRepository.getFavoriteMovies()
    }
}