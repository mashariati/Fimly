package com.fimly.app.presentation.feature.search

import androidx.lifecycle.viewModelScope
import com.fimly.app.domain.usecase.SearchMoviesUseCase
import com.fimly.app.domain.util.AppResult
import com.fimly.app.presentation.core.BaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class SearchViewModel(
    private val searchMoviesUseCase: SearchMoviesUseCase
) : BaseViewModel<SearchState, SearchIntent, SearchEffect>(SearchState()) {

    private companion object {
        const val FIRST_PAGE = 1
        const val SEARCH_DEBOUNCE_MS = 500L
    }

    private var searchJob: Job? = null
    private var paginationJob: Job? = null

    override fun handleIntent(intent: SearchIntent) {
        when (intent) {
            is SearchIntent.QueryChanged -> onQueryChanged(intent.query)
            is SearchIntent.ClearQueryClicked -> clearSearch()
            is SearchIntent.LoadNextPage -> loadNextPage()
            is SearchIntent.RetryClicked -> retrySearch()
            is SearchIntent.MovieClicked -> sendEffect { SearchEffect.NavigateToDetail(intent.movieId) }
        }
    }

    private fun onQueryChanged(newQuery: String) {
        if (currentState.query == newQuery) return

        searchJob?.cancel()
        paginationJob?.cancel()

        if (newQuery.isBlank()) {
            updateState {
                copy(
                    query = "",
                    currentPage = FIRST_PAGE,
                    contentState = SearchContentState.Idle
                )
            }
            return
        }

        updateState { copy(query = newQuery) }

        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_MS.milliseconds)
            executeInitialSearch(newQuery.trim())
        }
    }

    private fun clearSearch() {
        searchJob?.cancel()
        paginationJob?.cancel()
        updateState {
            SearchState(
                query = "",
                currentPage = FIRST_PAGE,
                contentState = SearchContentState.Idle
            )
        }
    }

    private fun retrySearch() {
        val currentQuery = currentState.query.trim()
        if (currentQuery.isBlank()) return

        val content = currentState.contentState
        // If we already have movies and pagination failed, retry loading the next page
        if (content is SearchContentState.Success && content.paginationError != null) {
            loadNextPage()
        } else {
            searchJob?.cancel()
            searchJob = viewModelScope.launch {
                executeInitialSearch(currentQuery)
            }
        }
    }

    private suspend fun executeInitialSearch(query: String) {
        updateState {
            copy(
                currentPage = FIRST_PAGE,
                contentState = SearchContentState.Loading
            )
        }

        when (val result = searchMoviesUseCase(query = query, page = FIRST_PAGE)) {
            is AppResult.Success -> {
                val movies = result.data.distinctBy { it.id }
                updateState {
                    copy(
                        currentPage = FIRST_PAGE,
                        contentState = if (movies.isEmpty()) {
                            SearchContentState.Empty
                        } else {
                            SearchContentState.Success(
                                movies = movies,
                                isLoadingNextPage = false,
                                paginationError = null,
                                endReached = movies.size < 20
                            )
                        }
                    )
                }
            }
            is AppResult.Error -> {
                updateState {
                    copy(
                        contentState = SearchContentState.Error(
                            result.message ?: "Something went wrong. Please try again."
                        )
                    )
                }
            }
        }
    }

    private fun loadNextPage() {
        val currentContent = currentState.contentState
        if (currentContent !is SearchContentState.Success) return
        if (currentContent.isLoadingNextPage || currentContent.endReached) return

        val query = currentState.query.trim()
        if (query.isBlank()) return

        val nextPage = currentState.currentPage + 1

        paginationJob?.cancel()
        paginationJob = viewModelScope.launch {
            updateState {
                copy(
                    contentState = currentContent.copy(
                        isLoadingNextPage = true,
                        paginationError = null
                    )
                )
            }

            when (val result = searchMoviesUseCase(query = query, page = nextPage)) {
                is AppResult.Success -> {
                    val newMovies = result.data
                    val combinedMovies = (currentContent.movies + newMovies).distinctBy { it.id }
                    updateState {
                        copy(
                            currentPage = nextPage,
                            contentState = currentContent.copy(
                                movies = combinedMovies,
                                isLoadingNextPage = false,
                                paginationError = null,
                                endReached = newMovies.isEmpty()
                            )
                        )
                    }
                }
                is AppResult.Error -> {
                    updateState {
                        copy(
                            contentState = currentContent.copy(
                                isLoadingNextPage = false,
                                paginationError = result.message ?: "Failed to load more results."
                            )
                        )
                    }
                }
            }
        }
    }
}