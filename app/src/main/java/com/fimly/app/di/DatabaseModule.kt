package com.fimly.app.di

import androidx.room.Room
import com.fimly.app.data.local.database.FimlyDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

private const val DATABASE_NAME = "fimly.db"

val databaseModule = module {

    single {
        Room.databaseBuilder(
            androidContext(),
            FimlyDatabase::class.java,
            DATABASE_NAME
        ).build()
    }

    single { get<FimlyDatabase>().movieDao() }
}