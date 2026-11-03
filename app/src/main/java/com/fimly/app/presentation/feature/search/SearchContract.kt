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
    data class Success(
        val movies: List<Movie>,
        val isLoadingNextPage: Boolean = false,
        val paginationError: String? = null,
        val endReached: Boolean = false
    ) : SearchContentState
}

data class SearchState(
    val query: String = "",
    val currentPage: Int = 1,
    val contentState: SearchContentState = SearchContentState.Idle
) : UiState

sealed interface SearchIntent : UiIntent {
    data class QueryChanged(val query: String) : SearchIntent
    data object ClearQueryClicked : SearchIntent
    data object LoadNextPage : SearchIntent
    data object RetryClicked : SearchIntent
    data class MovieClicked(val movieId: Int) : SearchIntent
}

sealed interface SearchEffect : UiEffect {
    data class NavigateToDetail(val movieId: Int) : SearchEffect
}