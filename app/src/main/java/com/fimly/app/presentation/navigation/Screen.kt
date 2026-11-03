package com.fimly.app.presentation.navigation

import kotlinx.serialization.Serializable

// Root level destinations (Full-screen flows without Bottom Navigation Bar)
sealed interface RootRoute {
    @Serializable
    data object StartupGraph : RootRoute

    @Serializable
    data object Splash : RootRoute

    @Serializable
    data object Onboarding : RootRoute

    // The container screen that holds the Scaffold and Bottom Navigation Bar
    @Serializable
    data object MainContainer : RootRoute

    // Detail screen is placed at root level so it covers the whole screen (hiding BottomBar naturally)
    @Serializable
    data class MovieDetail(val movieId: Int) : RootRoute
}

// Bottom Navigation Bar tab destinations inside MainContainer
sealed interface BottomTabRoute {
    @Serializable
    data object Home : BottomTabRoute

    @Serializable
    data object Search : BottomTabRoute

    @Serializable
    data object Favorites : BottomTabRoute
    @Serializable

    data object Profile : BottomTabRoute
}