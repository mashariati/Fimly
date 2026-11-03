package com.fimly.app.di

import com.fimly.app.presentation.feature.detail.DetailViewModel
import com.fimly.app.presentation.feature.favorites.FavoritesViewModel
import com.fimly.app.presentation.feature.home.HomeViewModel
import com.fimly.app.presentation.feature.onboarding.OnboardingViewModel
import com.fimly.app.presentation.feature.search.SearchViewModel
import com.fimly.app.presentation.feature.splash.SplashViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { SplashViewModel(get()) }
    viewModel { OnboardingViewModel(get()) }
    viewModelOf(::HomeViewModel)
    viewModelOf(::SearchViewModel)
    viewModel { (movieId: Int) ->
        DetailViewModel(
            movieId = movieId,
            getMovieDetailsUseCase = get(),
            observeFavoriteStatusUseCase = get(),
            toggleFavoriteMovieUseCase = get()
        )
    }
    viewModelOf(::FavoritesViewModel)

}