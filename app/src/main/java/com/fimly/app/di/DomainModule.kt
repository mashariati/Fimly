package com.fimly.app.di

import com.fimly.app.domain.usecase.DiscoverMoviesUseCase
import com.fimly.app.domain.usecase.GetMovieDetailsUseCase
import com.fimly.app.domain.usecase.GetMovieGenresUseCase
import com.fimly.app.domain.usecase.ReadOnboardingStateUseCase
import com.fimly.app.domain.usecase.SaveOnboardingStateUseCase
import com.fimly.app.domain.usecase.SearchMoviesUseCase
import org.koin.dsl.module

val domainModule = module {
    // We use 'factory' for UseCases because they are lightweight
    // and we don't need to keep them in memory as a Singleton.
    factory { ReadOnboardingStateUseCase(get()) }
    factory { SaveOnboardingStateUseCase(get()) }
    factory { DiscoverMoviesUseCase(get()) }
    factory { GetMovieDetailsUseCase(get()) }
    factory { SearchMoviesUseCase(get()) }
    factory { GetMovieGenresUseCase(get()) }
}