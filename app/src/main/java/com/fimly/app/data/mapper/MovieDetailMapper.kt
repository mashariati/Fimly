package com.fimly.app.data.mapper

import com.fimly.app.data.network.dto.GenreDto
import com.fimly.app.data.network.dto.MovieDetailDto
import com.fimly.app.data.network.dto.ProductionCompanyDto
import com.fimly.app.domain.model.Genre
import com.fimly.app.domain.model.MovieDetail
import com.fimly.app.domain.model.ProductionCompany



fun ProductionCompanyDto.toDomainModel(): ProductionCompany? {
    return ProductionCompany(
        id = id ?: return null,
        name = name ?: "Unknown",
        logoPath = logoPath,
        originCountry = originCountry ?: ""
    )
}

fun MovieDetailDto.toDomainModel(): MovieDetail? {
    val validId = id ?: return null
    val validTitle = title?.takeIf { it.isNotBlank() }
        ?: originalTitle?.takeIf { it.isNotBlank() }
        ?: return null

    return MovieDetail(
        id = validId,
        title = validTitle,
        originalTitle = originalTitle.orEmpty(),
        tagline = tagline.orEmpty(),
        overview = overview.orEmpty(),
        posterPath = posterPath,
        backdropPath = backdropPath,
        releaseDate = releaseDate.orEmpty(),
        runtime = runtime ?: 0,
        voteAverage = voteAverage ?: 0.0,
        voteCount = voteCount ?: 0,
        genres = genres?.mapNotNull { it?.toDomainModel() } ?: emptyList(),
        productionCompanies = productionCompanies?.mapNotNull { it?.toDomainModel() } ?: emptyList(),
        status = status.orEmpty(),
        originalLanguage = originalLanguage.orEmpty(),
        budget = budget ?: 0L,
        revenue = revenue ?: 0L,
        homepage = homepage,
        isFavorite = false
    )
}