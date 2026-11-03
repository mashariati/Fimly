package com.fimly.app.presentation.feature.favorites

import androidx.lifecycle.viewModelScope
import com.fimly.app.domain.model.Movie
import com.fimly.app.domain.usecase.ClearAllFavoritesUseCase
import com.fimly.app.domain.usecase.GetFavoriteMoviesUseCase
import com.fimly.app.domain.usecase.RemoveFavoriteMovieUseCase
import com.fimly.app.domain.usecase.ToggleFavoriteMovieUseCase
import com.fimly.app.presentation.core.BaseViewModel
import com.fimly.app.presentation.core.ContentState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val getFavoriteMoviesUseCase: GetFavoriteMoviesUseCase,
    private val removeFavoriteMovieUseCase: RemoveFavoriteMovieUseCase,
    private val toggleFavoriteMovieUseCase: ToggleFavoriteMovieUseCase,
    private val clearAllFavoritesUseCase: ClearAllFavoritesUseCase
) : BaseViewModel<FavoritesState, FavoritesIntent, FavoritesEffect>(FavoritesState()) {

    private var observeJob: Job? = null

    init {
        observeFavorites()
    }

    override fun handleIntent(intent: FavoritesIntent) {
        when (intent) {
            is FavoritesIntent.MovieClicked -> {
                sendEffect { FavoritesEffect.NavigateToDetail(intent.movieId) }
            }
            is FavoritesIntent.RemoveFavoriteClicked -> {
                removeMovieFromFavorites(intent.movie)
            }
            is FavoritesIntent.UndoRemoveFavorite -> {
                restoreRemovedMovie(intent.movie)
            }
            is FavoritesIntent.ClearAllFavoritesClicked -> {
                clearAllFavorites()
            }
        }
    }

    private fun observeFavorites() {
        observeJob?.cancel()
        updateState { copy(contentState = ContentState.Loading) }

        observeJob = getFavoriteMoviesUseCase()
            .onEach { movies ->
                updateState {
                    copy(
                        contentState = if (movies.isEmpty()) {
                            ContentState.Empty
                        } else {
                            ContentState.Success(movies)
                        }
                    )
                }
            }
            .catch { throwable ->
                updateState {
                    copy(
                        contentState = ContentState.Error(
                            throwable.message ?: "Failed to load favorites from local storage."
                        )
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private fun removeMovieFromFavorites(movie: Movie) {
        viewModelScope.launch {
            removeFavoriteMovieUseCase(movie.id)
            sendEffect {
                FavoritesEffect.ShowUndoSnackBar(
                    removedMovie = movie,
                    message = "${movie.title} removed from Favorites"
                )
            }
        }
    }

    private fun restoreRemovedMovie(movie: Movie) {
        viewModelScope.launch {
            toggleFavoriteMovieUseCase(movie)
        }
    }

    private fun clearAllFavorites() {
        viewModelScope.launch {
            clearAllFavoritesUseCase()
        }
    }
}