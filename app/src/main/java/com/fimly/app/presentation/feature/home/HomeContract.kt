package com.fimly.app.presentation.feature.home

import com.fimly.app.domain.model.Movie
import com.fimly.app.presentation.core.UiEffect
import com.fimly.app.presentation.core.UiIntent
import com.fimly.app.presentation.core.UiState

data class MovieCategory(
    val id: Int?, // null represents "All"
    val name: String
)

/**
 * Explicitly models the 4 required UI states from the technical assignment.
 */
sealed interface HomeContentState {
    data object Loading : HomeContentState
    data object Empty : HomeContentState
    data class Error(val message: String) : HomeContentState
    data class Success(
        val bannerMovies: List<Movie>,
        val mostPopularMovies: List<Movie>,
        val nowPlayingMovies: List<Movie>,
        val topRatedMovies: List<Movie>,
        val upcomingMovies: List<Movie>
    ) : HomeContentState
}

data class HomeState(
    val categories: List<MovieCategory> = defaultCategories,
    val selectedCategoryId: Int? = null,
    val contentState: HomeContentState = HomeContentState.Loading
) : UiState

sealed interface HomeIntent : UiIntent {
    data class SelectCategory(val categoryId: Int?) : HomeIntent
    data class MovieClicked(val movieId: Int) : HomeIntent
    data object RetryClicked : HomeIntent
}

sealed interface HomeEffect : UiEffect {
    data class NavigateToDetail(val movieId: Int) : HomeEffect
}

// --- Default Categories & Dummy Data for UI Development & Previews ---

val defaultCategories = listOf(
    MovieCategory(id = null, name = "All"),
    MovieCategory(id = 35, name = "Comedy"),
    MovieCategory(id = 16, name = "Animation"),
    MovieCategory(id = 99, name = "Documentary"),
    MovieCategory(id = 28, name = "Action"),
    MovieCategory(id = 878, name = "Sci-Fi")
)

val dummyMovies = listOf(
    Movie(
        id = 155,
        title = "The Dark Knight",
        originalTitle = "The Dark Knight",
        overview = "Batman raises the stakes in his war on crime.",
        posterPath = "/qJ2tW6WMUDux911r6m7haRef0WH.jpg",
        backdropPath = "/9FE5eD92WfVCiivM9Pq9GVSrlWk.jpg",
        releaseDate = "2008-07-16",
        popularity = 4135.6,
        voteAverage = 8.5,
        voteCount = 37150,
        isAdult = false,
        genreIds = listOf(28, 53, 80)
    ),
    Movie(
        id = 969681,
        title = "Spider-Man: Brand New Day",
        originalTitle = "Spider-Man: Brand New Day",
        overview = "Fighting crime full-time as Spider-Man in a world that doesn't remember him.",
        posterPath = "/bjiS5ipwxb9JFy3XRRN4OAilSeX.jpg",
        backdropPath = "/4tjxYg2AbOzzG9G1YuiT2NzYhlH.jpg",
        releaseDate = "2026-07-29",
        popularity = 390.9,
        voteAverage = 8.4,
        voteCount = 6376,
        isAdult = false,
        genreIds = listOf(878, 28, 12)
    ),
    Movie(
        id = 1368337,
        title = "The Odyssey",
        originalTitle = "The Odyssey",
        overview = "Odysseus, the legendary King of Ithaca, embarks on a perilous journey home.",
        posterPath = "/5rhTDKUhPYvpdQIijFIs5VoWsON.jpg",
        backdropPath = "/bulFtfy3oBQtCnAMSi7g6DSSds3.jpg",
        releaseDate = "2026-07-15",
        popularity = 202.8,
        voteAverage = 8.0,
        voteCount = 4314,
        isAdult = false,
        genreIds = listOf(12, 28, 14)
    ),
    Movie(
        id = 354912,
        title = "Coco",
        originalTitle = "Coco",
        overview = "Miguel dreams of becoming an accomplished musician like his idol.",
        posterPath = "/6Ryitt95xrO8KXuqRGm1fUuNwqF.jpg",
        backdropPath = "/g7CHF8gTLGooTbP4GznIGwaqAGL.jpg",
        releaseDate = "2017-10-27",
        popularity = 133.1,
        voteAverage = 8.2,
        voteCount = 21684,
        isAdult = false,
        genreIds = listOf(10751, 16)
    )
)

val dummyHomeSuccessState = HomeState(
    selectedCategoryId = null,
    contentState = HomeContentState.Success(
        bannerMovies = dummyMovies.take(3),
        mostPopularMovies = dummyMovies,
        nowPlayingMovies = dummyMovies.reversed(),
        topRatedMovies = dummyMovies,
        upcomingMovies = dummyMovies.shuffled()
    )
)