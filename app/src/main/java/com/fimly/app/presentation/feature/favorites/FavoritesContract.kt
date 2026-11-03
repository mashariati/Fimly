package com.fimly.app.presentation.feature.favorites

import com.fimly.app.domain.model.Movie
import com.fimly.app.domain.model.MovieDetail
import com.fimly.app.presentation.core.ContentState
import com.fimly.app.presentation.core.UiEffect
import com.fimly.app.presentation.core.UiIntent
import com.fimly.app.presentation.core.UiState


data class FavoritesState(
    val contentState: ContentState<List<Movie>> = ContentState.Loading
) : UiState

sealed interface FavoritesIntent : UiIntent {
    data class MovieClicked(val movieId: Int) : FavoritesIntent
    data class RemoveFavoriteClicked(val movie: Movie) : FavoritesIntent
    data class UndoRemoveFavorite(val movie: Movie) : FavoritesIntent
    data object ClearAllFavoritesClicked : FavoritesIntent
}

sealed interface FavoritesEffect : UiEffect {
    data class NavigateToDetail(val movieId: Int) : FavoritesEffect
    data class ShowUndoSnackBar(val removedMovie: Movie, val message: String) : FavoritesEffect
}