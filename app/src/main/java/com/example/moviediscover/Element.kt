package com.example.moviediscover

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.moviediscover.data.Movie
import com.example.moviediscover.data.getSampleCategoryList
import com.example.moviediscover.data.getSampleMovie
import com.example.moviediscover.ui.theme.MovieDiscoverTheme
import com.example.moviediscover.ui.theme.OverviewTextColor

@Composable
fun DetailMovieCard(
    movie: Movie,
    onMovieClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(modifier = Modifier.clickable { onMovieClick(movie.id) }) {
        Box(Modifier.fillMaxWidth()) {
            Row {
                Poster(
                    posterPath = movie.posterPath,
                    modifier = Modifier
                        .size(width = 182.dp, height = 273.dp)
                )
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp)
                ) {
                    Title(
                        title = movie.title,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )
                    Vote(movie.vote)
                    Overview(movie.overview)
                }
            }
            AddToWatchlistButton(modifier = Modifier.align(Alignment.BottomEnd)) {}
        }
    }
}

@Composable
fun CategoryChips(
    categoryList: List<String>,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    val categorySelected: List<Boolean> = List(10) { false }

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items(categoryList) {
            FilterChip(
                label = {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                selected = true,
                shape = CircleShape,
                onClick = {},
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DetailMovieCardPreview() {
    MovieDiscoverTheme {
        DetailMovieCard(getSampleMovie(), {})
    }
}

@Preview(showBackground = true)
@Composable
fun CategoryChipsPreview() {
    MovieDiscoverTheme {
        CategoryChips(getSampleCategoryList())
    }
}

@Composable
fun Header(header: String, modifier: Modifier = Modifier) {
    Text(
        text = getStyledTitle(header),
        style = MaterialTheme.typography.displayLarge,
        modifier = modifier
    )
}

@Composable
fun HeaderWithSeeMoreButton(
    header: String,
    onSeeMoreClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier) {
        Header(header)
        Spacer(Modifier.weight(1f))
        TextButton(
            onClick = onSeeMoreClick,
            modifier = Modifier.align(Alignment.CenterVertically)
        ) {
            Text(
                text = "See More >",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
fun Poster(posterPath: String?, modifier: Modifier = Modifier) {
    if (posterPath.isNullOrEmpty()) {
        Box {
            Image(
                painter = ColorPainter(MaterialTheme.colorScheme.primary),
                contentDescription = "",
                modifier = modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(15.dp))
            )
            Text(
                text = "No Image",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    } else {
        AsyncImage(
            model = ImageRequest.Builder(context = LocalContext.current)
                .data(posterPath)
                .crossfade(true)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            error = ColorPainter(MaterialTheme.colorScheme.primary),
            fallback = ColorPainter(MaterialTheme.colorScheme.primary),
            placeholder = ColorPainter(MaterialTheme.colorScheme.primary),
            modifier = modifier.clip(RoundedCornerShape(15.dp))
        )
    }
}

@Composable
fun Title(title: String, maxLines: Int = 2, modifier: Modifier = Modifier) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier
    )
}

@SuppressLint("DefaultLocale")
@Composable
fun Vote(vote: Double, modifier: Modifier = Modifier) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = String.format("%.2f", vote),
            style = MaterialTheme.typography.labelLarge
        )
        Icon(
            imageVector = Icons.Default.Star,
            tint = MaterialTheme.colorScheme.primary,
            contentDescription = null,
            modifier = modifier
                .padding(horizontal = 4.dp)
                .size(24.dp)
        )
    }
}

@Composable
fun Overview(overview: String, modifier: Modifier = Modifier) {
    Text(
        modifier = modifier,
        text = overview,
        style = MaterialTheme.typography.bodyMedium,
        color = OverviewTextColor,
        maxLines = 5,
        overflow = TextOverflow.Ellipsis,
    )
}

@Composable
fun AddToWatchlistButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    TextButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Text(
            text = "+ Add to Watchlist",
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Composable
fun IndeterminateCircularIndicator(modifier: Modifier = Modifier) {
    CircularProgressIndicator(
        modifier = modifier.width(64.dp),
        color = MaterialTheme.colorScheme.secondary,
        trackColor = MaterialTheme.colorScheme.surfaceVariant,
    )
}


@Composable
fun BottomNavigation(
    navController: NavController,
) {
    val bottomNavItems = NavItem.getNavItems()
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        for (screen in bottomNavItems) {
            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(if (currentRoute == screen.route) screen.selectedIcon else screen.unselectedIcon),
                        tint = if (currentRoute == screen.route) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                        contentDescription = null
                    )
                },
                selected = currentRoute == screen.route,
                onClick = { navController.navigate(screen.route) }
            )
        }
    }
}

@Composable
fun NavigationRail(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    androidx.compose.material3.NavigationRail(
        modifier = modifier.padding(start = 8.dp, end = 8.dp),
        containerColor = MaterialTheme.colorScheme.background,
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        Column(
            modifier = modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val railNavItems = NavItem.getNavItems()
            for (screen in railNavItems) {
                NavigationRailItem(
                    icon = {
                        Icon(
                            painter = painterResource(if (currentRoute == screen.route) screen.selectedIcon else screen.unselectedIcon),
                            tint = if (currentRoute == screen.route) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                            contentDescription = null
                        )
                    },
                    selected = currentRoute == screen.route,
                    onClick = { navController.navigate(screen.route) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

class NavItem(
    val route: String,
    @DrawableRes val selectedIcon: Int,
    @DrawableRes val unselectedIcon: Int,
) {
    companion object {
        fun getNavItems() = listOf(
            NavItem(Screen.Home.route, R.drawable.home_filled, R.drawable.home_border),
            NavItem(Screen.Search.route, R.drawable.search_border, R.drawable.search_border),
            NavItem(
                Screen.Bookmark.route,
                R.drawable.bookmark_filled,
                R.drawable.bookmark_border
            ),
        )
    }
}

@Composable
fun getStyledTitle(title: String) = buildAnnotatedString {
    append(title)
    withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
        append(".")
    }
}
