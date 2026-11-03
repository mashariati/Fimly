package com.fimly.app.di

import com.fimly.app.domain.usecase.ClearAllFavoritesUseCase
import com.fimly.app.domain.usecase.DiscoverMoviesUseCase
import com.fimly.app.domain.usecase.GetFavoriteMoviesUseCase
import com.fimly.app.domain.usecase.GetMovieDetailsUseCase
import com.fimly.app.domain.usecase.GetMovieGenresUseCase
import com.fimly.app.domain.usecase.ObserveFavoriteStatusUseCase
import com.fimly.app.domain.usecase.ReadOnboardingStateUseCase
import com.fimly.app.domain.usecase.RemoveFavoriteMovieUseCase
import com.fimly.app.domain.usecase.SaveOnboardingStateUseCase
import com.fimly.app.domain.usecase.SearchMoviesUseCase
import com.fimly.app.domain.usecase.ToggleFavoriteMovieUseCase
import org.koin.dsl.module

val domainModule = module {
    // Onboarding
    factory { ReadOnboardingStateUseCase(get()) }
    factory { SaveOnboardingStateUseCase(get()) }

    // Remote Movie
    factory { DiscoverMoviesUseCase(get()) }
    factory { SearchMoviesUseCase(get()) }
    factory { GetMovieDetailsUseCase(get()) }
    factory { GetMovieGenresUseCase(get()) }

    // Local Favorites
    factory { GetFavoriteMoviesUseCase(get()) }
    factory { ObserveFavoriteStatusUseCase(get()) }
    factory { ToggleFavoriteMovieUseCase(get()) }
    factory { RemoveFavoriteMovieUseCase(get()) }
    factory { ClearAllFavoritesUseCase(get()) }
}