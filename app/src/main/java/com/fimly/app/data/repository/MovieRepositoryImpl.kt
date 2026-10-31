package com.fimly.app.data.repository
import com.fimly.app.data.mapper.toDomainList
import com.fimly.app.data.network.NetworkDataSource
import com.fimly.app.data.network.dto.MovieResponseDto
import com.fimly.app.data.network.safeApiCall
import com.fimly.app.domain.model.Movie
import com.fimly.app.domain.model.MovieSortBy
import com.fimly.app.domain.repository.MovieRepository
import com.fimly.app.domain.util.AppResult
import com.fimly.app.domain.util.DataError
import com.fimly.app.domain.util.map

class MovieRepositoryImpl(private val networkDataSource: NetworkDataSource): MovieRepository {
    override suspend fun discoverMovies(
        page: Int,
        sortBy: MovieSortBy,
        withGenres: String?,
        primaryReleaseYear: Int?,
        voteAverageGte: Double?
    ): AppResult<List<Movie>, DataError> {

        return networkDataSource.discoverMovies(
            page = page,
            sortBy = sortBy.value,
            withGenres = withGenres,
            primaryReleaseYear = primaryReleaseYear,
            voteAverageGte = voteAverageGte
        ).map { it.toDomainList() }

    }

}