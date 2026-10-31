package com.fimly.app.di

import com.fimly.app.data.repository.RepositoryImpl
import com.fimly.app.domain.repository.Repository
import org.koin.dsl.module

val repositoryModule = module {
    single<Repository>{ RepositoryImpl(get()) }
//    singleOf(::RepositoryImpl) bind Repository::class
}