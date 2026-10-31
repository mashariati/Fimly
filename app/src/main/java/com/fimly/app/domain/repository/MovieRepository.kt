package com.fimly.app.domain.repository

import com.fimly.app.domain.model.Movie
import com.fimly.app.domain.model.MovieSortBy
import com.fimly.app.domain.util.AppResult
import com.fimly.app.domain.util.DataError

interface MovieRepository {
    suspend fun discoverMovies(
        page: Int = 1,
        sortBy: MovieSortBy = MovieSortBy.POPULARITY_DESC,
        withGenres: String? = null,
        primaryReleaseYear: Int? = null,
        voteAverageGte: Double? = null
    ): AppResult<List<Movie>, DataError>
}