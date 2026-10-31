package com.fimly.app.di

import com.fimly.app.data.repository.MovieRepositoryImpl
import com.fimly.app.domain.repository.MovieRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<MovieRepository>{ MovieRepositoryImpl(get()) }
//    singleOf(::RepositoryImpl) bind Repository::class
}