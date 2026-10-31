package com.fimly.app.data.network

import com.fimly.app.data.network.dto.MovieResponseDto
import com.fimly.app.domain.util.AppResult
import com.fimly.app.domain.util.DataError


interface NetworkDataSource {
    suspend fun discoverMovies(
        page: Int,
        sortBy: String,
        withGenres: String?,
        primaryReleaseYear: Int?,
        voteAverageGte: Double?
    ): AppResult<MovieResponseDto, DataError>
}