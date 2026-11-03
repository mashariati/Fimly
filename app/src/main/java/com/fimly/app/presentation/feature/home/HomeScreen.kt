package com.fimly.app.presentation.feature.home

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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.fimly.app.R
import com.fimly.app.domain.model.Genre
import com.fimly.app.domain.model.Movie
import com.fimly.app.presentation.component.FimlyMovieCard
import com.fimly.app.presentation.component.FimlySectionHeader
import com.fimly.app.presentation.component.shimmerEffect
import com.fimly.app.presentation.core.ContentState
import com.fimly.app.presentation.theme.FimlyTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(
    onNavigateToDetail: (Int) -> Unit = {},
    viewModel: HomeViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is HomeEffect.NavigateToDetail -> onNavigateToDetail(effect.movieId)
            }
        }
    }

    HomeContent(
        state = uiState,
        onIntent = viewModel::onIntent
    )
}

@Composable
fun HomeContent(
    state: HomeState,
    onIntent: (HomeIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(top = 24.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item(key = "hero_banner_section") {
            BannerSectionContainer(
                bannerState = state.bannerState,
                onMovieClick = { movieId -> onIntent(HomeIntent.MovieClicked(movieId)) },
                onRetryClick = { onIntent(HomeIntent.RetrySection(HomeSection.BANNER)) }
            )
        }

        item(key = "categories_section") {
            CategoriesSectionContainer(
                genresState = state.genresState,
                selectedGenreId = state.selectedGenreId,
                onGenreSelect = { genreId -> onIntent(HomeIntent.SelectGenre(genreId)) },
                onRetryClick = { onIntent(HomeIntent.RetrySection(HomeSection.GENRES)) }
            )
        }

        item(key = "most_popular_section") {
            MovieRowSectionContainer(
                title = "Most popular",
                sectionState = state.mostPopularState,
                onMovieClick = { movieId -> onIntent(HomeIntent.MovieClicked(movieId)) },
                onRetryClick = { onIntent(HomeIntent.RetrySection(HomeSection.MOST_POPULAR)) }
            )
        }

        item(key = "now_playing_section") {
            MovieRowSectionContainer(
                title = "Now playing",
                sectionState = state.nowPlayingState,
                onMovieClick = { movieId -> onIntent(HomeIntent.MovieClicked(movieId)) },
                onRetryClick = { onIntent(HomeIntent.RetrySection(HomeSection.NOW_PLAYING)) }
            )
        }

        item(key = "top_rated_section") {
            MovieRowSectionContainer(
                title = "Top rated",
                sectionState = state.topRatedState,
                onMovieClick = { movieId -> onIntent(HomeIntent.MovieClicked(movieId)) },
                onRetryClick = { onIntent(HomeIntent.RetrySection(HomeSection.TOP_RATED)) }
            )
        }

        item(key = "upcoming_section") {
            MovieRowSectionContainer(
                title = "Upcoming",
                sectionState = state.upcomingState,
                onMovieClick = { movieId -> onIntent(HomeIntent.MovieClicked(movieId)) },
                onRetryClick = { onIntent(HomeIntent.RetrySection(HomeSection.UPCOMING)) }
            )
        }
    }
}


@Composable
private fun BannerSectionContainer(
    bannerState: ContentState<List<Movie>>,
    onMovieClick: (Int) -> Unit,
    onRetryClick: () -> Unit
) {
    when (bannerState) {
        is ContentState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 36.dp)
                    .height(165.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .shimmerEffect()
            )
        }

        is ContentState.Error -> {
            SectionInlineErrorCard(
                message = bannerState.message,
                onRetryClick = onRetryClick,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
        }

        is ContentState.Empty -> Unit

        is ContentState.Success -> {
            HeroBannerCarousel(
                movies = bannerState.data,
                onMovieClick = onMovieClick
            )
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
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable { onClick() }
    ) {
        AsyncImage(
            model = movie.fullBackdropUrl ?: movie.fullPosterUrl,
            contentDescription = movie.title,
            contentScale = ContentScale.Crop,
            error = painterResource(R.drawable.film),
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
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Release: ${movie.releaseDate.ifBlank { movie.releaseYear }}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.75f)
            )
        }
    }
}


