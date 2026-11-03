package com.fimly.app.presentation.feature.home

import com.fimly.app.domain.model.Genre
import com.fimly.app.domain.model.Movie
import com.fimly.app.presentation.core.ContentState
import com.fimly.app.presentation.core.UiEffect
import com.fimly.app.presentation.core.UiIntent
import com.fimly.app.presentation.core.UiState


enum class HomeSection {
    BANNER,
    GENRES,
    MOST_POPULAR,
    NOW_PLAYING,
    TOP_RATED,
    UPCOMING
}

data class HomeState(
    val selectedGenreId: Int? = null,
    val genresState: ContentState<List<Genre>> = ContentState.Loading,
    val bannerState: ContentState<List<Movie>> = ContentState.Loading,
    val mostPopularState: ContentState<List<Movie>> = ContentState.Loading,
    val nowPlayingState: ContentState<List<Movie>> = ContentState.Loading,
    val topRatedState: ContentState<List<Movie>> = ContentState.Loading,
    val upcomingState: ContentState<List<Movie>> = ContentState.Loading
) : UiState

sealed interface HomeIntent : UiIntent {
    data class SelectGenre(val genreId: Int?) : HomeIntent
    data class MovieClicked(val movieId: Int) : HomeIntent
    data class RetrySection(val section: HomeSection) : HomeIntent
}

sealed interface HomeEffect : UiEffect {
    data class NavigateToDetail(val movieId: Int) : HomeEffect
}