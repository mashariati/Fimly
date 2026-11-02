package com.fimly.app.domain.model

import java.util.Locale


data class ProductionCompany(
    val id: Int,
    val name: String,
    val logoPath: String?,
    val originCountry: String
) {
    val fullLogoUrl: String?
        get() = logoPath?.let { "https://image.tmdb.org/t/p/w500$it" }//todo : change it to a Constraint variable
}

data class MovieDetail(
    val id: Int,
    val title: String,
    val originalTitle: String,
    val tagline: String,
    val overview: String,
    val posterPath: String?,
    val backdropPath: String?,
    val releaseDate: String,
    val runtime: Int,
    val voteAverage: Double,
    val voteCount: Int,
    val genres: List<Genre>,
    val productionCompanies: List<ProductionCompany>,
    val status: String,
    val originalLanguage: String,
    val budget: Long,
    val revenue: Long,
    val homepage: String?,
    val isFavorite: Boolean = false
) {
    val fullPosterUrl: String?
        get() = posterPath?.let { "https://image.tmdb.org/t/p/w500$it" }

    val fullBackdropUrl: String?
        get() = backdropPath?.let { "https://image.tmdb.org/t/p/w780$it" }

    val releaseYear: String
        get() = releaseDate.takeIf { it.length >= 4 }?.substring(0, 4) ?: "N/A"

    val formattedRuntime: String
        get() = if (runtime > 0) "$runtime Minutes" else "N/A"

    val primaryGenre: String
        get() = genres.firstOrNull()?.name ?: "Movie"

    val formattedRating: String
        get() = if (voteAverage > 0.0) {
            String.format(Locale.US, "%.1f", voteAverage)
        } else {
            "N/A"
        }

    val formattedBudget: String
        get() = formatCurrency(budget)

    val formattedRevenue: String
        get() = formatCurrency(revenue)

    private fun formatCurrency(amount: Long): String {
        if (amount <= 0L) return "N/A"
        return when {
            amount >= 1_000_000_000 -> String.format(Locale.US, "$%.1fB", amount / 1_000_000_000.0)
            amount >= 1_000_000 -> String.format(Locale.US, "$%.1fM", amount / 1_000_000.0)
            amount >= 1_000 -> String.format(Locale.US, "$%.1fK", amount / 1_000.0)
            else -> "$$amount"
        }
    }
}