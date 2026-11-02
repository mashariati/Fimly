package com.fimly.app.data.mapper

import com.fimly.app.data.network.dto.GenreDto
import com.fimly.app.data.network.dto.GenreListDto
import com.fimly.app.domain.model.Genre


fun GenreDto.toDomainModel(): Genre? {
    return Genre(
        id = id ?: return null,
        name = name ?: "Unknown"
    )
}
fun GenreListDto.toDomainList(): List<Genre> {
    return genres?.mapNotNull { it?.toDomainModel() } ?: emptyList()
}