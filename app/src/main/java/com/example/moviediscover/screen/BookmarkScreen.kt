package com.example.moviediscover.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.moviediscover.getStyledTitle
import com.example.moviediscover.ui.theme.MovieDiscoverTheme

@Composable
fun BookmarkScreen(
    onMovieClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    MovieDiscoverTheme {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentHeight(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item { Spacer(modifier.statusBarsPadding()) }
            item {
                Text(
                    text = getStyledTitle("Bookmark"),
                    style = MaterialTheme.typography.displayLarge,
                )
            }
        }
    }
}