package com.fimly.app.data.mapper

import com.fimly.app.data.local.database.entity.MovieEntity
import com.fimly.app.domain.model.Movie
import com.fimly.app.domain.model.MovieDetail

fun MovieEntity.toDomainModel(): Movie {
    return Movie(
        id = id,
        title = title,
        originalTitle = originalTitle,
        overview = overview,
        posterPath = posterPath,
        backdropPath = backdropPath,
        releaseDate = releaseDate,
        popularity = popularity,
        voteAverage = voteAverage,
        voteCount = voteCount,
        isAdult = isAdult,
        genreIds = genreIds,
        isFavorite = true
    )
}

fun Movie.toEntity(): MovieEntity {
    return MovieEntity(
        id = id,
        title = title,
        originalTitle = originalTitle,
        overview = overview,
        posterPath = posterPath,
        backdropPath = backdropPath,
        releaseDate = releaseDate,
        popularity = popularity,
        voteAverage = voteAverage,
        voteCount = voteCount,
        isAdult = isAdult,
        genreIds = genreIds
    )
}

fun List<MovieEntity>.toDomainList(): List<Movie> {
    return this.map { it.toDomainModel() }
}

fun MovieDetail.toMovie(): Movie {
    return Movie(
        id = id,
        title = title,
        originalTitle = originalTitle,
        overview = overview,
        posterPath = posterPath,
        backdropPath = backdropPath,
        releaseDate = releaseDate,
        popularity = 0.0,
        voteAverage = voteAverage,
        voteCount = voteCount,
        isAdult = false,
        genreIds = genres.map { it.id },
        isFavorite = isFavorite
    )
}