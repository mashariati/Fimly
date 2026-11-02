package com.fimly.app.presentation.feature.home

import android.content.res.Configuration
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.fimly.app.domain.model.Movie
import com.fimly.app.presentation.component.FimlyEmptyState
import com.fimly.app.presentation.component.FimlyErrorState
import com.fimly.app.presentation.component.FimlyMovieRowSection
import com.fimly.app.presentation.component.shimmerEffect
import com.fimly.app.presentation.theme.FimlyTheme
import com.fimly.app.presentation.theme.PrimarySoft

@Composable
fun HomeScreen(
    onNavigateToDetail: (Int) -> Unit = {}
) {
    var uiState by remember { mutableStateOf(dummyHomeSuccessState) }

    HomeContent(
        state = uiState,
        onCategorySelect = { categoryId ->
            uiState = uiState.copy(selectedCategoryId = categoryId)
        },
        onMovieClick = onNavigateToDetail,
        onRetryClick = {
            uiState = dummyHomeSuccessState
        }
    )
}

@Composable
fun HomeContent(
    state: HomeState,
    onCategorySelect: (Int?) -> Unit,
    onMovieClick: (Int) -> Unit,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        when (val content = state.contentState) {
            is HomeContentState.Loading -> {
                HomeSkeletonLoading()
            }

            is HomeContentState.Error -> {
                FimlyErrorState(
                    message = content.message,
                    onRetryClick = onRetryClick,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            is HomeContentState.Empty -> {
                FimlyEmptyState(
                    title = "No Movies Found",
                    subtitle = "We couldn't find any movies for the selected category.",
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            is HomeContentState.Success -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(top = 24.dp, bottom = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // 1. Hero Banner Carousel
                    if (content.bannerMovies.isNotEmpty()) {
                        item(key = "hero_carousel") {
                            HeroBannerCarousel(
                                movies = content.bannerMovies,
                                onMovieClick = onMovieClick,
                            )
                        }
                    }

                    // Categories Filter Strip
                    item(key = "categories_section") {
                        CategoriesSection(
                            categories = state.categories,
                            selectedCategoryId = state.selectedCategoryId,
                            onCategorySelect = onCategorySelect
                        )
                    }

                    // Most Popular Movies Row
                    if (content.mostPopularMovies.isNotEmpty()) {
                        item(key = "most_popular_row") {
                            FimlyMovieRowSection(
                                title = "Most popular",
                                movies = content.mostPopularMovies,
                                onMovieClick = onMovieClick,
//                                onSeeAllClick = {}
                            )
                        }
                    }

                    // Now Playing Movies Row
                    if (content.nowPlayingMovies.isNotEmpty()) {
                        item(key = "now_playing_row") {
                            FimlyMovieRowSection(
                                title = "Now playing",
                                movies = content.nowPlayingMovies,
                                onMovieClick = onMovieClick
                            )
                        }
                    }

                    // Top Rated Movies Row
                    if (content.topRatedMovies.isNotEmpty()) {
                        item(key = "top_rated_row") {
                            FimlyMovieRowSection(
                                title = "Top rated",
                                movies = content.topRatedMovies,
                                onMovieClick = onMovieClick
                            )
                        }
                    }

                    // Upcoming Movies Row
                    if (content.upcomingMovies.isNotEmpty()) {
                        item(key = "upcoming_row") {
                            FimlyMovieRowSection(
                                title = "Upcoming",
                                movies = content.upcomingMovies,
                                onMovieClick = onMovieClick
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HeroBannerCarousel(
    movies: List<Movie>,
    onMovieClick: (Int) -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { movies.size })

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 36.dp),
            pageSpacing = 16.dp,
            modifier = Modifier.fillMaxWidth()
        ) { page ->
            val movie = movies[page]
            HeroBannerItem(
                movie = movie,
                onClick = { onMovieClick(movie.id) }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Animated Page Indicators
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(movies.size) { index ->
                val isSelected = pagerState.currentPage == index
                val width by animateDpAsState(
                    targetValue = if (isSelected) 24.dp else 8.dp,
                    label = "indicator_width"
                )
                val color by animateColorAsState(
                    targetValue = if (isSelected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                    },
                    label = "indicator_color"
                )

                Box(
                    modifier = Modifier
                        .height(8.dp)
                        .width(width)
                        .clip(CircleShape)
                        .background(color)
                )
            }
        }
    }
}

@Composable
private fun HeroBannerItem(
    movie: Movie,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(165.dp)
            .clip(MaterialTheme.shapes.medium)
            .background(PrimarySoft)
            .clickable { onClick() }
    ) {
        AsyncImage(
            model = movie.fullBackdropUrl ?: movie.fullPosterUrl,
            contentDescription = movie.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.75f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            Text(
                text = movie.title,
                style = MaterialTheme.typography.titleMedium, // 16sp SemiBold
                color = Color.White,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Release: ${movie.releaseDate.ifBlank { movie.releaseYear }}",
                style = MaterialTheme.typography.bodySmall, // 12sp Medium
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun CategoriesSection(
    categories: List<MovieCategory>,
    selectedCategoryId: Int?,
    onCategorySelect: (Int?) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Categories",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = categories,
                key = { it.id ?: -1 }
            ) { category ->
                val isSelected = category.id == selectedCategoryId

                Box(
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.extraSmall)
                        .background(if (isSelected) PrimarySoft else Color.Transparent)
                        .clickable { onCategorySelect(category.id) }
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = category.name,
                        style = MaterialTheme.typography.labelMedium,
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeSkeletonLoading() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Banner Skeleton
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 36.dp)
                .height(165.dp)
                .clip(MaterialTheme.shapes.medium)
                .shimmerEffect()
        )

        // Categories Skeleton
        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            Box(
                modifier = Modifier
                    .width(100.dp)
                    .height(20.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .shimmerEffect()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(4) {
                    Box(
                        modifier = Modifier
                            .width(80.dp)
                            .height(34.dp)
                            .clip(MaterialTheme.shapes.extraSmall)
                            .shimmerEffect()
                    )
                }
            }
        }

        // Movie Rows Skeleton
        repeat(2) {
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Box(
                    modifier = Modifier
                        .width(120.dp)
                        .height(20.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .shimmerEffect()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    repeat(3) {
                        Box(
                            modifier = Modifier
                                .width(135.dp)
                                .height(235.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .shimmerEffect()
                        )
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true, name = "1. Home - Success State",)
@Composable
private fun HomeSuccessPreview() {
    FimlyTheme(darkTheme = true) {
        HomeContent(
            state = dummyHomeSuccessState,
            onCategorySelect = {},
            onMovieClick = {},
            onRetryClick = {}
        )
    }
}

@Preview(showBackground = true, name = "2. Home - Skeleton Loading State")
@Composable
private fun HomeLoadingPreview() {
    FimlyTheme(darkTheme = true) {
        HomeContent(
            state = HomeState(contentState = HomeContentState.Loading),
            onCategorySelect = {},
            onMovieClick = {},
            onRetryClick = {}
        )
    }
}

@Preview(showBackground = true, name = "3. Home - Error State")
@Composable
private fun HomeErrorPreview() {
    FimlyTheme(darkTheme = true) {
        HomeContent(
            state = HomeState(
                contentState = HomeContentState.Error("No internet connection. Please check your network settings.")
            ),
            onCategorySelect = {},
            onMovieClick = {},
            onRetryClick = {}
        )
    }
}

@Preview(showBackground = true, name = "4. Home - Empty State")
@Composable
private fun HomeEmptyPreview() {
    FimlyTheme(darkTheme = true) {
        HomeContent(
            state = HomeState(contentState = HomeContentState.Empty),
            onCategorySelect = {},
            onMovieClick = {},
            onRetryClick = {}
        )
    }
}