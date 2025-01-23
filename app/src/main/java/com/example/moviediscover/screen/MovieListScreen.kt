package com.example.moviediscover.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.moviediscover.CategoryChips
import com.example.moviediscover.MovieListType
import com.example.moviediscover.Poster
import com.example.moviediscover.Title
import com.example.moviediscover.Vote
import com.example.moviediscover.data.Movie
import com.example.moviediscover.data.getSampleCategoryList
import com.example.moviediscover.getStyledTitle
import com.example.moviediscover.ui.theme.MovieDiscoverTheme
import com.example.moviediscover.viewmodel.MainViewModel

@Composable
fun MovieListScreen(
    listType: MovieListType = MovieListType.POPULAR,
    viewModel: MainViewModel,
    onMovieClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    viewModel.getMovieList(listType)
    MovieDiscoverTheme {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(150.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = PaddingValues(16.dp),
            modifier = modifier.fillMaxSize(),
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) { Spacer(modifier.statusBarsPadding()) }
            item(span = { GridItemSpan(maxLineSpan) }) {
                Text(
                    text = getStyledTitle(listType.header),
                    style = MaterialTheme.typography.displayLarge,
                )
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                CategoryChips(getSampleCategoryList())
            }
            val movieList = viewModel.movieLists[listType]
            if (movieList != null) {
                items(movieList) { item ->
                    PortraitMovieCard(item, onMovieClick)
                }
            }
        }
    }
}

@Composable
fun PortraitMovieCard(
    movie: Movie,
    onMovieClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(modifier = Modifier.clickable { onMovieClick(movie.id) }) {
        Column {
            Poster(
                posterPath = movie.posterPath,
                modifier = Modifier.size(width = 200.dp, height = 300.dp)
            )
            Title(
                title = movie.title,
                modifier = Modifier
                    .width(200.dp)
                    .padding(vertical = 4.dp)
            )
            Vote(movie.vote)
        }
    }
}