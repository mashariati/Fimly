package com.fimly.app.presentation.feature.detail

import androidx.annotation.DrawableRes
import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.fimly.app.R
import com.fimly.app.domain.model.Genre
import com.fimly.app.domain.model.MovieDetail
import com.fimly.app.domain.model.ProductionCompany
import com.fimly.app.presentation.component.FimlyEmptyState
import com.fimly.app.presentation.component.FimlyErrorState
import com.fimly.app.presentation.component.shimmerEffect
import com.fimly.app.presentation.core.ContentState
import com.fimly.app.presentation.theme.FimlyTheme
import com.fimly.app.presentation.theme.SecondaryOrange
import com.fimly.app.presentation.theme.SecondaryRed
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun DetailScreen(
    movieId: Int,
    onNavigateBack: () -> Unit,
    viewModel: DetailViewModel = koinViewModel(parameters = { parametersOf(movieId) })
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is DetailEffect.NavigateBack -> onNavigateBack()
                is DetailEffect.ShowSnackBar -> snackBarHostState.showSnackbar(effect.message)
            }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) }
    ) { innerPadding ->
        DetailContent(
            state = uiState,
            onIntent = viewModel::onIntent,
            modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
        )
    }
}

@Composable
fun DetailContent(
    state: DetailState,
    onIntent: (DetailIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        when (val content = state.contentState) {
            is ContentState.Loading -> {
                DetailSkeletonLoading(
                    onBackClick = { onIntent(DetailIntent.BackClicked) }
                )
            }

            is ContentState.Error -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    DetailTopBar(
                        title = "",
                        isFavorite = false,
                        showFavoriteButton = false,
                        onBackClick = { onIntent(DetailIntent.BackClicked) },
                        onFavoriteClick = {}
                    )
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        FimlyErrorState(
                            message = content.message,
                            onRetryClick = { onIntent(DetailIntent.RetryClicked) }
                        )
                    }
                }
            }

            is ContentState.Empty -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    DetailTopBar(
                        title = "",
                        isFavorite = false,
                        showFavoriteButton = false,
                        onBackClick = { onIntent(DetailIntent.BackClicked) },
                        onFavoriteClick = {}
                    )
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        FimlyEmptyState(
                            title = "Movie Not Found",
                            subtitle = "The requested movie details are unavailable."
                        )
                    }
                }
            }

            is ContentState.Success -> {
                DetailSuccessView(
                    movie = content.data,
                    onBackClick = { onIntent(DetailIntent.BackClicked) },
                    onFavoriteClick = { onIntent(DetailIntent.ToggleFavoriteClicked) }
                )
            }
        }
    }
}

@Composable
private fun DetailSuccessView(
    movie: MovieDetail,
    onBackClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    val scrollState = rememberScrollState()
    val backgroundColor = MaterialTheme.colorScheme.background

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. Faded Backdrop Image at the top (Adapts cleanly to both Dark & Light theme backgrounds)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(520.dp)
        ) {
            AsyncImage(
                model = movie.fullBackdropUrl ?: movie.fullPosterUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(0.28f)
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                backgroundColor.copy(alpha = 0.3f),
                                backgroundColor.copy(alpha = 0.85f),
                                backgroundColor
                            )
                        )
                    )
            )
        }

        // 2. Scrollable Foreground Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Navigation Bar (Back + Title + Favorite Heart)
            DetailTopBar(
                title = movie.title,
                isFavorite = movie.isFavorite,
                showFavoriteButton = true,
                onBackClick = onBackClick,
                onFavoriteClick = onFavoriteClick
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Center Movie Poster (205dp x 287dp, Shape Small = 12dp)
            Surface(
                modifier = Modifier
                    .width(205.dp)
                    .height(287.dp),
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.surfaceVariant,
                shadowElevation = 12.dp
            ) {
                AsyncImage(
                    model = movie.fullPosterUrl,
                    contentDescription = movie.title,
                    contentScale = ContentScale.Crop,
                    error = painterResource(R.drawable.film),
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Metadata Row: Year | Runtime | Primary Genre
            MovieMetadataRow(
                releaseYear = movie.releaseYear,
                runtime = movie.formattedRuntime,
                primaryGenre = movie.primaryGenre
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Rating Pill Badge
            DetailRatingBadge(
                rating = movie.formattedRating,
                voteCount = movie.voteCount
            )

            // Tagline (if available)
            if (movie.tagline.isNotBlank()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "\"${movie.tagline}\"",
                    style = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic),
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )
            }

            // Genre Chips Row
            if (movie.genres.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                GenreChipsRow(genres = movie.genres)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Story Line Section (Expandable More/Less)
            StoryLineSection(
                overview = movie.overview,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Additional TMDB Details Grid (Status, Language, Budget, Revenue)
            MovieExtraInfoSection(
                status = movie.status,
                language = movie.originalLanguage,
                budget = movie.formattedBudget,
                revenue = movie.formattedRevenue,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            // Production Companies Section
            if (movie.productionCompanies.isNotEmpty()) {
                Spacer(modifier = Modifier.height(24.dp))
                ProductionCompaniesSection(companies = movie.productionCompanies)
            }
        }
    }
}

@Composable
private fun DetailTopBar(
    title: String,
    isFavorite: Boolean,
    showFavoriteButton: Boolean,
    onBackClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Back Button (36dp rounded container)
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(MaterialTheme.shapes.small)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f))
                .clickable {
                    if (lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                        onBackClick()
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.arrow_back),
                contentDescription = "Back",
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(20.dp)
            )
        }

        // Movie Title
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
        )

        // Favorite Heart Button
        if (showFavoriteButton) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(MaterialTheme.shapes.small)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f))
                    .clickable { onFavoriteClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(
                        id = if (isFavorite) R.drawable.heart else R.drawable.heart
                    ),
                    contentDescription = "Toggle Favorite",
                    tint = if (isFavorite) SecondaryRed else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }
        } else {
            Spacer(modifier = Modifier.size(36.dp))
        }
    }
}

