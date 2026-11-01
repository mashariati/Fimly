package com.fimly.app.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.fimly.app.presentation.feature.home.HomeScreen
import com.fimly.app.presentation.feature.onboarding.OnboardingScreen
import com.fimly.app.presentation.feature.splash.SplashScreen

@Composable
fun FimlyNavGraph(
    rootNavController: NavHostController = rememberNavController(),
    isUserOnboarded: Boolean = false
) {
    val startDestination = if (isUserOnboarded) {
        RootRoute.MainContainer
    } else {
        RootRoute.AuthGraph
    }

    NavHost(
        navController = rootNavController,
        startDestination = startDestination
    ) {
        // Auth & Onboarding Flow
        navigation<RootRoute.AuthGraph>(
            startDestination = RootRoute.Splash
        ) {
            composable<RootRoute.Splash> {
                 SplashScreen(
                     onSplashFinished = {
                         rootNavController.navigate(RootRoute.Onboarding) {
                             popUpTo(RootRoute.Splash) { inclusive = true }
                         }
                     }
                 )
            }

            composable<RootRoute.Onboarding> {
                 OnboardingScreen(
                     onFinishOnboarding = {
                         // TODO later: Save true to DataStore here
                         rootNavController.navigate(RootRoute.MainContainer) {
                             popUpTo(RootRoute.AuthGraph) { inclusive = true }
                         }
                     }
                 )
            }
        }

        // Main Container (Has Scaffold + Bottom Navigation Bar)
        composable<RootRoute.MainContainer> {
            MainScaffoldScreen(
                onNavigateToMovieDetail = { movieId ->
                    // Navigates using root controller to open Detail full-screen
                    rootNavController.navigate(RootRoute.MovieDetail(movieId = movieId))
                }
            )
        }

        // Full-Screen Movie Detail (No BottomBar)
        composable<RootRoute.MovieDetail> { backStackEntry ->
            val route = backStackEntry.toRoute<RootRoute.MovieDetail>()
            // DetailScreen(
            //     movieId = route.movieId,
            //     onNavigateBack = { rootNavController.navigateUp() }
            // )
        }
    }
}

/**
 * Main screen container that holds the BottomNavigationBar and switches between tabs.
 */
@Composable
fun MainScaffoldScreen(
    onNavigateToMovieDetail: (Int) -> Unit
) {
    // Dedicated NavController for switching between Bottom Bar tabs
    val tabNavController = rememberNavController()

    Scaffold(
        bottomBar = {
            // TODO:custom BottomNavigationBar composable
//            tabNavController.navigate(tabRoute) {
//                // Pop up to the start destination of the graph to avoid building up a large stack of destinations
//                popUpTo(tabNavController.graph.findStartDestination().id) {
//                    saveState = true
//                }
//                // Avoid multiple copies of the same destination when reselecting the same item
//                launchSingleTop = true
//                // Restore state when reselecting a previously selected item
//                restoreState = true
//            }
            // It will use tabNavController to switch between Home, Search, and Favorites
        }
    ) { innerPadding ->
        NavHost(
            navController = tabNavController,
            startDestination = BottomTabRoute.Home,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<BottomTabRoute.Home> {
                HomeScreen(
                    // Pass the callback to open Detail screen via rootNavController
                    // onMovieClick = onNavigateToMovieDetail
                )
            }

            composable<BottomTabRoute.Search> {
                // SearchScreen(onMovieClick = onNavigateToMovieDetail)
            }

            composable<BottomTabRoute.Favorites> {
                // FavoritesScreen(onMovieClick = onNavigateToMovieDetail)
            }
        }
    }
}