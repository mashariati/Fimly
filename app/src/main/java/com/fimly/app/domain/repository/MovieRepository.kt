package com.fimly.app.domain.repository

import com.fimly.app.domain.model.Genre
import com.fimly.app.domain.model.Movie
import com.fimly.app.domain.model.MovieDetail
import com.fimly.app.domain.model.MovieSortBy
import com.fimly.app.domain.util.AppResult
import com.fimly.app.domain.util.DataError
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    suspend fun discoverMovies(
        page: Int = 1,
        sortBy: MovieSortBy = MovieSortBy.POPULARITY_DESC,
        withGenres: String? = null,
        primaryReleaseYear: Int? = null,
        voteAverageGte: Double? = null
    ): AppResult<List<Movie>, DataError>

    suspend fun searchMovies(
        query: String,
        page: Int = 1,
        primaryReleaseYear: String? = null
    ): AppResult<List<Movie>, DataError>

    suspend fun getMovieDetails(
        movieId: Int
    ): AppResult<MovieDetail, DataError>

    suspend fun getMovieGenres(
    ): AppResult<List<Genre>, DataError>

    fun getFavoriteMovies(): Flow<List<Movie>>

    fun isMovieFavorite(movieId: Int): Flow<Boolean>

    suspend fun toggleFavorite(movie: Movie)

    suspend fun removeFavorite(movieId: Int)

    suspend fun clearAllFavorites()
}