@Composable
private fun MovieMetadataRow(
    releaseYear: String,
    runtime: String,
    primaryGenre: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        MetadataItem(iconRes = R.drawable.calendar, text = releaseYear)
        MetadataDivider()
        MetadataItem(iconRes = R.drawable.clock, text = runtime)
        MetadataDivider()
        MetadataItem(iconRes = R.drawable.film, text = primaryGenre)
    }
}

@Composable
private fun MetadataItem(
    @DrawableRes iconRes: Int,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun MetadataDivider() {
    Box(
        modifier = Modifier
            .padding(horizontal = 12.dp)
            .width(1.dp)
            .height(16.dp)
            .background(MaterialTheme.colorScheme.outline)
    )
}

@Composable
private fun DetailRatingBadge(
    rating: String,
    voteCount: Int
) {
    Row(
        modifier = Modifier
            .clip(MaterialTheme.shapes.extraSmall)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.star),
            contentDescription = "Rating",
            tint = SecondaryOrange,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = rating,
            style = MaterialTheme.typography.labelLarge,
            color = SecondaryOrange
        )
        if (voteCount > 0) {
            Text(
                text = "($voteCount)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun GenreChipsRow(genres: List<Genre>) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(genres, key = { it.id }) { genre ->
            Surface(
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Text(
                    text = genre.name,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun StoryLineSection(
    overview: String,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    val safeOverview = overview.ifBlank { "No storyline description available for this movie." }
    val shouldShowMore = safeOverview.length > 140

    Column(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        Text(
            text = "Story Line",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (shouldShowMore && !isExpanded) {
            val truncatedText = safeOverview.take(140).trimEnd() + "... "
            val annotatedString = buildAnnotatedString {
                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onSurfaceVariant)) {
                    append(truncatedText)
                }
                withStyle(
                    style = SpanStyle(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                ) {
                    append("More")
                }
            }
            Text(
                text = annotatedString,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.clickable { isExpanded = true }
            )
        } else {
            val annotatedString = buildAnnotatedString {
                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onSurfaceVariant)) {
                    append(safeOverview)
                }
                if (shouldShowMore) {
                    withStyle(
                        style = SpanStyle(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    ) {
                        append(" Less")
                    }
                }
            }
            Text(
                text = annotatedString,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.clickable(enabled = shouldShowMore) { isExpanded = false }
            )
        }
    }
}

@Composable
private fun MovieExtraInfoSection(
    status: String,
    language: String,
    budget: String,
    revenue: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Movie Info",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            InfoMetricCard(
                label = "Status",
                value = status.ifBlank { "N/A" },
                modifier = Modifier.weight(1f)
            )
            InfoMetricCard(
                label = "Language",
                value = language.uppercase().ifBlank { "N/A" },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            InfoMetricCard(
                label = "Budget",
                value = budget,
                modifier = Modifier.weight(1f)
            )
            InfoMetricCard(
                label = "Box Office",
                value = revenue,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun InfoMetricCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun ProductionCompaniesSection(
    companies: List<ProductionCompany>
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Production Companies",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(companies, key = { it.id }) { company ->
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.9f))
                                .padding(6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (company.fullLogoUrl != null) {
                                AsyncImage(
                                    model = company.fullLogoUrl,
                                    contentDescription = company.name,
                                    contentScale = ContentScale.Fit,
                                    error = painterResource(R.drawable.film),
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Text(
                                    text = company.name.take(1).uppercase(),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.Black
                                )
                            }
                        }

                        Column {
                            Text(
                                text = company.name,
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onBackground,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (company.originCountry.isNotBlank()) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = company.originCountry,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

//  Shimmer Skeleton Loading matching the Detail Screen layout.
@Composable
private fun DetailSkeletonLoading(onBackClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DetailTopBar(
            title = "",
            isFavorite = false,
            showFavoriteButton = false,
            onBackClick = onBackClick,
            onFavoriteClick = {}
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Poster Skeleton
        Box(
            modifier = Modifier
                .width(205.dp)
                .height(287.dp)
                .clip(MaterialTheme.shapes.small)
                .shimmerEffect()
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Metadata Row Skeleton
        Box(
            modifier = Modifier
                .width(240.dp)
                .height(20.dp)
                .clip(MaterialTheme.shapes.extraSmall)
                .shimmerEffect()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Rating Skeleton
        Box(
            modifier = Modifier
                .width(80.dp)
                .height(28.dp)
                .clip(MaterialTheme.shapes.extraSmall)
                .shimmerEffect()
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Storyline Skeleton
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(100.dp)
                    .height(20.dp)
                    .clip(MaterialTheme.shapes.extraSmall)
                    .shimmerEffect()
            )
            Spacer(modifier = Modifier.height(4.dp))
            repeat(3) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(16.dp)
                        .clip(MaterialTheme.shapes.extraSmall)
                        .shimmerEffect()
                )
            }
        }
    }
}


private val previewMovieDetail = MovieDetail(
    id = 11,
    title = "Star Wars",
    originalTitle = "Star Wars",
    tagline = "A long time ago in a galaxy far, far away...",
    overview = "Princess Leia is captured and held hostage by the evil Imperial forces in their effort to take over the galactic Empire.",
    posterPath = "/6FfCtAuVAW8XJjZ7eWeLibRLWTw.jpg",
    backdropPath = "/2w4xG178RpB4MDAIfTkqAuSJzec.jpg",
    releaseDate = "1977-05-25",
    runtime = 121,
    voteAverage = 8.2,
    voteCount = 22061,
    genres = listOf(Genre(12, "Adventure"), Genre(28, "Action")),
    productionCompanies = listOf(
        ProductionCompany(1, "Lucasfilm Ltd.", "/tlVSws0RvvtPBwViUyOFAO0vcQS.png", "US")
    ),
    status = "Released",
    originalLanguage = "EN",
    budget = 11_000_000,
    revenue = 775_398_007,
    homepage = null,
    isFavorite = true
)

@Preview(showBackground = true, name = "1. Detail - Dark Mode (Success)")
@Composable
private fun DetailDarkPreview() {
    FimlyTheme(darkTheme = true) {
        DetailContent(
            state = DetailState(contentState = ContentState.Success(previewMovieDetail)),
            onIntent = {}
        )
    }
}

@Preview(showBackground = true, name = "2. Detail - Light Mode (Success)")
@Composable
private fun DetailLightPreview() {
    FimlyTheme(darkTheme = false) {
        DetailContent(
            state = DetailState(contentState = ContentState.Success(previewMovieDetail)),
            onIntent = {}
        )
    }
}

@Preview(showBackground = true, name = "3. Detail - Skeleton Loading")
@Composable
private fun DetailLoadingPreview() {
    FimlyTheme(darkTheme = true) {
        DetailContent(
            state = DetailState(contentState = ContentState.Loading),
            onIntent = {}
        )
    }
}

@Preview(showBackground = true, name = "4. Detail - Error State")
@Composable
private fun DetailErrorPreview() {
    FimlyTheme(darkTheme = true) {
        DetailContent(
            state = DetailState(
                contentState = ContentState.Error("Failed to load movie details. Please try again.")
            ),
            onIntent = {}
        )
    }
}