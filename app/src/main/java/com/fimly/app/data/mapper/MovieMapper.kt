package com.fimly.app.data.mapper

import com.fimly.app.data.network.dto.MovieDto
import com.fimly.app.data.network.dto.MovieListDto
import com.fimly.app.domain.model.Movie

fun MovieDto.toDomainModel(): Movie? {
    val validId = id ?: return null
    val validTitle = title?.takeIf { it.isNotBlank() }
        ?: originalTitle?.takeIf { it.isNotBlank() }
        ?: return null

    return Movie(
        id = validId,
        title = validTitle,
        originalTitle = originalTitle.orEmpty(),
        overview = overview.orEmpty(),
        posterPath = posterPath,
        backdropPath = backdropPath,
        releaseDate = releaseDate.orEmpty(),
        popularity = popularity ?: 0.0,
        voteAverage = voteAverage ?: 0.0,
        voteCount = voteCount ?: 0,
        isAdult = adult ?: false,
        genreIds = genreIds?.filterNotNull() ?: emptyList()
    )
}

fun MovieListDto.toDomainList(): List<Movie> {
    return results?.mapNotNull { it?.toDomainModel() } ?: emptyList()
}