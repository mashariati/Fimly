package com.fimly.app.presentation.feature.home

import androidx.lifecycle.viewModelScope
import com.fimly.app.domain.model.Movie
import com.fimly.app.domain.model.MovieSortBy
import com.fimly.app.domain.usecase.DiscoverMoviesUseCase
import com.fimly.app.domain.usecase.GetMovieGenresUseCase
import com.fimly.app.domain.util.AppResult
import com.fimly.app.domain.util.DataError
import com.fimly.app.presentation.core.BaseViewModel
import com.fimly.app.presentation.core.ContentState
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class HomeViewModel(
    private val discoverMoviesUseCase: DiscoverMoviesUseCase,
    private val getMovieGenresUseCase: GetMovieGenresUseCase
) : BaseViewModel<HomeState, HomeIntent, HomeEffect>(HomeState()) {

    private companion object {
        const val FIRST_PAGE = 1
        const val BANNER_ITEMS_LIMIT = 4
        const val CURRENT_YEAR = 2026
        const val UPCOMING_YEAR = 2027
        const val MIN_TOP_RATED_SCORE = 7.5
    }

    private var popularJob: Job? = null
    private var nowPlayingJob: Job? = null
    private var topRatedJob: Job? = null
    private var upcomingJob: Job? = null

    init {
        loadInitialHomeData()
    }

    override fun handleIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.SelectGenre -> handleGenreSelection(intent.genreId)
            is HomeIntent.MovieClicked -> sendEffect { HomeEffect.NavigateToDetail(intent.movieId) }
            is HomeIntent.RetrySection -> retrySection(intent.section)
        }
    }

    private fun loadInitialHomeData() {
        loadGenres()
        loadBannerMovies()
        loadMovieRowsForGenre(genreId = currentState.selectedGenreId)
    }

    private fun handleGenreSelection(genreId: Int?) {
        if (currentState.selectedGenreId == genreId) return

        updateState { copy(selectedGenreId = genreId) }
        loadMovieRowsForGenre(genreId = genreId)
    }

    private fun loadMovieRowsForGenre(genreId: Int?) {
        loadMostPopularMovies(genreId)
        loadNowPlayingMovies(genreId)
        loadTopRatedMovies(genreId)
        loadUpcomingMovies(genreId)
    }

    private fun retrySection(section: HomeSection) {
        val activeGenreId = currentState.selectedGenreId
        when (section) {
            HomeSection.BANNER -> loadBannerMovies()
            HomeSection.GENRES -> loadGenres()
            HomeSection.MOST_POPULAR -> loadMostPopularMovies(activeGenreId)
            HomeSection.NOW_PLAYING -> loadNowPlayingMovies(activeGenreId)
            HomeSection.TOP_RATED -> loadTopRatedMovies(activeGenreId)
            HomeSection.UPCOMING -> loadUpcomingMovies(activeGenreId)
        }
    }

    private fun loadGenres() {
        viewModelScope.launch {
            updateState { copy(genresState = ContentState.Loading) }

            when (val result = getMovieGenresUseCase()) {
                is AppResult.Success -> {
                    val genres = result.data
                    updateState {
                        copy(
                            genresState = if (genres.isEmpty()) {
                                ContentState.Empty
                            } else {
                                ContentState.Success(genres)
                            }
                        )
                    }
                }
                is AppResult.Error -> {
                    updateState {
                        copy(genresState = ContentState.Error(result.message?:"Something went wrong. Please try again."))
                    }
                }
            }
        }
    }

    private fun loadBannerMovies() {
        viewModelScope.launch {
            updateState { copy(bannerState = ContentState.Loading) }

            val result = discoverMoviesUseCase(
                page = FIRST_PAGE,
                sortBy = MovieSortBy.POPULARITY_DESC
            )

            updateState {
                copy(
                    bannerState = result.toSectionContentState { movies ->
                        movies.take(BANNER_ITEMS_LIMIT)
                    }
                )
            }
        }
    }

    private fun loadMostPopularMovies(genreId: Int?) {
        popularJob?.cancel()
        popularJob = viewModelScope.launch {
            updateState { copy(mostPopularState = ContentState.Loading) }

            val result = discoverMoviesUseCase(
                page = FIRST_PAGE,
                sortBy = MovieSortBy.POPULARITY_DESC,
                withGenres = genreId?.toString()
            )

            updateState { copy(mostPopularState = result.toSectionContentState()) }
        }
    }

    private fun loadNowPlayingMovies(genreId: Int?) {
        nowPlayingJob?.cancel()
        nowPlayingJob = viewModelScope.launch {
            updateState { copy(nowPlayingState = ContentState.Loading) }

            val result = discoverMoviesUseCase(
                page = FIRST_PAGE,
                sortBy = MovieSortBy.PRIMARY_RELEASE_DATE_DESC,
                withGenres = genreId?.toString(),
                primaryReleaseYear = CURRENT_YEAR
            )

            updateState { copy(nowPlayingState = result.toSectionContentState()) }
        }
    }

    private fun loadTopRatedMovies(genreId: Int?) {
        topRatedJob?.cancel()
        topRatedJob = viewModelScope.launch {
            updateState { copy(topRatedState = ContentState.Loading) }

            val result = discoverMoviesUseCase(
                page = FIRST_PAGE,
                sortBy = MovieSortBy.VOTE_AVERAGE_DESC,
                withGenres = genreId?.toString(),
                voteAverageGte = MIN_TOP_RATED_SCORE
            )

            updateState { copy(topRatedState = result.toSectionContentState()) }
        }
    }

    private fun loadUpcomingMovies(genreId: Int?) {
        upcomingJob?.cancel()
        upcomingJob = viewModelScope.launch {
            updateState { copy(upcomingState = ContentState.Loading) }

            val result = discoverMoviesUseCase(
                page = FIRST_PAGE,
                sortBy = MovieSortBy.POPULARITY_DESC,
                withGenres = genreId?.toString(),
                primaryReleaseYear = UPCOMING_YEAR
            )

            updateState { copy(upcomingState = result.toSectionContentState()) }
        }
    }

    private fun AppResult<List<Movie>, DataError>.toSectionContentState(
        transform: (List<Movie>) -> List<Movie> = { it }
    ): ContentState<List<Movie>> {
        return when (this) {
            is AppResult.Success -> {
                val list = transform(data)
                if (list.isEmpty()) ContentState.Empty else ContentState.Success(list)
            }
            is AppResult.Error -> ContentState.Error(message?:"Something went wrong. Please try again.")
        }
    }

}