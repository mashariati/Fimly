package com.fimly.app.presentation.feature.detail

import com.fimly.app.domain.model.Genre
import com.fimly.app.domain.model.MovieDetail
import com.fimly.app.domain.model.ProductionCompany
import com.fimly.app.presentation.core.UiEffect
import com.fimly.app.presentation.core.UiIntent
import com.fimly.app.presentation.core.UiState

sealed interface DetailContentState {
    data object Loading : DetailContentState
    data object Empty : DetailContentState
    data class Error(val message: String) : DetailContentState
    data class Success(val movie: MovieDetail) : DetailContentState
}

data class DetailState(
    val contentState: DetailContentState = DetailContentState.Loading
) : UiState

sealed interface DetailIntent : UiIntent {
    data object ToggleFavorite : DetailIntent
    data object RetryClicked : DetailIntent
    data object BackClicked : DetailIntent
}

sealed interface DetailEffect : UiEffect {
    data object NavigateBack : DetailEffect
    data class ShowSnackbar(val message: String) : DetailEffect
}

// --- Dummy Data based on the provided TMDB Movie Detail JSON ---

val dummyMovieDetail = MovieDetail(
    id = 11,
    title = "Star Wars",
    originalTitle = "Star Wars",
    tagline = "A long time ago in a galaxy far, far away...",
    overview = "Princess Leia is captured and held hostage by the evil Imperial forces in their effort to take over the galactic Empire. Venturesome Luke Skywalker and dashing captain Han Solo team together with the loveable robot duo R2-D2 and C-3PO to rescue the beautiful princess and restore peace and justice in the Empire.",
    posterPath = "/6FfCtAuVAW8XJjZ7eWeLibRLWTw.jpg",
    backdropPath = "/2w4xG178RpB4MDAIfTkqAuSJzec.jpg",
    releaseDate = "1977-05-25",
    runtime = 121,
    voteAverage = 8.2,
    voteCount = 22061,
    genres = listOf(
        Genre(id = 12, name = "Adventure"),
        Genre(id = 28, name = "Action"),
        Genre(id = 878, name = "Science Fiction")
    ),
    productionCompanies = listOf(
        ProductionCompany(
            id = 1,
            name = "Lucasfilm Ltd.",
            logoPath = "/tlVSws0RvvtPBwViUyOFAO0vcQS.png",
            originCountry = "US"
        ),
        ProductionCompany(
            id = 25,
            name = "20th Century Fox",
            logoPath = "/qZCc1lty5FzX30aOCVRBLzaVmcp.png",
            originCountry = "US"
        )
    ),
    status = "Released",
    originalLanguage = "EN",
    budget = 11_000_000,
    revenue = 775_398_007,
    homepage = "http://www.starwars.com/films/star-wars-episode-iv-a-new-hope",
    isFavorite = true
)

val dummyDetailSuccessState = DetailState(
    contentState = DetailContentState.Success(movie = dummyMovieDetail)
)