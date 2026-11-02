package com.fimly.app.di

import com.fimly.app.data.repository.MovieRepositoryImpl
import com.fimly.app.data.repository.UserDataRepositoryImpl
import com.fimly.app.domain.repository.MovieRepository
import com.fimly.app.domain.repository.UserDataRepository
import org.koin.dsl.module
val repositoryModule = module {
    single<UserDataRepository> { UserDataRepositoryImpl(get()) }
    single<MovieRepository>{ MovieRepositoryImpl(get()) }
//    singleOf(::RepositoryImpl) bind Repository::class
}