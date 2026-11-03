package com.fimly.app.data.repository
import com.fimly.app.data.local.database.dao.MovieDao
import com.fimly.app.data.mapper.toDomainList
import com.fimly.app.data.mapper.toDomainModel
import com.fimly.app.data.mapper.toEntity
import com.fimly.app.data.network.NetworkDataSource
import com.fimly.app.domain.model.Genre
import com.fimly.app.domain.model.Movie
import com.fimly.app.domain.model.MovieDetail
import com.fimly.app.domain.model.MovieSortBy
import com.fimly.app.domain.repository.MovieRepository
import com.fimly.app.domain.util.AppResult
import com.fimly.app.domain.util.DataError
import com.fimly.app.domain.util.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class MovieRepositoryImpl(private val networkDataSource: NetworkDataSource,private val movieDao: MovieDao): MovieRepository {
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
    override suspend fun searchMovies(
        query: String,
        page: Int,
        primaryReleaseYear: String?
    ): AppResult<List<Movie>, DataError> {
        return networkDataSource.searchMovies(
            query = query,
            page = page,
            primaryReleaseYear = primaryReleaseYear
        ).map { it.toDomainList() }
    }

    override suspend fun getMovieDetails(
        movieId: Int
    ): AppResult<MovieDetail, DataError> {
        return when (val result = networkDataSource.getMovieDetails(movieId)) {
            is AppResult.Success -> {
                val domainModel = result.data.toDomainModel()
                if (domainModel != null) {
                    AppResult.Success(domainModel)
                } else {
                    AppResult.Error(DataError.SERIALIZATION_ERROR, "Invalid movie details data received")
                }
            }
            is AppResult.Error -> AppResult.Error(result.error, result.message)
        }
    }

    override suspend fun getMovieGenres(
    ): AppResult<List<Genre>, DataError> {
        return networkDataSource.getMovieGenres().map { it.toDomainList() }
    }

    override fun getFavoriteMovies(): Flow<List<Movie>> {
        return movieDao.getAllFavorites().map { entities ->
            entities.toDomainList()
        }
    }

    override fun isMovieFavorite(movieId: Int): Flow<Boolean> {
        return movieDao.isFavorite(movieId)
    }

    override suspend fun toggleFavorite(movie: Movie) {
        val isCurrentlyFavorite = movieDao.isFavorite(movie.id).first()
        if (isCurrentlyFavorite) {
            movieDao.deleteFavoriteById(movie.id)
        } else {
            movieDao.insertFavorite(movie.toEntity())
        }
    }

    override suspend fun removeFavorite(movieId: Int) {
        movieDao.deleteFavoriteById(movieId)
    }

    override suspend fun clearAllFavorites() {
        movieDao.deleteAllFavorites()
    }

}