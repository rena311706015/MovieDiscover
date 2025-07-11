package com.example.moviediscover.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import com.example.moviediscover.components.CategoryChips
import com.example.moviediscover.components.DetailMovieCard
import com.example.moviediscover.components.ErrorDialog
import com.example.moviediscover.components.ErrorItem
import com.example.moviediscover.components.HandleConfigurationChange
import com.example.moviediscover.components.HandleLoadMore
import com.example.moviediscover.components.HandleToast
import com.example.moviediscover.components.Header
import com.example.moviediscover.components.LoadMoreItem
import com.example.moviediscover.components.NetworkStatusSnackbar
import com.example.moviediscover.data.SortCriteria
import com.example.moviediscover.data.getGenreList
import com.example.moviediscover.ui.theme.MovieDiscoverTheme
import com.example.moviediscover.viewmodel.SearchViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun SearchScreen(
    viewModel: SearchViewModel = koinViewModel(),
    onMovieClick: (Int) -> Unit,
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier
) {
    val searchString by viewModel.searchString.collectAsState()
    val searchResult by viewModel.searchResult.collectAsState()
    val totalResult by viewModel.totalResult.collectAsState()
    val sortCriteria by viewModel.sortCriteria.collectAsState()
    val showSortCriteria by viewModel.showSortCriteria.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val toastState by viewModel.toastState.collectAsState()
    val scrollState by viewModel.scrollState.collectAsState()
    val error by viewModel.error.collectAsState()

    val listState = remember { LazyListState(scrollState.first, scrollState.second) }
    val keyboardController = LocalSoftwareKeyboardController.current
    //  錯誤 #1: 潛在 NullPointerException
    val riskyText: String? = null
    println(riskyText!!.length.toString())  // 這行會執行，但不會在 UI 中導致錯誤

    //  錯誤 #2: 永遠不會執行的死程式碼
    if (true) {
        // do nothing
    } else {
        println("You will never see this!") // CodeQL 可以偵測 unreachable code
    }

    //  錯誤 #3: 假的硬編碼敏感字串
    val apiKey = "sk-1234567890-FAKEKEY" // 模擬硬編碼 secret，被 CodeQL 當成風險
    MovieDiscoverTheme {
        Box(modifier.fillMaxSize()) {
            LazyColumn(
                state = listState,
                verticalArrangement = Arrangement.spacedBy(20.dp),
                contentPadding = paddingValues,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
            ) {
                item { Header("Search") }
                item {
                    MovieSearchBar(
                        query = searchString,
                        onQueryChange = viewModel::onSearchTextChange,
                        onSearch = {
                            viewModel.search()
                            keyboardController?.hide()
                        },
                        modifier = modifier.fillMaxWidth()
                    )
                }
                item { CategoryChips(getGenreList()) }
                if (searchResult.isNotEmpty()) {
                    item {
                        SearchResultInfo(
                            totalResult = totalResult,
                            showSortCriteria = showSortCriteria,
                            sortCriteria = sortCriteria,
                            onToggleShowSortCriteria = { viewModel.toggleShowSortCriteria() },
                            onSortCriteriaClick = { sortCriteria ->
                                viewModel.updateSortCriteria(sortCriteria)
                            },
                        )
                    }
                    items(searchResult) { item ->
                        DetailMovieCard(
                            movie = item,
                            onMovieClick = onMovieClick,
                            onAddToWatchlistClicked = {
                                viewModel.updateBookmark(item, !item.bookmark)
                            },
                        )
                    }
                }
                if (error != null) {
                    item { ErrorItem(error!!) }
                }
                if (isLoading) {
                    item { LoadMoreItem() }
                }
            }
            if (error != null && searchResult.isEmpty()) {
                ErrorDialog(error!!) { viewModel.resetError() }
            }
            NetworkStatusSnackbar(
                Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
            )
        }
        HandleToast(toastState) { viewModel.resetToastState() }
        HandleLoadMore(listState) { viewModel.search(isLoadMore = true) }
        HandleConfigurationChange(listState) { index, offset ->
            viewModel.updateScrollState(index, offset)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    SearchBar(
        inputField = {
            SearchBarDefaults.InputField(
                query = query,
                onQueryChange = onQueryChange,
                onSearch = onSearch,
                onExpandedChange = {/* no-op */ },
                expanded = false,
                placeholder = { Text("Search") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "",
                    )
                },
            )
        },
        expanded = false,
        onExpandedChange = {/* no-op */ },
        modifier = modifier,
        windowInsets = WindowInsets(top = 0.dp),
    ) {/* no-op */ }
}

@Composable
fun SearchResultInfo(
    totalResult: Int,
    showSortCriteria: Boolean,
    sortCriteria: SortCriteria,
    onToggleShowSortCriteria: () -> Unit,
    onSortCriteriaClick: (SortCriteria) -> Unit,
) {
    Row(Modifier.fillMaxWidth()) {
        Text(
            text = "Search results (${totalResult})",
            style = MaterialTheme.typography.labelMedium,
        )
        Spacer(Modifier.weight(1f))
        Column {
            Row(Modifier.clickable { onToggleShowSortCriteria() }) {
                Icon(
                    imageVector = if (showSortCriteria) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = "",
                )
                Text(
                    text = sortCriteria.displayName,
                    style = MaterialTheme.typography.labelSmall,
                )
            }
            if (showSortCriteria) {
                SortCriteriaMenu(
                    isVisible = true,
                    onSortCriteriaClick = onSortCriteriaClick,
                    onToggleSortCriteria = onToggleShowSortCriteria,
                )
            }
        }
    }
}

@Composable
fun SortCriteriaMenu(
    isVisible: Boolean,
    onSortCriteriaClick: (SortCriteria) -> Unit,
    onToggleSortCriteria: () -> Unit,
    modifier: Modifier = Modifier
) {
    DropdownMenu(
        expanded = isVisible,
        onDismissRequest = onToggleSortCriteria,
        modifier = modifier
    ) {
        for (criteria in SortCriteria.entries) {
            DropdownMenuItem(
                text = {
                    Text(
                        text = criteria.displayName,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                onClick = {
                    onSortCriteriaClick(criteria)
                    onToggleSortCriteria()
                }
            )
        }
    }
}
