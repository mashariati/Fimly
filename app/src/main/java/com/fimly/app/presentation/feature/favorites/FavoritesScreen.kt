package com.fimly.app.presentation.feature.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.fimly.app.R
import com.fimly.app.domain.model.Movie
import com.fimly.app.presentation.component.FimlyEmptyState
import com.fimly.app.presentation.component.FimlyErrorState
import com.fimly.app.presentation.component.TmdbGenreHelper
import com.fimly.app.presentation.component.shimmerEffect
import com.fimly.app.presentation.core.ContentState
import com.fimly.app.presentation.theme.FimlyTheme
import com.fimly.app.presentation.theme.SecondaryOrange
import com.fimly.app.presentation.theme.SecondaryRed
import org.koin.androidx.compose.koinViewModel

@Composable
fun FavoritesScreen(
    onNavigateToDetail: (Int) -> Unit = {},
    viewModel: FavoritesViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is FavoritesEffect.NavigateToDetail -> onNavigateToDetail(effect.movieId)
                is FavoritesEffect.ShowUndoSnackBar -> {
                    val result = snackBarHostState.showSnackbar(
                        message = effect.message,
                        actionLabel = "Undo",
                        duration = SnackbarDuration.Short
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        viewModel.onIntent(FavoritesIntent.UndoRemoveFavorite(effect.removedMovie))
                    }
                }
            }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) }
    ) { innerPadding ->
        FavoritesContent(
            state = uiState,
            onIntent = viewModel::onIntent,
            modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
        )
    }
}

@Composable
fun FavoritesContent(
    state: FavoritesState,
    onIntent: (FavoritesIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(top = 24.dp)
    ) {
        val currentContent = state.contentState
        if (currentContent is ContentState.Success && currentContent.data.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${currentContent.data.size} Saved Movies",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Surface(
                    shape = MaterialTheme.shapes.extraSmall,
                    color = SecondaryRed.copy(alpha = 0.12f),
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.extraSmall)
                        .clickable { onIntent(FavoritesIntent.ClearAllFavoritesClicked) }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.trash_bin),
                            contentDescription = "Clear All Favorites",
                            tint = SecondaryRed,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Clear All",
                            style = MaterialTheme.typography.labelMedium,
                            color = SecondaryRed
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            when (val content = state.contentState) {
                is ContentState.Loading -> {
                    FavoritesSkeletonLoading()
                }

                is ContentState.Empty -> {
                    FimlyEmptyState(
                        title = "Your Favorites List is Empty",
                        subtitle = "Explore movies and tap the heart icon to save your favorite titles for offline access.",
                        iconRes = R.drawable.heart,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is ContentState.Error -> {
                    FimlyErrorState(
                        message = content.message,
                        onRetryClick = {},
                        iconRes = R.drawable.notification,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is ContentState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            start = 24.dp,
                            end = 24.dp,
                            top = 8.dp,
                            bottom = 24.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(
                            items = content.data,
                            key = { it.id }
                        ) { movie ->
                            FavoriteMovieCard(
                                movie = movie,
                                onCardClick = { onIntent(FavoritesIntent.MovieClicked(movie.id)) },
                                onRemoveClick = { onIntent(FavoritesIntent.RemoveFavoriteClicked(movie)) },
                                modifier = Modifier.animateItem()
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FavoriteMovieCard(
    movie: Movie,
    onCardClick: () -> Unit,
    onRemoveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val imageUrl = movie.fullBackdropUrl ?: movie.fullPosterUrl

    val imageRequest = remember(imageUrl) {
        ImageRequest.Builder(context)
            .data(imageUrl)
            .crossfade(true)
            .diskCacheKey(imageUrl)
            .memoryCacheKey(imageUrl)
            .diskCachePolicy(CachePolicy.ENABLED)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .build()
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium) // 16.dp radius from Shape.kt
            .clickable { onCardClick() },
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = imageRequest,
                contentDescription = movie.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(120.dp)
                    .height(86.dp)
                    .clip(MaterialTheme.shapes.extraSmall)
                    .background(MaterialTheme.colorScheme.background)
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = TmdbGenreHelper.getGenreNames(movie.genreIds, limit = 1),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = movie.releaseYear,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.star),
                                contentDescription = "Rating",
                                tint = SecondaryOrange,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = movie.formattedRating,
                                style = MaterialTheme.typography.labelMedium,
                                color = SecondaryOrange
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .clickable { onRemoveClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.heart),
                            contentDescription = "Remove from Favorites",
                            tint = SecondaryRed,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FavoritesSkeletonLoading() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        repeat(4) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .width(120.dp)
                            .height(86.dp)
                            .clip(MaterialTheme.shapes.extraSmall)
                            .shimmerEffect()
                    )

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .width(60.dp)
                                .height(14.dp)
                                .clip(MaterialTheme.shapes.extraSmall)
                                .shimmerEffect()
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.85f)
                                .height(18.dp)
                                .clip(MaterialTheme.shapes.extraSmall)
                                .shimmerEffect()
                        )
                        Box(
                            modifier = Modifier
                                .width(100.dp)
                                .height(14.dp)
                                .clip(MaterialTheme.shapes.extraSmall)
                                .shimmerEffect()
                        )
                    }
                }
            }
        }
    }
}


private val previewFavoriteMovies = listOf(
    Movie(
        id = 155,
        title = "The Dark Knight",
        originalTitle = "The Dark Knight",
        overview = "Batman raises the stakes in his war on crime.",
        posterPath = "/qJ2tW6WMUDux911r6m7haRef0WH.jpg",
        backdropPath = "/9FE5eD92WfVCiivM9Pq9GVSrlWk.jpg",
        releaseDate = "2008-07-16",
        popularity = 4135.6,
        voteAverage = 8.5,
        voteCount = 37150,
        isAdult = false,
        genreIds = listOf(28, 53),
        isFavorite = true
    )
)

@Preview(showBackground = true, name = "1. Favorites - Dark Mode (Success)")
@Composable
private fun FavoritesDarkPreview() {
    FimlyTheme(darkTheme = true) {
        FavoritesContent(
            state = FavoritesState(contentState = ContentState.Success(previewFavoriteMovies)),
            onIntent = {}
        )
    }
}

@Preview(showBackground = true, name = "2. Favorites - Light Mode (Success)")
@Composable
private fun FavoritesLightPreview() {
    FimlyTheme(darkTheme = false) {
        FavoritesContent(
            state = FavoritesState(contentState = ContentState.Success(previewFavoriteMovies)),
            onIntent = {}
        )
    }
}

@Preview(showBackground = true, name = "3. Favorites - Empty State")
@Composable
private fun FavoritesEmptyPreview() {
    FimlyTheme(darkTheme = true) {
        FavoritesContent(
            state = FavoritesState(contentState = ContentState.Empty),
            onIntent = {}
        )
    }
}

@Preview(showBackground = true, name = "4. Favorites - Skeleton Loading")
@Composable
private fun FavoritesLoadingPreview() {
    FimlyTheme(darkTheme = true) {
        FavoritesContent(
            state = FavoritesState(contentState = ContentState.Loading),
            onIntent = {}
        )
    }
}

@Preview(showBackground = true, name = "5. Favorites - Error State")
@Composable
private fun FavoritesErrorPreview() {
    FimlyTheme(darkTheme = true) {
        FavoritesContent(
            state = FavoritesState(
                contentState = ContentState.Error("Failed to read favorites from local storage.")
            ),
            onIntent = {}
        )
    }
}