@Composable
private fun CategoriesSectionContainer(
    genresState: ContentState<List<Genre>>,
    selectedGenreId: Int?,
    onGenreSelect: (Int?) -> Unit,
    onRetryClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Categories",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        when (genresState) {
            is ContentState.Loading -> {
                Row(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    repeat(5) {
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

            is ContentState.Error -> {
                SectionInlineErrorCard(
                    message = genresState.message,
                    onRetryClick = onRetryClick,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }

            is ContentState.Empty -> {
                CategoriesChipRow(
                    genres = emptyList(),
                    selectedGenreId = selectedGenreId,
                    onGenreSelect = onGenreSelect
                )
            }

            is ContentState.Success -> {
                CategoriesChipRow(
                    genres = genresState.data,
                    selectedGenreId = selectedGenreId,
                    onGenreSelect = onGenreSelect
                )
            }
        }
    }
}

@Composable
private fun CategoriesChipRow(
    genres: List<Genre>,
    selectedGenreId: Int?,
    onGenreSelect: (Int?) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item(key = "genre_all") {
            CategoryChipItem(
                name = "All",
                isSelected = selectedGenreId == null,
                onClick = { onGenreSelect(null) }
            )
        }

        items(
            items = genres,
            key = { it.id }
        ) { genre ->
            CategoryChipItem(
                name = genre.name,
                isSelected = genre.id == selectedGenreId,
                onClick = { onGenreSelect(genre.id) }
            )
        }
    }
}

@Composable
private fun CategoryChipItem(
    name: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(MaterialTheme.shapes.extraSmall)
            .background(
                if (isSelected) {
                    MaterialTheme.colorScheme.surfaceVariant
                } else {
                    Color.Transparent
                }
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.labelMedium,
            color = if (isSelected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
    }
}


@Composable
private fun MovieRowSectionContainer(
    title: String,
    sectionState: ContentState<List<Movie>>,
    onMovieClick: (Int) -> Unit,
    onRetryClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        FimlySectionHeader(title = title)

        Spacer(modifier = Modifier.height(16.dp))

        when (sectionState) {
            is ContentState.Loading -> {
                Row(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    repeat(3) {
                        Box(
                            modifier = Modifier
                                .width(135.dp)
                                .height(235.dp)
                                .clip(MaterialTheme.shapes.small)
                                .shimmerEffect()
                        )
                    }
                }
            }

            is ContentState.Error -> {
                SectionInlineErrorCard(
                    message = sectionState.message,
                    onRetryClick = onRetryClick,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }

            is ContentState.Empty -> {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ) {
                    Text(
                        text = "No movies available in this category.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 24.dp, horizontal = 16.dp)
                    )
                }
            }

            is ContentState.Success -> {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = sectionState.data,
                        key = { it.id }
                    ) { movie ->
                        FimlyMovieCard(
                            movie = movie,
                            onMovieClick = onMovieClick
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionInlineErrorCard(
    message: String,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 12.dp)
            )

            TextButton(
                onClick = onRetryClick,
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


private val previewMovies = listOf(
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
        genreIds = listOf(28, 53)
    ),
    Movie(
        id = 969681,
        title = "Spider-Man: Brand New Day",
        originalTitle = "Spider-Man: Brand New Day",
        overview = "Fighting crime full-time as Spider-Man.",
        posterPath = "/bjiS5ipwxb9JFy3XRRN4OAilSeX.jpg",
        backdropPath = "/4tjxYg2AbOzzG9G1YuiT2NzYhlH.jpg",
        releaseDate = "2026-07-29",
        popularity = 390.9,
        voteAverage = 8.4,
        voteCount = 6376,
        isAdult = false,
        genreIds = listOf(878, 28)
    )
)

private val previewGenres = listOf(
    Genre(id = 28, name = "Action"),
    Genre(id = 35, name = "Comedy"),
    Genre(id = 16, name = "Animation")
)

@Preview(showBackground = true, name = "1. Home - Mixed States (Success + Section Error)")
@Composable
private fun HomeMixedPreview() {
    FimlyTheme(darkTheme = true) {
        HomeContent(
            state = HomeState(
                selectedGenreId = null,
                genresState = ContentState.Success(previewGenres),
                bannerState = ContentState.Success(previewMovies),
                mostPopularState = ContentState.Success(previewMovies),
                nowPlayingState = ContentState.Error("Failed to load Now Playing movies."),
                topRatedState = ContentState.Success(previewMovies),
                upcomingState = ContentState.Loading
            ),
            onIntent = {}
        )
    }
}

@Preview(showBackground = true, name = "2. Home - Full Skeleton Loading")
@Composable
private fun HomeFullLoadingPreview() {
    FimlyTheme(darkTheme = true) {
        HomeContent(
            state = HomeState(),
            onIntent = {}
        )
    }
}