package com.fimly.app.presentation.feature.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.fimly.app.R
import com.fimly.app.domain.model.Movie
import com.fimly.app.presentation.component.FimlyEmptyState
import com.fimly.app.presentation.component.FimlyErrorState
import com.fimly.app.presentation.component.RatingBadge
import com.fimly.app.presentation.component.shimmerEffect
import com.fimly.app.presentation.theme.FimlyTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun SearchScreen(
    onNavigateToDetail: (Int) -> Unit = {},
    viewModel: SearchViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is SearchEffect.NavigateToDetail -> onNavigateToDetail(effect.movieId)
            }
        }
    }

    SearchContent(
        state = uiState,
        onIntent = viewModel::onIntent
    )
}

@Composable
fun SearchContent(
    state: SearchState,
    onIntent: (SearchIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(top = 24.dp)
    ) {
        FimlySearchBar(
            query = state.query,
            onQueryChange = { newQuery -> onIntent(SearchIntent.QueryChanged(newQuery)) },
            onCancelClick = {
                focusManager.clearFocus()
                onIntent(SearchIntent.ClearQueryClicked)
            },
            onSearchImeAction = {
                focusManager.clearFocus()
            },
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            when (val content = state.contentState) {
                is SearchContentState.Idle -> {
                    FimlyEmptyState(
                        title = "Search for Movies",
                        subtitle = "Find your favorite movies by typing a title,categories, years, etc",
                        iconRes = R.drawable.search,
                        iconTint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is SearchContentState.Loading -> {
                    SearchSkeletonLoading()
                }

                is SearchContentState.Empty -> {
                    FimlyEmptyState(
                        title = "We Are Sorry, We Can\nNot Find The Movie :(",
                        subtitle = "Find your movie by Type title,\ncategories, years, etc",
                        iconRes = R.drawable.ic_no_results,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is SearchContentState.Error -> {
                    FimlyErrorState(
                        message = content.message,
                        onRetryClick = { onIntent(SearchIntent.RetryClicked) },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is SearchContentState.Success -> {
                    SearchPaginatedMovieList(
                        content = content,
                        onMovieClick = { movieId -> onIntent(SearchIntent.MovieClicked(movieId)) },
                        onLoadNextPage = { onIntent(SearchIntent.LoadNextPage) },
                        onRetryNextPage = { onIntent(SearchIntent.RetryClicked) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchPaginatedMovieList(
    content: SearchContentState.Success,
    onMovieClick: (Int) -> Unit,
    onLoadNextPage: () -> Unit,
    onRetryNextPage: () -> Unit
) {
    val listState = rememberLazyListState()

    val shouldLoadMore by remember(
        content.movies.size,
        content.isLoadingNextPage,
        content.endReached,
        content.paginationError
    ) {
        derivedStateOf {
            if (content.isLoadingNextPage || content.endReached || content.paginationError != null) {
                false
            } else {
                val layoutInfo = listState.layoutInfo
                val totalItemsCount = layoutInfo.totalItemsCount
                val lastVisibleItemIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                totalItemsCount > 0 && lastVisibleItemIndex >= (totalItemsCount - 3)
            }
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) {
            onLoadNextPage()
        }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 24.dp,
            end = 24.dp,
            top = 4.dp,
            bottom = 24.dp
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(
            items = content.movies,
            key = { it.id }
        ) { movie ->
            FimlyMovieListItem(
                movie = movie,
                onMovieClick = onMovieClick
            )
        }

        if (content.isLoadingNextPage) {
            item(key = "pagination_loading") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp),
                        strokeWidth = 2.5.dp
                    )
                }
            }
        }

        if (content.paginationError != null) {
            item(key = "pagination_error") {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = content.paginationError,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 12.dp)
                        )
                        TextButton(
                            onClick = onRetryNextPage,
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text(
                                text = "Retry",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
            }
        }
    }
}

// Custom Search Input Bar
@Composable
private fun FimlySearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onCancelClick: () -> Unit,
    onSearchImeAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Surface(
            modifier = Modifier.weight(1f),
            shape = MaterialTheme.shapes.large, // 24.dp radius from Shape.kt
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onBackground
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { onSearchImeAction() }),
                decorationBox = { innerTextField ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.search),
                            contentDescription = "Search Icon",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )

                        Box(modifier = Modifier.weight(1f)) {
                            if (query.isEmpty()) {
                                Text(
                                    text = "Type title, categories, years, etc",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            innerTextField()
                        }
                    }
                }
            )
        }

        AnimatedVisibility(visible = query.isNotEmpty()) {
            Text(
                text = "Cancel",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .clip(MaterialTheme.shapes.extraSmall)
                    .clickable { onCancelClick() }
                    .padding(horizontal = 4.dp, vertical = 8.dp)
            )
        }
    }
}

@Composable
private fun SearchSkeletonLoading() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        repeat(4) {
            FimlyMovieListItemSkeleton()
        }
    }
}

@Composable
fun FimlyMovieListItem(
    movie: Movie,
    onMovieClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.small)
            .clickable { onMovieClick(movie.id) }
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(112.dp)
                .height(147.dp)
                .clip(MaterialTheme.shapes.extraSmall)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            AsyncImage(
                model = movie.fullPosterUrl ?: movie.fullBackdropUrl,
                contentDescription = movie.title,
                contentScale = ContentScale.Crop,
                error = painterResource(R.drawable.film),
                modifier = Modifier.fillMaxSize()

            )

            RatingBadge(
                rating = movie.formattedRating,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp)
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                shape = MaterialTheme.shapes.extraSmall,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
            ) {
                Text(
                    text = if (movie.voteCount > 0) "${movie.voteCount} Votes" else "TMDB Movie",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }

            Text(
                text = movie.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.calendar),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = movie.releaseYear,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Surface(
                    shape = MaterialTheme.shapes.extraSmall,
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                ) {
                    Text(
                        text = if (movie.isAdult) "18+" else "PG-13",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.film),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = movie.primaryGenresText.take(2).joinToString(" • "),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "|",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
                Text(
                    text = "Movie",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            if (movie.overview.isNotBlank()) {
                Text(
                    text = movie.overview,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun FimlyMovieListItemSkeleton(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(112.dp)
                .height(147.dp)
                .clip(MaterialTheme.shapes.extraSmall)
                .shimmerEffect()
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(70.dp)
                    .height(20.dp)
                    .clip(MaterialTheme.shapes.extraSmall)
                    .shimmerEffect()
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(20.dp)
                    .clip(MaterialTheme.shapes.extraSmall)
                    .shimmerEffect()
            )
            Box(
                modifier = Modifier
                    .width(120.dp)
                    .height(16.dp)
                    .clip(MaterialTheme.shapes.extraSmall)
                    .shimmerEffect()
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(16.dp)
                    .clip(MaterialTheme.shapes.extraSmall)
                    .shimmerEffect()
            )
        }
    }
}

private val previewSearchMovies = listOf(
    Movie(
        id = 550,
        title = "Fight Club",
        originalTitle = "Fight Club",
        overview = "A ticking-time-bomb insomniac and a slippery soap salesman channel primal male aggression.",
        posterPath = "/pB8BM7pdSp6B6Ih7QZ4DrQ3PmJK.jpg",
        backdropPath = "/hZkgoQYus5vegHoetLkCJzb17zJ.jpg",
        releaseDate = "1999-10-15",
        popularity = 73.4,
        voteAverage = 8.4,
        voteCount = 26279,
        isAdult = false,
        genreIds = listOf(18, 53)
    )
)

@Preview(showBackground = true, name = "1. Search Results - Dark Mode")
@Composable
private fun SearchResultsDarkPreview() {
    FimlyTheme(darkTheme = true) {
        SearchContent(
            state = SearchState(
                query = "Fight Club",
                contentState = SearchContentState.Success(
                    movies = previewSearchMovies,
                    isLoadingNextPage = true
                )
            ),
            onIntent = {}
        )
    }
}

@Preview(showBackground = true, name = "2. Search Results - Light Mode")
@Composable
private fun SearchResultsLightPreview() {
    FimlyTheme(darkTheme = false) {
        SearchContent(
            state = SearchState(
                query = "Fight Club",
                contentState = SearchContentState.Success(previewSearchMovies)
            ),
            onIntent = {}
        )
    }
}

@Preview(showBackground = true, name = "3. Search - Blank / Empty State")
@Composable
private fun SearchEmptyPreview() {
    FimlyTheme(darkTheme = true) {
        SearchContent(
            state = SearchState(
                query = "John",
                contentState = SearchContentState.Empty
            ),
            onIntent = {}
        )
    }
}

@Preview(showBackground = true, name = "4. Search - Initial Idle State")
@Composable
private fun SearchIdlePreview() {
    FimlyTheme(darkTheme = true) {
        SearchContent(
            state = SearchState(
                query = "",
                contentState = SearchContentState.Idle
            ),
            onIntent = {}
        )
    }
}

@Preview(showBackground = true, name = "5. Search - Skeleton Loading")
@Composable
private fun SearchLoadingPreview() {
    FimlyTheme(darkTheme = true) {
        SearchContent(
            state = SearchState(
                query = "Spiderman",
                contentState = SearchContentState.Loading
            ),
            onIntent = {}
        )
    }
}

@Preview(showBackground = true, name = "6. Search - Error State")
@Composable
private fun SearchErrorPreview() {
    FimlyTheme(darkTheme = true) {
        SearchContent(
            state = SearchState(
                query = "Spiderman",
                contentState = SearchContentState.Error("Unable to reach server. Please check your connection.")
            ),
            onIntent = {}
        )
    }
}