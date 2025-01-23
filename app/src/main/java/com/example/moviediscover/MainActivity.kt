package com.example.moviediscover

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.moviediscover.screen.BookmarkScreen
import com.example.moviediscover.screen.DetailScreen
import com.example.moviediscover.screen.HomeScreen
import com.example.moviediscover.screen.MovieListScreen
import com.example.moviediscover.screen.SearchScreen
import com.example.moviediscover.ui.theme.MovieDiscoverTheme
import com.example.moviediscover.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            val windowSize = calculateWindowSizeClass(this)
            when (windowSize.widthSizeClass) {
                WindowWidthSizeClass.Expanded -> {
                    MovieDiscoverAppLandscape(navController)
                }

                else -> {
                    MovieDiscoverAppPortrait(navController)
                }
            }
        }
    }
}

@Composable
fun MovieDiscoverAppPortrait(navHostController: NavHostController) {
    MovieDiscoverTheme {
        Scaffold(
            bottomBar = { BottomNavigation(navHostController) },
            modifier = Modifier.fillMaxSize(),
            contentWindowInsets = WindowInsets(0.dp)
        ) { innerPadding ->
            MovieDiscoverNavHost(
                navHostController = navHostController,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@Composable
fun MovieDiscoverAppLandscape(navHostController: NavHostController) {
    MovieDiscoverTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Row {
                NavigationRail(navHostController)
                MovieDiscoverNavHost(
                    navHostController = navHostController,
                    modifier = Modifier.padding()
                )
            }
        }
    }
}

@Composable
fun MovieDiscoverNavHost(
    navHostController: NavHostController,
    modifier: Modifier = Modifier
) {
    val mainViewModel: MainViewModel = viewModel()
    val onMovieClick = { movieId: Int -> navHostController.navigate("DETAIL/${movieId}") }
    NavHost(
        navController = navHostController,
        startDestination = Screen.Home.route,
        modifier = modifier,
    ) {
        composable(route = Screen.Home.route) {
            HomeScreen(
                nowPlayingMovieList = mainViewModel.movieLists[MovieListType.NOW_PLAYING],
                topRatedMovieList = mainViewModel.movieLists[MovieListType.TOP_RATED],
                upcomingMovieList = mainViewModel.movieLists[MovieListType.UPCOMING],
                onNavigateToMovieListScreen = { listType: MovieListType ->
                    navHostController.navigate(Screen.MovieList.createRoute(listType))
                },
                onMovieClick = onMovieClick
            )
        }
        composable(
            route = "${Screen.MovieList.baseRoute}/{listType}",
            arguments = listOf(navArgument("listType") {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val listTypeName = backStackEntry.arguments?.getString("listType")
            val listType = MovieListType.valueOf(
                listTypeName ?: throw IllegalArgumentException("Invalid listType")
            )
            MovieListScreen(
                listType = listType,
                onMovieClick = onMovieClick,
                viewModel = mainViewModel
            )
        }
        composable(route = Screen.Bookmark.route) { BookmarkScreen(onMovieClick = onMovieClick) }
        composable(route = Screen.Search.route) { SearchScreen(onMovieClick = onMovieClick) }
        composable(
            route = "${Screen.Detail.route}/{movieId}"
        ) { backStackEntry ->
            val movieId =
                backStackEntry.arguments?.getString("movieId")?.toIntOrNull()
            if (movieId != null) {
                DetailScreen(movieId = movieId)
            }
        }
    }
}