package com.fimly.app.domain.usecase

import com.fimly.app.domain.model.Genre
import com.fimly.app.domain.repository.MovieRepository
import com.fimly.app.domain.util.AppResult
import com.fimly.app.domain.util.DataError

class GetMovieGenresUseCase(
    private val movieRepository: MovieRepository
) {
    suspend operator fun invoke(): AppResult<List<Genre>, DataError> {
        return movieRepository.getMovieGenres()
    }
}