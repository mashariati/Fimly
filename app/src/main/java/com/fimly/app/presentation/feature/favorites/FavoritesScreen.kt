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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
import com.fimly.app.presentation.theme.FimlyTheme
import com.fimly.app.presentation.theme.SecondaryOrange
import com.fimly.app.presentation.theme.SecondaryRed

/**
 * Stateful Favorites Screen entry point (Interactive with Dummy Data).
 * Clicking the heart icon removes the movie from the list and transitions to Empty state when all are removed.
 */
@Composable
fun FavoritesScreen(
    onNavigateToDetail: (Int) -> Unit = {}
) {
    var uiState by remember { mutableStateOf(dummyFavoritesSuccessState) }

    FavoritesContent(
        state = uiState,
        onMovieClick = onNavigateToDetail,
        onRemoveFavoriteClick = { movieToRemove ->
            val currentContent = uiState.contentState
            if (currentContent is FavoritesContentState.Success) {
                val updatedList = currentContent.movies.filterNot { it.id == movieToRemove.id }
                uiState = uiState.copy(
                    contentState = if (updatedList.isEmpty()) {
                        FavoritesContentState.Empty
                    } else {
                        FavoritesContentState.Success(updatedList)
                    }
                )
            }
        },
        onRetryClick = {
            uiState = dummyFavoritesSuccessState
        }
    )
}

/**
 * Stateless Favorites Content handling Loading, Empty, Error, and Success states.
 */
@Composable
fun FavoritesContent(
    state: FavoritesState,
    onMovieClick: (Int) -> Unit,
    onRemoveFavoriteClick: (Movie) -> Unit,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        // Top Header Title
        Text(
            text = "Favorites",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        )

        // Dynamic Content Area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            when (val content = state.contentState) {
                is FavoritesContentState.Loading -> {
                    FavoritesSkeletonLoading()
                }

                is FavoritesContentState.Empty -> {
                    FimlyEmptyState(
                        title = "Your Favorites List is Empty",
                        subtitle = "Explore movies and tap the heart icon to save your favorite titles for offline access.",
                        iconRes = R.drawable.heart,//todo:double check it
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is FavoritesContentState.Error -> {
                    FimlyErrorState(
                        message = content.message,
                        onRetryClick = onRetryClick,
                        iconRes = R.drawable.notification,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is FavoritesContentState.Success -> {
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
                            items = content.movies,
                            key = { it.id }
                        ) { movie ->
                            FavoriteMovieCard(
                                movie = movie,
                                onCardClick = { onMovieClick(movie.id) },
                                onRemoveClick = { onRemoveFavoriteClick(movie) },
                                modifier = Modifier.animateItem()
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Horizontal Card matching the Figma Wishlist design (16dp container radius, 12dp internal padding,
 * landscape image on the left, genre/title/metadata/heart on the right) with explicit Coil disk caching.
 */
@Composable
private fun FavoriteMovieCard(
    movie: Movie,
    onCardClick: () -> Unit,
    onRemoveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val imageUrl = movie.fullBackdropUrl ?: movie.fullPosterUrl

    // Explicit Coil caching strategy so favorite images persist offline
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
            .clip(MaterialTheme.shapes.large) // 16.dp radius from Shape.kt
            .clickable { onCardClick() },
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Landscape Thumbnail (120dp x 86dp, 8dp corner radius)
            AsyncImage(
                model = imageRequest,
                contentDescription = movie.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(120.dp)
                    .height(86.dp)
                    .clip(MaterialTheme.shapes.small)
                    .background(MaterialTheme.colorScheme.background)
            )

            // Right Content Column (Genre, Title, Year + Rating + Remove Heart)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Primary Genre Caption
                Text(
                    text = TmdbGenreHelper.getGenreNames(movie.genreIds, limit = 1),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Movie Title (Up to 2 lines per Figma Wishlist card)
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                // Bottom Row: Release Year + Rating Star + Remove Favorite Heart Button
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
                                painter = painterResource(id = R.drawable.heart), // Replace with R.drawable.ic_star
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

                    // Remove from Favorites Action (Red Filled Heart)
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

/**
 * Shimmer Skeleton Loading matching the Favorites card layout.
 */
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
                shape = MaterialTheme.shapes.large,
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
                            .clip(MaterialTheme.shapes.small)
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

// --- Previews (Dark, Light, Empty, Loading, Error) ---

@Preview(showBackground = true, name = "1. Favorites - Dark Mode (Success)")
@Composable
private fun FavoritesDarkPreview() {
    FimlyTheme(darkTheme = true) {
        FavoritesContent(
            state = dummyFavoritesSuccessState,
            onMovieClick = {},
            onRemoveFavoriteClick = {},
            onRetryClick = {}
        )
    }
}

@Preview(showBackground = true, name = "2. Favorites - Light Mode (Success)")
@Composable
private fun FavoritesLightPreview() {
    FimlyTheme(darkTheme = false) {
        FavoritesContent(
            state = dummyFavoritesSuccessState,
            onMovieClick = {},
            onRemoveFavoriteClick = {},
            onRetryClick = {}
        )
    }
}

@Preview(showBackground = true, name = "3. Favorites - Empty State")
@Composable
private fun FavoritesEmptyPreview() {
    FimlyTheme(darkTheme = true) {
        FavoritesContent(
            state = FavoritesState(contentState = FavoritesContentState.Empty),
            onMovieClick = {},
            onRemoveFavoriteClick = {},
            onRetryClick = {}
        )
    }
}

@Preview(showBackground = true, name = "4. Favorites - Skeleton Loading")
@Composable
private fun FavoritesLoadingPreview() {
    FimlyTheme(darkTheme = true) {
        FavoritesContent(
            state = FavoritesState(contentState = FavoritesContentState.Loading),
            onMovieClick = {},
            onRemoveFavoriteClick = {},
            onRetryClick = {}
        )
    }
}

@Preview(showBackground = true, name = "5. Favorites - Error State")
@Composable
private fun FavoritesErrorPreview() {
    FimlyTheme(darkTheme = true) {
        FavoritesContent(
            state = FavoritesState(
                contentState = FavoritesContentState.Error("Failed to read favorites from local storage.")
            ),
            onMovieClick = {},
            onRemoveFavoriteClick = {},
            onRetryClick = {}
        )
    }
}