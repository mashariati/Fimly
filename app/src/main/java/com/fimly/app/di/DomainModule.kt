package com.fimly.app.di

import com.fimly.app.domain.usecase.ReadOnboardingStateUseCase
import com.fimly.app.domain.usecase.SaveOnboardingStateUseCase
import org.koin.dsl.module

val domainModule = module {
    // We use 'factory' for UseCases because they are lightweight
    // and we don't need to keep them in memory as a Singleton.
    factory { ReadOnboardingStateUseCase(get()) }
    factory { SaveOnboardingStateUseCase(get()) }
//    factory { DiscoverMoviesUseCase(get()) }
}