package com.fimly.app.presentation.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.fimly.app.R
import com.fimly.app.presentation.feature.detail.DetailScreen
import com.fimly.app.presentation.feature.home.HomeScreen
import com.fimly.app.presentation.feature.onboarding.OnboardingScreen
import com.fimly.app.presentation.feature.splash.SplashScreen
import com.fimly.app.presentation.theme.PrimarySoft

private data class BottomNavItem(
    val label: String,
    val icon: Painter,
    val route: BottomTabRoute
)

@Composable
fun FimlyNavGraph(
    rootNavController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = rootNavController,
        startDestination = RootRoute.StartupGraph
    ) {
        navigation<RootRoute.StartupGraph>(
            startDestination = RootRoute.Splash
        ) {
            composable<RootRoute.Splash> {
                 SplashScreen(
                     onNavigateToOnboarding = {
                         rootNavController.navigate(RootRoute.Onboarding) {
                             popUpTo(RootRoute.Splash) { inclusive = true }
                         }
                     },
                     onNavigateToMainApp = {
                         rootNavController.navigate(RootRoute.MainContainer) {
                             popUpTo(RootRoute.StartupGraph) { inclusive = true }
                         }
                     }
                 )
            }

            composable<RootRoute.Onboarding> {
                 OnboardingScreen(
                     onFinishOnboarding = {
                         rootNavController.navigate(RootRoute.MainContainer) {
                             popUpTo(RootRoute.StartupGraph) { inclusive = true }
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

        // Full-Screen Movie Detail
        composable<RootRoute.MovieDetail> { backStackEntry ->
            val route = backStackEntry.toRoute<RootRoute.MovieDetail>()
             DetailScreen(
                 movieId = route.movieId,
                 onNavigateBack = { rootNavController.navigateUp() }
             )
        }
    }
}

@Composable
fun MainScaffoldScreen(
    onNavigateToMovieDetail: (Int) -> Unit
) {
    // Dedicated NavController for switching between Bottom Bar tabs
    val tabNavController = rememberNavController()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            FimlyBottomNavigationBar(navController = tabNavController,)
        }
    ) { innerPadding ->
        NavHost(
            navController = tabNavController,
            startDestination = BottomTabRoute.Home,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<BottomTabRoute.Home> {
                HomeScreen(
                    onNavigateToDetail = onNavigateToMovieDetail
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

@Composable
private fun FimlyBottomNavigationBar(
    navController: NavHostController,
) {
    val items = listOf(
        BottomNavItem("Home", painterResource(R.drawable.home), BottomTabRoute.Home),
        BottomNavItem("Search",  painterResource(R.drawable.search), BottomTabRoute.Search),
        BottomNavItem("Favorites", painterResource(R.drawable.heart), BottomTabRoute.Favorites),
        BottomNavItem("Profile",  painterResource(R.drawable.profile), BottomTabRoute.Profile)
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .navigationBarsPadding()
            .height(72.dp)
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEach { item ->
            val isSelected = currentDestination?.hasRoute(item.route::class) == true

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isSelected) PrimarySoft else Color.Transparent)
                    .clickable {
                        if (!isSelected) {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    painter = item.icon,
                    contentDescription = item.label,
                    tint = if (isSelected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    modifier = Modifier.size(24.dp)
                )

                AnimatedVisibility(visible = isSelected) {
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}