package com.example.moviediscover.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.moviediscover.components.ErrorDialog
import com.example.moviediscover.components.HandleConfigurationChange
import com.example.moviediscover.components.HandleToast
import com.example.moviediscover.components.HeaderWithSeeMoreButton
import com.example.moviediscover.components.IndeterminateCircularIndicator
import com.example.moviediscover.components.LandscapeMovieCard
import com.example.moviediscover.data.Movie
import com.example.moviediscover.data.MovieListType
import com.example.moviediscover.ui.theme.MovieDiscoverTheme
import com.example.moviediscover.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToMovieListScreen: (MovieListType) -> Unit,
    onMovieClick: (Int) -> Unit,
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier
) {
    val movieLists by viewModel.movieLists.collectAsState()
    val toastState by viewModel.toastState.collectAsState()
    val scrollState by viewModel.movieListsScrollState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    val nowPlayingMovieList = movieLists[MovieListType.NOW_PLAYING]
    val popularMovieList = movieLists[MovieListType.POPULAR]
    val upcomingMovieList = movieLists[MovieListType.UPCOMING]
    val nowPlayingMovieListState = remember {
        LazyListState(
            scrollState[MovieListType.NOW_PLAYING]!!.first,
            scrollState[MovieListType.NOW_PLAYING]!!.second
        )
    }
    val popularMovieListState = remember {
        LazyListState(
            scrollState[MovieListType.POPULAR]!!.first,
            scrollState[MovieListType.POPULAR]!!.second
        )
    }
    val upcomingMovieListState = remember {
        LazyListState(
            scrollState[MovieListType.UPCOMING]!!.first,
            scrollState[MovieListType.UPCOMING]!!.second
        )
    }
    MovieDiscoverTheme {
        if (isLoading) {
            Box(Modifier.fillMaxSize()) {
                IndeterminateCircularIndicator(modifier.align(Alignment.Center))
            }
        } else {
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .wrapContentHeight(),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                contentPadding = paddingValues,
            ) {
                if (!nowPlayingMovieList.isNullOrEmpty()) {
                    item {
                        HeaderWithSeeMoreButton(
                            header = MovieListType.NOW_PLAYING.displayName,
                            onSeeMoreClick = { onNavigateToMovieListScreen(MovieListType.NOW_PLAYING) },
                            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
                        )
                        LandscapeMovieCardRow(
                            listState = nowPlayingMovieListState,
                            movieList = nowPlayingMovieList,
                            onMovieClick = onMovieClick,
                            onAddToWatchlistClicked = { movie ->
                                viewModel.updateBookmark(movie, !movie.bookmark)
                            }
                        )
                    }
                }
                if (!popularMovieList.isNullOrEmpty()) {
                    item {
                        HeaderWithSeeMoreButton(
                            header = MovieListType.POPULAR.displayName,
                            onSeeMoreClick = { onNavigateToMovieListScreen(MovieListType.POPULAR) },
                            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
                        )
                        LandscapeMovieCardRow(
                            listState = popularMovieListState,
                            movieList = popularMovieList,
                            onMovieClick = onMovieClick,
                            onAddToWatchlistClicked = { movie ->
                                viewModel.updateBookmark(movie, !movie.bookmark)
                            }
                        )
                    }
                }
                if (!upcomingMovieList.isNullOrEmpty()) {
                    item {
                        HeaderWithSeeMoreButton(
                            header = MovieListType.UPCOMING.displayName,
                            onSeeMoreClick = { onNavigateToMovieListScreen(MovieListType.UPCOMING) },
                            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
                        )
                        LandscapeMovieCardRow(
                            listState = upcomingMovieListState,
                            movieList = upcomingMovieList,
                            onMovieClick = onMovieClick,
                            onAddToWatchlistClicked = { movie ->
                                viewModel.updateBookmark(movie, !movie.bookmark)
                            }
                        )
                    }
                }
            }
        }
        if (error != null) {
            ErrorDialog(error!!) { viewModel.resetError() }
        }

        HandleToast(toastState) { viewModel.resetToastState() }
        HandleConfigurationChange(nowPlayingMovieListState) { index, offset ->
            viewModel.updateScrollState(MovieListType.NOW_PLAYING, index, offset)
        }
        HandleConfigurationChange(popularMovieListState) { index, offset ->
            viewModel.updateScrollState(MovieListType.POPULAR, index, offset)
        }
        HandleConfigurationChange(upcomingMovieListState) { index, offset ->
            viewModel.updateScrollState(MovieListType.UPCOMING, index, offset)
        }
    }
}

@Composable
fun LandscapeMovieCardRow(
    listState: LazyListState,
    movieList: List<Movie>,
    onMovieClick: (Int) -> Unit,
    onAddToWatchlistClicked: (Movie) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        state = listState,
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        modifier = Modifier
    ) {
        items(movieList) { item ->
            LandscapeMovieCard(item, onMovieClick, onAddToWatchlistClicked)
        }
    }
}
