package com.example.moviediscover.components

import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce

@OptIn(FlowPreview::class)
@Composable
fun HandleConfigurationChange(
    state: ScrollableState,
    onScrollUpdate: (index: Int, offset: Int) -> Unit
) {
    LaunchedEffect(state) {
        when (state) {
            is LazyListState -> {
                snapshotFlow { state.firstVisibleItemIndex to state.firstVisibleItemScrollOffset }
                    .debounce(100)
                    .collect { (index, offset) ->
                        onScrollUpdate(index, offset)
                    }
            }

            is LazyGridState -> {
                snapshotFlow { state.firstVisibleItemIndex to state.firstVisibleItemScrollOffset }
                    .debounce(100)
                    .collect { (index, offset) ->
                        onScrollUpdate(index, offset)
                    }
            }
        }
    }
}

@Composable
fun HandleLoadMore(
    state: ScrollableState,
    loadMore: () -> Unit,
) {
    var totalItemsCount = 0
    var lastVisibleItemIndex = 0
    val shouldLoadMore by remember {
        derivedStateOf {
            when (state) {
                is LazyListState -> {
                    totalItemsCount = state.layoutInfo.totalItemsCount
                    lastVisibleItemIndex =
                        state.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                }

                is LazyGridState -> {
                    totalItemsCount = state.layoutInfo.totalItemsCount
                    lastVisibleItemIndex =
                        state.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                }
            }
            totalItemsCount > 5 && lastVisibleItemIndex >= (totalItemsCount - 5)
        }
    }
    if (shouldLoadMore) loadMore()
}