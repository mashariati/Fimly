package com.fimly.app.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.fimly.app.data.local.database.dao.MovieDao
import com.fimly.app.data.local.database.entity.MovieEntity

@Database(
    entities = [MovieEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class FimlyDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
}