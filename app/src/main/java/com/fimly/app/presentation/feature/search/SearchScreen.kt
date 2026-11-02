package com.fimly.app.presentation.feature.search

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fimly.app.R
import com.fimly.app.presentation.component.FimlyEmptyState
import com.fimly.app.presentation.component.FimlyErrorState
import com.fimly.app.presentation.component.FimlyMovieListItem
import com.fimly.app.presentation.component.FimlyMovieListItemSkeleton
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
            shape = MaterialTheme.shapes.extraLarge, // 24.dp radius from Shape.kt
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
                    .clip(MaterialTheme.shapes.small)
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