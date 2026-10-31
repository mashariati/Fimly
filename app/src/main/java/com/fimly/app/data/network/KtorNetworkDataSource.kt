package com.fimly.app.data.network

import com.fimly.app.data.network.dto.MovieResponseDto
import com.fimly.app.domain.util.AppResult
import com.fimly.app.domain.util.DataError
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class KtorNetworkDataSource(private val httpClient: HttpClient) : NetworkDataSource {
    override suspend fun discoverMovies(
        page: Int,
        sortBy: String,
        withGenres: String?,
        primaryReleaseYear: Int?,
        voteAverageGte: Double?
    ): AppResult<MovieResponseDto, DataError> {
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
}