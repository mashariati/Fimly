package com.fimly.app.domain.usecase

import com.fimly.app.domain.model.Movie
import com.fimly.app.domain.model.MovieSortBy
import com.fimly.app.domain.repository.MovieRepository
import com.fimly.app.domain.util.AppResult
import com.fimly.app.domain.util.DataError

class DiscoverMoviesUseCase(
    private val movieRepository: MovieRepository
) {
    suspend operator fun invoke(
        page: Int = 1,
        sortBy: MovieSortBy = MovieSortBy.POPULARITY_DESC,
        withGenres: String? = null,
        primaryReleaseYear: Int? = null,
        voteAverageGte: Double? = null
    ): AppResult<List<Movie>, DataError> {
        return movieRepository.discoverMovies(
            page = page,
            sortBy = sortBy,
            withGenres = withGenres,
            primaryReleaseYear = primaryReleaseYear,
            voteAverageGte = voteAverageGte
        )
    }
}