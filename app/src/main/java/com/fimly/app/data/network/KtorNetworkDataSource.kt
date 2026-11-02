package com.fimly.app.data.network

import com.fimly.app.data.network.dto.GenreListDto
import com.fimly.app.data.network.dto.MovieDetailDto
import com.fimly.app.data.network.dto.MovieListDto
import com.fimly.app.domain.util.AppResult
import com.fimly.app.domain.util.DataError
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class KtorNetworkDataSource(private val httpClient: HttpClient) : NetworkDataSource {
    override suspend fun discoverMovies(
        page: Int,
        sortBy: String,
        withGenres: String?,
        primaryReleaseYear: Int?,
        voteAverageGte: Double?
    ): AppResult<MovieListDto, DataError> {
        return safeApiCall {
            httpClient.get("3/discover/movie") {
                parameter("page", page)
                parameter("sort_by", sortBy)
                parameter("include_adult", false)
                parameter("include_video", false)
                parameter("language", "en-US")

                withGenres?.let { parameter("with_genres", it) }
                primaryReleaseYear?.let { parameter("primary_release_year", it) }
                voteAverageGte?.let { parameter("vote_average.gte", it) }
            }
        }
    }
    override suspend fun searchMovies(
        query: String,
        page: Int,
        primaryReleaseYear: String?
    ): AppResult<MovieListDto, DataError> {
        return safeApiCall {
            httpClient.get("3/search/movie") {
                parameter("query", query)
                parameter("page", page)
                parameter("include_adult", false)
                parameter("language", "en-US")
                primaryReleaseYear?.let { parameter("primary_release_year", it) }
            }
        }
    }

    override suspend fun getMovieDetails(
        movieId: Int
    ): AppResult<MovieDetailDto, DataError> {
        return safeApiCall {
            httpClient.get("3/movie/$movieId") {
                parameter("language", "en-US")
            }
        }
    }

    override suspend fun getMovieGenres(): AppResult<GenreListDto, DataError> {
        return safeApiCall {
            httpClient.get("3/genre/movie/list") {
                parameter("language", "en-US")
            }
        }
    }
}