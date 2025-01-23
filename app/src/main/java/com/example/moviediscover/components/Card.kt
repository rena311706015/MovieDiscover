package com.example.moviediscover.components

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.moviediscover.data.Movie

@Composable
fun LandscapeMovieCard(
    movie: Movie,
    onMovieClick: (Int) -> Unit,
    onAddToWatchlistClicked: (Movie) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(modifier = Modifier.clickable { onMovieClick(movie.id) }) {
        Column {
            Poster(
                posterPath = movie.posterPath,
                modifier = Modifier
                    .size(width = 240.dp, height = 160.dp)
                    .clip(RoundedCornerShape(15.dp))
            )
            Title(
                title = movie.title,
                maxLines = 1,
                modifier = Modifier
                    .width(240.dp)
                    .padding(top = 8.dp)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.width(240.dp)
            ) {
                Vote(movie.vote)
                Spacer(modifier = Modifier.weight(1f))
                AddToWatchlistButton(
                    onClick = { onAddToWatchlistClicked(movie) },
                    bookmark = movie.bookmark,
                )
            }
        }
    }
}

@Composable
fun DetailMovieCard(
    movie: Movie,
    onMovieClick: (Int) -> Unit,
    onAddToWatchlistClicked: (Movie) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier
        .fillMaxWidth()
        .clickable { onMovieClick(movie.id) }) {
        Row {
            Poster(
                posterPath = movie.posterPath,
                modifier = Modifier
                    .size(width = 182.dp, height = 273.dp)
                    .clip(RoundedCornerShape(15.dp))
            )
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp)
            ) {
                Title(
                    title = movie.title,
                    modifier = Modifier.padding(bottom = 10.dp)
                )
                Vote(movie.vote)
                Overview(movie.overview)
            }
        }
        AddToWatchlistButton(
            onClick = { onAddToWatchlistClicked(movie) },
            bookmark = movie.bookmark,
            modifier = Modifier.align(Alignment.BottomEnd)
        )
    }
}

@Composable
fun Poster(posterPath: String?, modifier: Modifier = Modifier) {
    if (posterPath.isNullOrEmpty()) {
        Box {
            Image(
                painter = ColorPainter(MaterialTheme.colorScheme.primary),
                contentDescription = "",
                modifier = modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(15.dp))
            )
            Text(
                text = "No Image",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    } else {
        AsyncImage(
            model = ImageRequest.Builder(context = LocalContext.current)
                .data(posterPath)
                .crossfade(true)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            error = ColorPainter(MaterialTheme.colorScheme.primary),
            fallback = ColorPainter(MaterialTheme.colorScheme.primary),
            placeholder = ColorPainter(MaterialTheme.colorScheme.primary),
            modifier = modifier
        )
    }
}

@Composable
fun Title(title: String, maxLines: Int = 2, modifier: Modifier = Modifier) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.onSurface,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier
    )
}

@SuppressLint("DefaultLocale")
@Composable
fun Vote(vote: Double, modifier: Modifier = Modifier) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = String.format("%.2f", vote),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Icon(
            imageVector = Icons.Default.Star,
            tint = MaterialTheme.colorScheme.primary,
            contentDescription = null,
            modifier = modifier
                .padding(horizontal = 4.dp)
                .size(18.dp)
        )
    }
}

@Composable
fun Overview(overview: String, modifier: Modifier = Modifier) {
    Text(
        modifier = modifier,
        text = overview,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurface,
        maxLines = 5,
        overflow = TextOverflow.Ellipsis,
    )
}

@Composable
fun AddToWatchlistButton(
    onClick: () -> Unit,
    bookmark: Boolean,
    modifier: Modifier = Modifier,
) {
    TextButton(
        onClick = onClick,
        modifier = modifier
    ) { AddToWatchlistButtonText(bookmark, MaterialTheme.typography.labelMedium) }
}

@Composable
fun AddToWatchlistButtonText(bookmark: Boolean, style: TextStyle) =
    if (bookmark) {
        Text(
            text = "- Remove from Watchlist",
            color = MaterialTheme.colorScheme.secondary,
            style = style
        )
    } else {
        Text(
            text = "+ Add to Watchlist",
            style = style
        )
    }

@Composable
fun LoadMoreItem(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxWidth()) {
        IndeterminateCircularIndicator(modifier.size(24.dp).align(Alignment.Center))
    }
}

@Composable
fun ErrorItem(errorMessage: String, modifier: Modifier = Modifier) {
    Box(modifier.fillMaxWidth()) {
        Text(
            text = errorMessage,
            modifier = modifier.align(Alignment.Center)
        )
    }
}