package com.fimly.app.presentation.feature.favorites

import com.fimly.app.domain.model.Movie
import com.fimly.app.presentation.core.UiEffect
import com.fimly.app.presentation.core.UiIntent
import com.fimly.app.presentation.core.UiState
import com.fimly.app.presentation.feature.home.dummyMovies

sealed interface FavoritesContentState {
    data object Loading : FavoritesContentState
    data object Empty : FavoritesContentState
    data class Error(val message: String) : FavoritesContentState
    data class Success(val movies: List<Movie>) : FavoritesContentState
}

data class FavoritesState(
    val contentState: FavoritesContentState = FavoritesContentState.Loading
) : UiState

sealed interface FavoritesIntent : UiIntent {
    data class MovieClicked(val movieId: Int) : FavoritesIntent
    data class RemoveFromFavoritesClicked(val movie: Movie) : FavoritesIntent
    data object RetryClicked : FavoritesIntent
}

sealed interface FavoritesEffect : UiEffect {
    data class NavigateToDetail(val movieId: Int) : FavoritesEffect
    data class ShowUndoSnackbar(val removedMovie: Movie) : FavoritesEffect
}

// --- Dummy Data for UI Phase ---

val dummyFavoriteMovies = dummyMovies.map { it.copy(isFavorite = true) }

val dummyFavoritesSuccessState = FavoritesState(
    contentState = FavoritesContentState.Success(movies = dummyFavoriteMovies)
)