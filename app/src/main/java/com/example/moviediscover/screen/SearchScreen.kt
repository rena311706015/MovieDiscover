package com.example.moviediscover.screen

import android.util.Log
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moviediscover.CategoryChips
import com.example.moviediscover.DetailMovieCard
import com.example.moviediscover.Header
import com.example.moviediscover.IndeterminateCircularIndicator
import com.example.moviediscover.data.SortCriteria
import com.example.moviediscover.data.getSampleCategoryList
import com.example.moviediscover.ui.theme.MovieDiscoverTheme
import com.example.moviediscover.viewmodel.SearchViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel = viewModel(),
    onMovieClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val searchString by viewModel.searchString.collectAsState()
    val searchResult by viewModel.searchResult.collectAsState()
    val sortCriteria by viewModel.sortCriteria.collectAsState()
    val showSortCriteria by viewModel.showSortCriteria.collectAsState()
    val loading by viewModel.loading.collectAsState()
    Log.e("Rena", "loading = $loading")

    MovieDiscoverTheme {
        if (loading) {
            Box(Modifier.fillMaxSize()) {
                IndeterminateCircularIndicator(modifier.align(Alignment.Center))
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentHeight(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                if (searchResult.isNotEmpty()) {
                    item { Spacer(modifier.statusBarsPadding()) }
                }
                item {
                    Header("Search")
                    androidx.compose.material3.SearchBar(
                        query = searchString,
                        onQueryChange = viewModel::onSearchTextChange,
                        onSearch = { viewModel.search() },
                        active = false,
                        onActiveChange = { /* no-op */ },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "",
                            )
                        },
                        placeholder = { Text("Search") },
                        windowInsets = WindowInsets(top = 0.dp),
                        modifier = modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp)

                    ) { /* no-op */ }
                    CategoryChips(getSampleCategoryList())
                }
                if (searchResult.isNotEmpty()) {
                    item {
                        Row(Modifier.fillMaxWidth()) {
                            Text(
                                text = "Search results (${searchResult.size})",
                                style = MaterialTheme.typography.labelMedium,
                            )
                            Spacer(Modifier.weight(1f))
                            Column {
                                Row(Modifier.clickable { viewModel.updateShowSortCriteria() }) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
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
                                        onSortCriteriaClick = { sortCriteria ->
                                            viewModel.updateSortCriteria(
                                                sortCriteria
                                            )
                                        },
                                        onShowSortCriteriaChange = { viewModel.updateShowSortCriteria() },
                                    )
                                }
                            }
                        }

                    }
                }
                items(searchResult) { item ->
                    DetailMovieCard(item, onMovieClick)
                }
            }
        }
    }
}

@Composable
fun SortCriteriaMenu(
    isVisible: Boolean,
    onSortCriteriaClick: (SortCriteria) -> Unit,
    onShowSortCriteriaChange: () -> Unit,
    modifier: Modifier = Modifier
) {
    DropdownMenu(
        expanded = isVisible,
        onDismissRequest = onShowSortCriteriaChange,
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
                    onSortCriteriaClick.invoke(criteria)
                    onShowSortCriteriaChange.invoke()
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SearchScreenPreview() {
    SearchScreen(onMovieClick = {})
}