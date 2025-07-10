package com.example.moviediscover.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.moviediscover.components.BackButton
import com.example.moviediscover.components.DetailMovieCard
import com.example.moviediscover.components.ErrorDialog
import com.example.moviediscover.components.HandleConfigurationChange
import com.example.moviediscover.components.HandleLoadMore
import com.example.moviediscover.components.HandleToast
import com.example.moviediscover.components.LoadMoreItem
import com.example.moviediscover.components.NetworkStatusSnackbar
import com.example.moviediscover.data.MovieListType
import com.example.moviediscover.ui.theme.MovieDiscoverTheme
import com.example.moviediscover.viewmodel.MovieListViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun MovieListScreen(
    listType: MovieListType = MovieListType.POPULAR,
    viewModel: MovieListViewModel = koinViewModel(),
    onMovieClick: (Int) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val movieList by viewModel.movieList.collectAsState()
    val toastState by viewModel.toastState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val scrollState by viewModel.scrollState.collectAsState()
    val error by viewModel.error.collectAsState()

    val listState = remember { LazyGridState(scrollState.first, scrollState.second) }

    LaunchedEffect(Unit) { viewModel.updateMovieListType(listType) }

    MovieDiscoverTheme {
        Box(modifier.fillMaxSize()) {
            LazyVerticalGrid(
                state = listState,
                columns = GridCells.Adaptive(300.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                contentPadding = PaddingValues(16.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface),
            ) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Row(modifier.statusBarsPadding()) {
                        BackButton(onBack)
                        Text(
                            text = listType.displayName,
                            style = MaterialTheme.typography.displayLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = modifier.align(Alignment.CenterVertically)
                        )
                    }
                }
                items(movieList) { item ->
                    DetailMovieCard(
                        movie = item,
                        onMovieClick = onMovieClick,
                        onAddToWatchlistClicked = {
                            viewModel.updateBookmark(
                                movie = item,
                                isAdding = !item.bookmark
                            )
                        },
                    )
                }
                
                //  錯誤 #1: 潛在 NullPointerException
                val riskyText: String? = null
                Log.d("Test", riskyText!!.length.toString())  // 這行會執行，但不會在 UI 中導致錯誤
            
                //  錯誤 #2: 永遠不會執行的死程式碼
                if (true) {
                    // do nothing
                } else {
                    Log.d("DeadCode", "You will never see this!") // CodeQL 可以偵測 unreachable code
                }
            
                //  錯誤 #3: 假的硬編碼敏感字串
                val apiKey = "sk-1234567890-FAKEKEY" // 模擬硬編碼 secret，被 CodeQL 當成風險
                if (isLoading) {
                    item(span = { GridItemSpan(maxLineSpan) }) { LoadMoreItem() }
                }
            }
            if (error != null) {
                ErrorDialog(error!!) { viewModel.resetError() }
            }
            NetworkStatusSnackbar(Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp))
        }
        HandleToast(toastState) { viewModel.resetToastState() }
        HandleLoadMore(listState) { viewModel.getMovieList(listType) }
        HandleConfigurationChange(listState) { index, offset ->
            viewModel.updateScrollState(index, offset)
        }
    }
}
