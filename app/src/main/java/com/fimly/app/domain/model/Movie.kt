package com.fimly.app.domain.model

import com.fimly.app.domain.util.BASE_URL_IMAGE
import com.fimly.app.domain.util.TmdbGenreHelper
import java.util.Locale

data class Movie(
    val id: Int,
    val title: String,
    val originalTitle: String,
    val overview: String,
    val posterPath: String?,
    val backdropPath: String?,
    val releaseDate: String,
    val popularity: Double,
    val voteAverage: Double,
    val voteCount: Int,
    val isAdult: Boolean,
    val genreIds: List<Int>,
    val isFavorite: Boolean = false
) {
    val fullPosterUrl: String?
        get() = posterPath?.let { "${BASE_URL_IMAGE}/w500$it" }

    val fullBackdropUrl: String?
        get() = backdropPath?.let { "${BASE_URL_IMAGE}/w780$it" }

    val releaseYear: String
        get() = releaseDate.takeIf { it.length >= 4 }?.substring(0, 4) ?: "N/A"

    val formattedRating: String
        get() = if (voteAverage > 0.0) {
            String.format(Locale.US, "%.1f", voteAverage)
        } else {
            "N/A"
        }

    val primaryGenresText: List<String>
        get() = TmdbGenreHelper.getGenreNames(genreIds)
}