package com.fimly.app.presentation.component

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import com.fimly.app.R
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.fimly.app.domain.model.Movie
import com.fimly.app.presentation.theme.PrimarySoft
import com.fimly.app.presentation.theme.RatingBadgeBg
import com.fimly.app.presentation.theme.SecondaryOrange

@Composable
fun FimlyMovieCard(
    movie: Movie,
    onMovieClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    genreName: String = "Action"
) {
    Column(
        modifier = modifier
            .width(135.dp)
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable { onMovieClick(movie.id) }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(178.dp)
        ) {
            AsyncImage(
                model = movie.fullPosterUrl,
                contentDescription = movie.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )

            RatingBadge(
                rating = movie.formattedRating,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(horizontal = 8.dp, vertical = 10.dp)
        ) {
            Text(
                text = movie.title,
                style = MaterialTheme.typography.labelLarge, // 14sp SemiBold
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${movie.releaseYear} • $genreName",
                style = MaterialTheme.typography.labelSmall, // 10sp Medium
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun RatingBadge(
    rating: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(MaterialTheme.shapes.extraSmall)
            .background(RatingBadgeBg)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            painter  = painterResource(R.drawable.star),
            contentDescription = "Rating",
            tint = SecondaryOrange,
            modifier = Modifier.size(14.dp)
        )
        Text(
            text = rating,
            style = MaterialTheme.typography.labelMedium,
            color = SecondaryOrange
        )
    }
}

@Composable
fun FimlySectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    onSeeAllClick: (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium, // 16sp SemiBold
            color = MaterialTheme.colorScheme.onBackground
        )

        if (onSeeAllClick != null) {
            Text(
                text = "See All",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .clickable { onSeeAllClick() }
                    .padding(4.dp)
            )
        }
    }
}

@Composable
fun FimlyMovieRowSection(
    title: String,
    movies: List<Movie>,
    onMovieClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    onSeeAllClick: (() -> Unit)? = null
) {
    Column(modifier = modifier.fillMaxWidth()) {
        FimlySectionHeader(
            title = title,
            onSeeAllClick = onSeeAllClick
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                items = movies,
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
