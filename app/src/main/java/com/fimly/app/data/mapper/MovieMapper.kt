package com.fimly.app.data.mapper
import com.fimly.app.data.network.dto.MovieDto
import com.fimly.app.data.network.dto.MovieResponseDto
import com.fimly.app.domain.model.Movie

private fun MovieDto.toDomainModel(): Movie {
    return Movie(
        id = id,
        title = title,
        originalTitle = originalTitle,
        overview = overview,
        posterUrl = posterPath,
        backdropUrl = backdropPath,
        releaseDate = releaseDate ?: "",
        popularity = popularity,
        voteAverage = voteAverage,
        voteCount = voteCount,
        isAdult = adult,
        genreIds = genreIds
    )
}

fun MovieResponseDto.toDomainList(): List<Movie> {
    return results.mapNotNull { it?.toDomainModel() }
}