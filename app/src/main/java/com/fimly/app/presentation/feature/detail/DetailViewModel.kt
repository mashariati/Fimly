package com.fimly.app.presentation.feature.detail

import androidx.lifecycle.viewModelScope
import com.fimly.app.data.mapper.toMovie
import com.fimly.app.domain.usecase.GetMovieDetailsUseCase
import com.fimly.app.domain.usecase.ObserveFavoriteStatusUseCase
import com.fimly.app.domain.usecase.ToggleFavoriteMovieUseCase
import com.fimly.app.domain.util.AppResult
import com.fimly.app.presentation.core.BaseViewModel
import com.fimly.app.presentation.core.ContentState
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch


class DetailViewModel(
    private val movieId: Int,
    private val getMovieDetailsUseCase: GetMovieDetailsUseCase,
    private val observeFavoriteStatusUseCase: ObserveFavoriteStatusUseCase,
    private val toggleFavoriteMovieUseCase: ToggleFavoriteMovieUseCase
) : BaseViewModel<DetailState, DetailIntent, DetailEffect>(DetailState()) {

    init {
        loadMovieDetails()
        observeFavoriteStatus()
    }

    override fun handleIntent(intent: DetailIntent) {
        when (intent) {
            is DetailIntent.BackClicked -> sendEffect { DetailEffect.NavigateBack }
            is DetailIntent.RetryClicked -> loadMovieDetails()
            is DetailIntent.ToggleFavoriteClicked -> toggleFavorite()
        }
    }

    private fun loadMovieDetails() {
        viewModelScope.launch {
            updateState { copy(contentState = ContentState.Loading) }

            when (val result = getMovieDetailsUseCase(movieId)) {
                is AppResult.Success -> {
                    updateState {
                        copy(contentState = ContentState.Success(result.data))
                    }
                }
                is AppResult.Error -> {
                    updateState {
                        copy(
                            contentState = ContentState.Error(
                                result.message ?: "Something went wrong. Please try again."
                            )
                        )
                    }
                }
            }
        }
    }

    private fun observeFavoriteStatus() {
        observeFavoriteStatusUseCase(movieId)
            .onEach { isFav ->
                val currentContent = currentState.contentState
                if (currentContent is ContentState.Success) {
                    updateState {
                        copy(
                            contentState = ContentState.Success(
                                currentContent.data.copy(isFavorite = isFav)
                            )
                        )
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    private fun toggleFavorite() {
        val currentContent = currentState.contentState
        if (currentContent !is ContentState.Success) return

        val movieDetail = currentContent.data
        val willBeFavorite = !movieDetail.isFavorite

        viewModelScope.launch {
            toggleFavoriteMovieUseCase(movieDetail.toMovie())
            val message = if (willBeFavorite) {
                "Added to Favorites"
            } else {
                "Removed from Favorites"
            }
            sendEffect { DetailEffect.ShowSnackBar(message) }
        }
    }
}