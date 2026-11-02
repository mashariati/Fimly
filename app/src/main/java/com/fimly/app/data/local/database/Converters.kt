package com.fimly.app.data.local.database

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromGenreIdsList(genreIds: List<Int>?): String {
        return genreIds?.joinToString(",") ?: ""
    }

    @TypeConverter
    fun toGenreIdsList(genreIdsString: String): List<Int> {
        if (genreIdsString.isBlank()) return emptyList()
        return genreIdsString.split(",").mapNotNull { it.trim().toIntOrNull() }
    }
}