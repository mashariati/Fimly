package com.fimly.app.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GenreListDto (
    @SerialName("genres") val genres: List<GenreDto?>? = null
)
@Serializable
data class GenreDto(
    @SerialName("id") val id: Int? = null,
    @SerialName("name") val name: String? = null
)
