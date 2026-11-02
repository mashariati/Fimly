package com.fimly.app.domain.usecase

import com.fimly.app.domain.model.Movie
import com.fimly.app.domain.repository.MovieRepository
import com.fimly.app.domain.util.AppResult
import com.fimly.app.domain.util.DataError

class SearchMoviesUseCase(
    private val movieRepository: MovieRepository
) {
    suspend operator fun invoke(
        query: String,
        page: Int = 1,
        primaryReleaseYear: String? = null
    ): AppResult<List<Movie>, DataError> {
        if (query.isBlank()) {
            return AppResult.Success(emptyList())
        }
        return movieRepository.searchMovies(
            query = query,
            page = page,
            primaryReleaseYear = primaryReleaseYear
        )
    }
}