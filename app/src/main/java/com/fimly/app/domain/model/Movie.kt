package com.fimly.app.domain.model

data class Movie(
    val id: Int,
    val title: String,
    val originalTitle: String,
    val overview: String,
    val posterUrl: String?,
    val backdropUrl: String?,
    val releaseDate: String,
    val popularity: Double,
    val voteAverage: Double,
    val voteCount: Int,
    val isAdult: Boolean,
    val genreIds: List<Int>
) {
    val fullPosterUrl: String?
        get() = posterUrl?.let { "https://image.tmdb.org/t/p/w500$it" }
}