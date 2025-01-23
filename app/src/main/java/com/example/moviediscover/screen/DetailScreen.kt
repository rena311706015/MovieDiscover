package com.example.moviediscover.screen

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.moviediscover.components.AddToWatchlistButtonText
import com.example.moviediscover.components.BackButton
import com.example.moviediscover.components.FloatingBackButton
import com.example.moviediscover.components.HandleToast
import com.example.moviediscover.components.IndeterminateCircularIndicator
import com.example.moviediscover.components.NetworkStatusSnackbar
import com.example.moviediscover.components.Overview
import com.example.moviediscover.components.Poster
import com.example.moviediscover.components.Title
import com.example.moviediscover.components.Vote
import com.example.moviediscover.data.Movie
import com.example.moviediscover.ui.theme.MovieDiscoverTheme
import com.example.moviediscover.viewmodel.DetailViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun DetailScreen(
    movieId: Int,
    viewModel: DetailViewModel = koinViewModel(),
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val movie by viewModel.movie.collectAsState()
    val toastState by viewModel.toastState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(Unit) { viewModel.updateMovieId(movieId) }

    MovieDiscoverTheme {
        Box(Modifier.fillMaxSize()) {
            if (error != null && movie == null) {
                Text(error.toString(), modifier = Modifier.align(Alignment.Center))
            } else if (isLoading) {
                IndeterminateCircularIndicator(modifier.align(Alignment.Center))
            } else {
                val currentMovie = movie
                if (currentMovie != null) {
                    val windowSize = LocalActivity.current?.let { calculateWindowSizeClass(it) }
                    when (windowSize?.widthSizeClass) {
                        WindowWidthSizeClass.Expanded -> {
                            DetailScreenLandscape(
                                movie = currentMovie,
                                onBookmarkClick = {
                                    viewModel.updateBookmark(
                                        currentMovie,
                                        !currentMovie.bookmark
                                    )
                                },
                                onBack = onBack,
                                modifier = modifier
                            )
                        }

                        else -> {
                            DetailScreenPortrait(
                                movie = currentMovie,
                                onBookmarkClick = {
                                    viewModel.updateBookmark(
                                        currentMovie,
                                        !currentMovie.bookmark
                                    )
                                },
                                onBack = onBack,
                                modifier = modifier
                            )
                        }
                    }
                }
            }
            NetworkStatusSnackbar(Modifier.align(Alignment.BottomCenter).padding(bottom = 16.dp))
        }
        HandleToast(toastState) { viewModel.resetToastState() }
    }
}

@Composable
fun DetailScreenPortrait(
    movie: Movie,
    onBookmarkClick: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier
) {
    Box {
        Poster(movie.posterPath, modifier.fillMaxSize())
        DetailColumn(
            movie,
            onBookmarkClick,
            modifier
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, MaterialTheme.colorScheme.surface)
                    )
                )
                .padding(horizontal = 16.dp)
                .padding(top = 274.dp, bottom = 48.dp)
                .align(Alignment.BottomCenter)
        )
        FloatingBackButton(
            onBack = onBack,
            modifier = Modifier
                .statusBarsPadding()
                .align(Alignment.TopStart)
        )
    }
}

@Composable
fun DetailScreenLandscape(
    movie: Movie,
    onBookmarkClick: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier
) {
    Row {
        Column(
            modifier = modifier
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface, if (isSystemInDarkTheme()) {
                                MaterialTheme.colorScheme.surface
                            } else {
                                Color.Transparent
                            }
                        )
                    )
                )
                .padding(24.dp)
                .statusBarsPadding()
                .width(300.dp)
                .fillMaxHeight()
        ) {
            BackButton(onBack)
            DetailColumn(movie, onBookmarkClick, modifier.padding(start = 12.dp))
        }
        Box(modifier = modifier.fillMaxSize()) {
            Poster(movie.posterPath, modifier.fillMaxSize())
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
fun DetailColumn(
    movie: Movie,
    onBookmarkClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Title(movie.title, maxLines = 3)
        Vote(movie.vote)
        Text(
            modifier = Modifier
                .padding(bottom = 10.dp),
            text = movie.genres,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.labelSmall
        )
        Overview(movie.overview)
        OutlinedButton(
            onClick = onBookmarkClick,
            modifier = Modifier
                .padding(top = 16.dp)
                .height(50.dp)
                .fillMaxWidth(),
        ) { AddToWatchlistButtonText(movie.bookmark, MaterialTheme.typography.bodyMedium) }
    }
}