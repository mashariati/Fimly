package com.fimly.app.presentation.feature.search

import com.fimly.app.domain.model.Movie
import com.fimly.app.presentation.core.UiEffect
import com.fimly.app.presentation.core.UiIntent
import com.fimly.app.presentation.core.UiState

sealed interface SearchContentState {
    data object Idle : SearchContentState
    data object Loading : SearchContentState
    data object Empty : SearchContentState
    data class Error(val message: String) : SearchContentState
    data class Success(val movies: List<Movie>) : SearchContentState
}

data class SearchState(
    val query: String = "",
    val contentState: SearchContentState = SearchContentState.Idle
) : UiState

sealed interface SearchIntent : UiIntent {
    data class QueryChanged(val query: String) : SearchIntent
    data object ClearQueryClicked : SearchIntent
    data class MovieClicked(val movieId: Int) : SearchIntent
    data object RetryClicked : SearchIntent
}

sealed interface SearchEffect : UiEffect {
    data class NavigateToDetail(val movieId: Int) : SearchEffect
}

// --- Dummy Data from TMDB "/3/search/movie" JSON ---

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
    )
)