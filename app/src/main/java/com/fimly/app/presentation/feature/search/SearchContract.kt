package com.fimly.app.presentation.feature.search

import com.fimly.app.domain.model.Movie
import com.fimly.app.presentation.core.UiEffect
import com.fimly.app.presentation.core.UiIntent
import com.fimly.app.presentation.core.UiState
import com.fimly.app.presentation.feature.home.MovieCategory

/**
 * Models all states of the Search screen:
 * - Idle: Initial state before typing a query
 * - Loading: Shimmer skeleton while fetching results
 * - Empty: No movies matched the query/filter ("We Are Sorry, We Can Not Find The Movie :(")
 * - Error: Network/API failure with Retry button
 * - Success: Filtered list of movies
 */
sealed interface SearchContentState {
    data object Idle : SearchContentState
    data object Loading : SearchContentState
    data object Empty : SearchContentState
    data class Error(val message: String) : SearchContentState
    data class Success(val movies: List<Movie>) : SearchContentState
}

data class SearchState(
    val query: String = "",
    val categories: List<MovieCategory> = searchGenreCategories,
    val selectedCategoryId: Int? = null,
    val selectedYear: String? = null,
    val contentState: SearchContentState = SearchContentState.Idle
) : UiState

sealed interface SearchIntent : UiIntent {
    data class QueryChanged(val query: String) : SearchIntent
    data object ClearQueryClicked : SearchIntent
    data class SelectCategory(val categoryId: Int?) : SearchIntent
    data class MovieClicked(val movieId: Int) : SearchIntent
    data object RetryClicked : SearchIntent
}

sealed interface SearchEffect : UiEffect {
    data class NavigateToDetail(val movieId: Int) : SearchEffect
}

// --- Genre Filter Categories for Search ---

val searchGenreCategories = listOf(
    MovieCategory(id = null, name = "All"),
    MovieCategory(id = 18, name = "Drama"),
    MovieCategory(id = 28, name = "Action"),
    MovieCategory(id = 35, name = "Comedy"),
    MovieCategory(id = 53, name = "Thriller"),
    MovieCategory(id = 16, name = "Animation"),
    MovieCategory(id = 99, name = "Documentary")
)

// --- Dummy Data from your TMDB "/3/search/movie?query=Fight Club" JSON ---

val dummySearchMovies = listOf(
    Movie(
        id = 550,
        title = "Fight Club",
        originalTitle = "Fight Club",
        overview = "A ticking-time-bomb insomniac and a slippery soap salesman channel primal male aggression into a shocking new form of therapy.",
        posterPath = "/pB8BM7pdSp6B6Ih7QZ4DrQ3PmJK.jpg",
        backdropPath = "/hZkgoQYus5vegHoetLkCJzb17zJ.jpg",
        releaseDate = "1999-10-15",
        popularity = 73.433,
        voteAverage = 8.433,
        voteCount = 26279,
        isAdult = false,
        genreIds = listOf(18, 53, 35)
    ),
    Movie(
        id = 289732,
        title = "Zombie Fight Club",
        originalTitle = "屍城",
        overview = "It's the end of the century at a corner of the city in a building riddled with crime - Everyone in the building has turned into zombies.",
        posterPath = "/u8u3KVq0qfJYmNDsaTVOXy4So6f.jpg",
        backdropPath = "/1VqE5z4VIOcNcajJuHLk4fDkY9G.jpg",
        releaseDate = "2014-10-23",
        popularity = 15.263,
        voteAverage = 4.721,
        voteCount = 52,
        isAdult = false,
        genreIds = listOf(28, 27)
    ),
    Movie(
        id = 323667,
        title = "Florence Fight Club",
        originalTitle = "Вставай и бейся",
        overview = "Intertwined stories from the gladiator/athletes participating to the Calcio Storico Fiorentino yearly championship.",
        posterPath = "/rPCOC0myV3Vr7nYGMAOAOpUXgH3.jpg",
        backdropPath = null,
        releaseDate = "2015-06-21",
        popularity = 2.744,
        voteAverage = 5.722,
        voteCount = 9,
        isAdult = false,
        genreIds = listOf(35, 18)
    ),
    Movie(
        id = 973534,
        title = "Fight Club (Russian version)",
        originalTitle = "Бойцовский клуб (русская версия)",
        overview = "The Russian version of the movie \"Fight Club\" created in the very heart of southern Russia, in the city of Krasnodar.",
        posterPath = null, // Missing poster test case!
        backdropPath = null,
        releaseDate = "",  // Missing release date test case!
        popularity = 0.874,
        voteAverage = 7.4,
        voteCount = 6,
        isAdult = false,
        genreIds = emptyList() // Missing genres test case!
    ),
    Movie(
        id = 259016,
        title = "Insane Fight Club",
        originalTitle = "Insane Fight Club",
        overview = "A group of friends have created a brand new subculture that is taking over the streets of Glasgow.",
        posterPath = null,
        backdropPath = null,
        releaseDate = "2014-03-11",
        popularity = 1.614,
        voteAverage = 4.333,
        voteCount = 3,
        isAdult = false,
        genreIds = listOf(99)
    )
)