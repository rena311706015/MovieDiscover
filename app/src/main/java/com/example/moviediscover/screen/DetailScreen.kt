package com.example.moviediscover.screen

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.moviediscover.Overview
import com.example.moviediscover.Title
import com.example.moviediscover.Vote
import com.example.moviediscover.data.Movie
import com.example.moviediscover.viewmodel.DetailViewModel

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun DetailScreen(
    movieId: Int,
    viewModel: DetailViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    viewModel.getMovieDetail(movieId)
    val movie by viewModel.movie.collectAsState()
    val windowSize = LocalActivity.current?.let { calculateWindowSizeClass(it) }
    when (windowSize?.widthSizeClass) {
        WindowWidthSizeClass.Expanded -> {
            DetailScreenLandscape(movie, modifier)
        }

        else -> {
            DetailScreenPortrait(movie, modifier)
        }
    }

}

@Composable
fun DetailScreenPortrait(movie: Movie, modifier: Modifier) {
    Box {
        AsyncImage(
            model = ImageRequest.Builder(context = LocalContext.current)
                .data(movie.posterPath)
                .crossfade(true)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = modifier.fillMaxSize()
        )
        DetailColumn(
            movie,
            modifier
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, MaterialTheme.colorScheme.surface)
                    )
                )
                .padding(16.dp)
                .padding(top = 274.dp)
                .align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun DetailScreenLandscape(movie: Movie, modifier: Modifier) {
    Row {
        DetailColumn(
            movie,
            modifier
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(MaterialTheme.colorScheme.surface, Color.Transparent)
                    )
                )
                .padding(16.dp)
                .width(300.dp)

        )
        Box(modifier = modifier.fillMaxSize()) {
            AsyncImage(
                model = ImageRequest.Builder(context = LocalContext.current)
                    .data(movie.posterPath)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.horizontalGradient(
                            0f to MaterialTheme.colorScheme.surface,
                            0.6f to Color.Transparent
                        )
                    )
            )
        }
    }
}

@Composable
fun DetailColumn(movie: Movie, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
    ) {
        Spacer(Modifier.height(75.dp))
        Title(movie.title, maxLines = 3)
        Vote(movie.vote)
        Text(
            modifier = Modifier
                .padding(bottom = 10.dp),
            text = movie.getCategoryString(),
            style = MaterialTheme.typography.labelSmall
        )
        Overview(movie.overview)
        OutlinedButton(
            onClick = {},
            modifier = Modifier
                .padding(top = 16.dp)
                .height(50.dp)
                .fillMaxWidth(),

            ) {
            Text(
                text = "+Add to Watchlist",
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}