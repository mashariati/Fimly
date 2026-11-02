package com.fimly.app.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class ProductionCompanyDto(
    @SerialName("id") val id: Int? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("logo_path") val logoPath: String? = null,
    @SerialName("origin_country") val originCountry: String? = null
)

@Serializable
data class MovieDetailDto(
    @SerialName("id") val id: Int? = null,
    @SerialName("title") val title: String? = null,
    @SerialName("original_title") val originalTitle: String? = null,
    @SerialName("tagline") val tagline: String? = null,
    @SerialName("overview") val overview: String? = null,
    @SerialName("poster_path") val posterPath: String? = null,
    @SerialName("backdrop_path") val backdropPath: String? = null,
    @SerialName("release_date") val releaseDate: String? = null,
    @SerialName("runtime") val runtime: Int? = null,
    @SerialName("vote_average") val voteAverage: Double? = null,
    @SerialName("vote_count") val voteCount: Int? = null,
    @SerialName("genres") val genres: List<GenreDto?>? = null,
    @SerialName("production_companies") val productionCompanies: List<ProductionCompanyDto?>? = null,
    @SerialName("status") val status: String? = null,
    @SerialName("original_language") val originalLanguage: String? = null,
    @SerialName("budget") val budget: Long? = null,
    @SerialName("revenue") val revenue: Long? = null,
    @SerialName("homepage") val homepage: String? = null,
    @SerialName("adult") val adult: Boolean? = null
)