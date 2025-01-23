package com.example.moviediscover.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.moviediscover.AddToWatchlistButton
import com.example.moviediscover.HeaderWithSeeMoreButton
import com.example.moviediscover.MovieListType
import com.example.moviediscover.Poster
import com.example.moviediscover.Title
import com.example.moviediscover.Vote
import com.example.moviediscover.data.Movie
import com.example.moviediscover.data.getSampleMovie
import com.example.moviediscover.data.getSampleMovieList
import com.example.moviediscover.ui.theme.MovieDiscoverTheme

@Composable
fun HomeScreen(
    nowPlayingMovieList: List<Movie>?,
    topRatedMovieList: List<Movie>?,
    upcomingMovieList: List<Movie>?,
    onNavigateToMovieListScreen: (MovieListType) -> Unit,
    onMovieClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    MovieDiscoverTheme {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentHeight(),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
        ) {
            item { Spacer(modifier.statusBarsPadding()) }
            item {
                HeaderWithSeeMoreButton(
                    header = MovieListType.NOW_PLAYING.header,
                    onSeeMoreClick = { onNavigateToMovieListScreen(MovieListType.NOW_PLAYING) },
                    modifier = modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
                )
                if (nowPlayingMovieList != null) {
                    LandscapeMovieCardRow(nowPlayingMovieList, onMovieClick)
                }
            }
            item {
                HeaderWithSeeMoreButton(
                    header = MovieListType.TOP_RATED.header,
                    onSeeMoreClick = { onNavigateToMovieListScreen(MovieListType.TOP_RATED) },
                    modifier = modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
                )
                if (topRatedMovieList != null) {
                    LandscapeMovieCardRow(topRatedMovieList, onMovieClick)
                }
            }
            item {
                HeaderWithSeeMoreButton(
                    header = MovieListType.UPCOMING.header,
                    onSeeMoreClick = { onNavigateToMovieListScreen(MovieListType.UPCOMING) },
                    modifier = modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
                )
                if (upcomingMovieList != null) {
                    LandscapeMovieCardRow(upcomingMovieList, onMovieClick)
                }
            }
        }
    }
}


@Composable
fun LandscapeMovieCardRow(
    movieList: List<Movie>,
    onMovieClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        modifier = Modifier
    ) {
        items(movieList) { item ->
            LandscapeMovieCard(item, onMovieClick)
        }
    }
}

@Composable
fun LandscapeMovieCard(
    movie: Movie,
    onMovieClick: (Int) -> Unit,
    onAddToWatchlistClicked: (Movie) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Surface(modifier = Modifier.clickable { onMovieClick(movie.id) }) {
        Column {
            Poster(
                posterPath = movie.posterPath,
                modifier = Modifier.size(width = 240.dp, height = 160.dp)
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
                AddToWatchlistButton { onAddToWatchlistClicked }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    MovieDiscoverTheme {
        HomeScreen(getSampleMovieList(), getSampleMovieList(), getSampleMovieList(), {}, {})
    }
}

@Preview(showBackground = true)
@Composable
fun LandscapeMovieCardRowPreview() {
    MovieDiscoverTheme {
        LandscapeMovieCardRow(getSampleMovieList(), {})
    }
}

@Preview(showBackground = true)
@Composable
fun LandscapeMovieCardPreview() {
    MovieDiscoverTheme {
        LandscapeMovieCard(getSampleMovie(), {})
    }
}