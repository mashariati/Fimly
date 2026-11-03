package com.fimly.app.presentation.feature.detail

import com.fimly.app.domain.model.MovieDetail
import com.fimly.app.presentation.core.ContentState
import com.fimly.app.presentation.core.UiEffect
import com.fimly.app.presentation.core.UiIntent
import com.fimly.app.presentation.core.UiState


data class DetailState(
    val contentState: ContentState<MovieDetail> = ContentState.Loading
) : UiState

sealed interface DetailIntent : UiIntent {
    data object ToggleFavoriteClicked : DetailIntent
    data object RetryClicked : DetailIntent
    data object BackClicked : DetailIntent
}

sealed interface DetailEffect : UiEffect {
    data object NavigateBack : DetailEffect
    data class ShowSnackBar(val message: String) : DetailEffect
}