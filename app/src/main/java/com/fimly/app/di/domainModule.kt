package com.fimly.app.di

import org.koin.dsl.module

val domainModule = module {
    // We use 'factory' for UseCases because they are lightweight
    // and we don't need to keep them in memory as a Singleton.
}