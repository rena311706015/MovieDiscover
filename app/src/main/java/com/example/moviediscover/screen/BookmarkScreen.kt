package com.example.moviediscover.screen

import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.moviediscover.components.DetailMovieCard
import com.example.moviediscover.components.HandleConfigurationChange
import com.example.moviediscover.components.HandleToast
import com.example.moviediscover.components.Header
import com.example.moviediscover.ui.theme.MovieDiscoverTheme
import com.example.moviediscover.viewmodel.BookmarkViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun BookmarkScreen(
    viewModel: BookmarkViewModel = koinViewModel(),
    onMovieClick: (Int) -> Unit,
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier
) {
    val bookmarkMovieList by viewModel.bookmarkMovieList.collectAsState()
    val toastState by viewModel.toastState.collectAsState()
    val scrollState by viewModel.scrollState.collectAsState()
    val listState = remember { LazyGridState(scrollState.first, scrollState.second) }

    MovieDiscoverTheme {
        LazyVerticalGrid(
            state = listState,
            columns = GridCells.Adaptive(300.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = paddingValues,
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) { Header("Bookmark") }
            items(bookmarkMovieList, key = { it.id }) { item ->
                DetailMovieCard(
                    movie = item,
                    onMovieClick = onMovieClick,
                    onAddToWatchlistClicked = { viewModel.updateBookmark(item, !item.bookmark) },
                    modifier = modifier.animateItem(fadeOutSpec = tween(durationMillis = 300))
                )
            }
        }
    }
    HandleToast(toastState) { viewModel.resetToastState() }
    HandleConfigurationChange(listState) { index, offset ->
        viewModel.updateScrollState(index, offset)
    }
}