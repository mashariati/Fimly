package com.fimly.app.domain.usecase

import com.fimly.app.domain.model.MovieDetail
import com.fimly.app.domain.repository.MovieRepository
import com.fimly.app.domain.util.AppResult
import com.fimly.app.domain.util.DataError

class GetMovieDetailsUseCase(
    private val movieRepository: MovieRepository
) {
    suspend operator fun invoke(movieId: Int): AppResult<MovieDetail, DataError> {
        return movieRepository.getMovieDetails(movieId)
    }
}