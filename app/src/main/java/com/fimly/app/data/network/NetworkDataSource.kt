package com.fimly.app.data.network

import com.fimly.app.data.network.dto.GenreListDto
import com.fimly.app.data.network.dto.MovieDetailDto
import com.fimly.app.data.network.dto.MovieListDto
import com.fimly.app.domain.util.AppResult
import com.fimly.app.domain.util.DataError


interface NetworkDataSource {
    suspend fun discoverMovies(
        page: Int,
        sortBy: String,
        withGenres: String?,
        primaryReleaseYear: Int?,
        voteAverageGte: Double?
    ): AppResult<MovieListDto, DataError>

    suspend fun searchMovies(
        query: String,
        page: Int,
        primaryReleaseYear: String?
    ): AppResult<MovieListDto, DataError>

    suspend fun getMovieDetails(
        movieId: Int
    ): AppResult<MovieDetailDto, DataError>

    suspend fun getMovieGenres(
    ): AppResult<GenreListDto, DataError>
}