package com.fimly.app.presentation.navigation

import kotlinx.serialization.Serializable

/**
 * Root routes for navigation graphs
 */
sealed interface GraphRoute {
    @Serializable data object AuthGraph : GraphRoute
    @Serializable data object MainGraph : GraphRoute
}

/**
 * Screens inside the Auth & Onboarding Graph
 */
sealed interface AuthScreen {
    @Serializable data object Splash : AuthScreen
    @Serializable data object Onboarding : AuthScreen
}
/**
 * Screens inside the Main App Graph
 */
sealed interface AppScreen{
    @Serializable
    data object Home: AppScreen
    @Serializable
    data class Detail(val carId: String) : AppScreen
}

