package com.fimly.app.di

import com.fimly.app.presentation.feature.home.HomeViewModel
import com.fimly.app.presentation.feature.onboarding.OnboardingViewModel
import com.fimly.app.presentation.feature.splash.SplashViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { SplashViewModel(get()) }
    viewModel { OnboardingViewModel(get()) }
//    viewModel{ HomeViewModel(get()) }
//    viewModelOf(::HomeViewModel)
}