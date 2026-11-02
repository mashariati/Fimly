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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.fimly.app.R
import com.fimly.app.domain.model.Movie
import com.fimly.app.presentation.component.FimlyEmptyState
import com.fimly.app.presentation.component.FimlyErrorState
import com.fimly.app.presentation.component.RatingBadge
import com.fimly.app.presentation.component.TmdbGenreHelper
import com.fimly.app.presentation.component.shimmerEffect
import com.fimly.app.presentation.feature.home.MovieCategory
import com.fimly.app.presentation.theme.FimlyTheme


/**
 * Stateful Search Screen entry point (Interactive with Dummy Data).
 */
@Composable
fun SearchScreen(
    onNavigateToDetail: (Int) -> Unit = {}
) {
    // Starts with empty query and Idle state by default
    var uiState by remember { mutableStateOf(SearchState()) }

    fun handleDummyQuery(newQuery: String) {
        if (newQuery.isBlank()) {
            uiState = SearchState(query = "", contentState = SearchContentState.Idle)
            return
        }

        val filtered = dummySearchMovies.filter { movie ->
            movie.title.contains(newQuery, ignoreCase = true) ||
                    movie.releaseYear.contains(newQuery, ignoreCase = true)
        }

        uiState = SearchState(
            query = newQuery,
            contentState = if (filtered.isEmpty()) {
                SearchContentState.Empty
            } else {
                SearchContentState.Success(filtered)
            }
        )
    }

    SearchContent(
        state = uiState,
        onQueryChange = { handleDummyQuery(it) },
        onCancelClick = { handleDummyQuery("") },
        onMovieClick = onNavigateToDetail,
        onRetryClick = { handleDummyQuery(uiState.query) }
    )
}

/**
 * Stateless Search Content handling Idle, Loading, Empty, Error, and Success states.
 */
@Composable
fun SearchContent(
    state: SearchState,
    onQueryChange: (String) -> Unit,
    onCancelClick: () -> Unit,
    onMovieClick: (Int) -> Unit,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(top = 24.dp)
    ) {
        // 1. Top Search Bar + "Cancel" Action
        FimlySearchBar(
            query = state.query,
            onQueryChange = onQueryChange,
            onCancelClick = {
                focusManager.clearFocus()
                onCancelClick()
            },
            onSearchImeAction = {
                focusManager.clearFocus()
            },
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))



        // 3. Dynamic State Content Area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            when (val content = state.contentState) {
                is SearchContentState.Idle -> {
                    FimlyEmptyState(
                        title = "Search for Movies",
                        subtitle = "Find your favorite movies by typing a title, release year, or selecting a genre above.",
                        iconRes = R.drawable.search,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is SearchContentState.Loading -> {
                    SearchSkeletonLoading()
                }

                is SearchContentState.Empty -> {
                    // Exact copy from Figma "Search - Blank Page" design
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
                        onRetryClick = onRetryClick,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is SearchContentState.Success -> {
                    LazyColumn(
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
                                onMovieClick = onMovieClick,
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Custom Search Input Bar matching Figma specifications (24dp corner radius, 16dp horizontal / 12dp vertical padding).
 */
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

        // Animated "Cancel" button when query is not empty (Matches Figma Search Result & Blank Page)
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

// --- Previews (Results Dark/Light, Idle, Blank/Empty, Skeleton Loading, Error) ---

@Preview(showBackground = true, name = "1. Search Results - Dark Mode")
@Composable
private fun SearchResultsDarkPreview() {
    FimlyTheme(darkTheme = true) {
        SearchContent(
            state = SearchState(
                query = "Fight Club",
                contentState = SearchContentState.Success(dummySearchMovies)
            ),
            onQueryChange = {},
            onCancelClick = {},
            onMovieClick = {},
            onRetryClick = {}
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
                contentState = SearchContentState.Success(dummySearchMovies)
            ),
            onQueryChange = {},
            onCancelClick = {},
            onMovieClick = {},
            onRetryClick = {}
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
            onQueryChange = {},
            onCancelClick = {},
            onMovieClick = {},
            onRetryClick = {}
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
            onQueryChange = {},
            onCancelClick = {},
            onMovieClick = {},
            onRetryClick = {}
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
            onQueryChange = {},
            onCancelClick = {},
            onMovieClick = {},
            onRetryClick = {}
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
            onQueryChange = {},
            onCancelClick = {},
            onMovieClick = {},
            onRetryClick = {}
        )
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
        // Left Poster with Top-Left Rating Badge (112dp x 147dp, 8dp radius per Figma)
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
                modifier = Modifier.fillMaxSize()
            )

            RatingBadge(
                rating = movie.formattedRating,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp)
            )
        }

        // Right Movie Metadata Column
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Top Accent Tag (Vote count / Popularity indicator)
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

            // Movie Title
            Text(
                text = movie.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Release Year + Age Certification Badge (PG-13 / 18+)
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

            // Genre Row (Action | Movie)
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
                    text = TmdbGenreHelper.getGenreNames(movie.genreIds),
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

            // Short Overview Snippet (if available)
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

/**
 * Reusable Shimmer Skeleton for horizontal movie list items (Search & Favorites).
 */
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