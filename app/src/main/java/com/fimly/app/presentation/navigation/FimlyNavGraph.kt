package com.fimly.app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.fimly.app.presentation.feature.home.HomeScreen

@Composable
fun FimlyNavGraph(
    navController: NavHostController = rememberNavController(),
    isUserLoggedInOrOnboarded: Boolean = false
) {
    val rootStartGraph = if (isUserLoggedInOrOnboarded) {
        GraphRoute.MainGraph
    } else {
        GraphRoute.AuthGraph
    }
    NavHost(navController = navController, startDestination = rootStartGraph) {

        composable<GraphRoute.AuthGraph> {
            AuthNavGraph(
                onAuthFinished = {
                    navController.navigate(GraphRoute.MainGraph) {
                        popUpTo(GraphRoute.AuthGraph) { inclusive = true }
                    }
                }
            )
        }
        composable<GraphRoute.MainGraph> {
            MainAppNavGraph()
        }
    }
}

@Composable
fun AuthNavGraph(onAuthFinished: () -> Unit) {
    val authController = rememberNavController()
    NavHost(
        navController = authController,
        startDestination = AuthScreen.Splash
    ) {
        composable<AuthScreen.Splash> {

        }

        composable<AuthScreen.Onboarding> {
            onAuthFinished()
        }
    }
}

@Composable
fun MainAppNavGraph() {
    val mainController = rememberNavController()

    NavHost(
        navController = mainController,
        startDestination = AppScreen.Home
    ) {
        composable<AppScreen.Home> {
            HomeScreen(
//                onNavigateToDetail = { movieId ->
//                    mainController.navigate(AppScreen.Detail(movieId = movieId))
//                }
            )
        }

        composable<AppScreen.Detail> { backStackEntry ->
            val args = backStackEntry.toRoute<AppScreen.Detail>()
            // DetailScreen(movieId = args.movieId, onNavigateBack = { mainController.navigateUp() })
        }
    }
}