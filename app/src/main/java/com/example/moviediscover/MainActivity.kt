package com.example.moviediscover

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.moviediscover.components.BottomNavigation
import com.example.moviediscover.components.NavigationRail
import com.example.moviediscover.components.NetworkStatusSnackbar
import com.example.moviediscover.data.MovieListType
import com.example.moviediscover.data.Screen
import com.example.moviediscover.network.NetworkStateUtil
import com.example.moviediscover.screen.BookmarkScreen
import com.example.moviediscover.screen.DetailScreen
import com.example.moviediscover.screen.HomeScreen
import com.example.moviediscover.screen.MovieListScreen
import com.example.moviediscover.screen.SearchScreen
import com.example.moviediscover.ui.theme.MovieDiscoverTheme
import com.example.moviediscover.viewmodel.HomeViewModel
import com.example.moviediscover.viewmodel.MainViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        NetworkStateUtil.initConnectivityManager(this)
        setContent {
            val navHostController = rememberNavController()
            val windowSize = calculateWindowSizeClass(this)
            when (windowSize.widthSizeClass) {
                WindowWidthSizeClass.Expanded -> MovieDiscoverNavHost(
                    navHostController,
                    isLandscape = true
                )

                else -> MovieDiscoverNavHost(navHostController, isLandscape = false)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        NetworkStateUtil.registerNetworkCallback()
    }

    override fun onStop() {
        super.onStop()
        NetworkStateUtil.unregisterNetworkCallback()
    }
}

@Composable
fun MovieDiscoverNavHost(
    navHostController: NavHostController,
    isLandscape: Boolean,
    homeViewModel: HomeViewModel = koinViewModel(),
    modifier: Modifier = Modifier
) {
    val onMovieClick = { movieId: Int ->
        val route = Screen.Detail.createRoute(movieId)
        navHostController.navigate(route) {
            popUpTo(route)
            launchSingleTop = true
        }
    }
    NavHost(
        navController = navHostController,
        startDestination = Screen.Home.route,
        enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start) },
        exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Start) },
        popEnterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.End) },
        popExitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End) },
        modifier = modifier,
    ) {
        composable(route = Screen.Home.route) {
            ResponsiveContainer(navHostController, isLandscape) { innerPadding ->
                HomeScreen(
                    viewModel = homeViewModel,
                    onNavigateToMovieListScreen = { listType: MovieListType ->
                        val route = Screen.MovieList.createRoute(listType)
                        navHostController.navigate(route) {
                            popUpTo(route)
                            launchSingleTop = true
                        }
                    },
                    onMovieClick = onMovieClick,
                    paddingValues = innerPadding,
                )
            }
        }
        composable(
            route = "${Screen.MovieList.BASE_ROUTE}/{listType}",
            arguments = listOf(navArgument("listType") { type = NavType.StringType })
        ) { backStackEntry ->
            val listTypeName = backStackEntry.arguments?.getString("listType")
            val listType = MovieListType.valueOf(
                listTypeName ?: throw IllegalArgumentException("Invalid listType")
            )
            MovieListScreen(
                listType = listType,
                onMovieClick = onMovieClick,
                onBack = { navHostController.navigateUp() }
            )
        }
        composable(route = Screen.Bookmark.route) {
            ResponsiveContainer(navHostController, isLandscape) { innerPadding ->
                BookmarkScreen(
                    onMovieClick = onMovieClick,
                    paddingValues = innerPadding,
                )
            }
        }
        composable(route = Screen.Search.route) {
            ResponsiveContainer(
                navHostController,
                isLandscape
            ) { innerPadding ->
                SearchScreen(
                    onMovieClick = onMovieClick,
                    paddingValues = innerPadding,
                )
            }
        }
        composable(route = "${Screen.Detail.BASE_ROUTE}/{movieId}") { backStackEntry ->
            val movieId =
                backStackEntry.arguments?.getString("movieId")?.toIntOrNull()
            if (movieId != null) {
                DetailScreen(movieId = movieId, onBack = { navHostController.navigateUp() })
            }
        }
    }
}

@Composable
fun ResponsiveContainer(
    navHostController: NavHostController,
    isLandscape: Boolean,
    content: @Composable (PaddingValues) -> Unit,
) {
    if (isLandscape) {
        LandscapeContainer(navHostController) { innerPadding -> content(innerPadding) }
    } else {
        PortraitContainer(navHostController) { innerPadding -> content(innerPadding) }
    }
}


@Composable
fun PortraitContainer(
    navHostController: NavHostController,
    content: @Composable (PaddingValues) -> Unit,
) {
    MovieDiscoverTheme {
        Scaffold(
            bottomBar = { BottomNavigation(navHostController) },
            snackbarHost = { NetworkStatusSnackbar() },
            modifier = Modifier.fillMaxSize(),
            contentWindowInsets = WindowInsets.systemBars
        ) { innerPadding ->
            val contentPadding = PaddingValues(
                start = innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                top = innerPadding.calculateTopPadding() + 16.dp,
                end = innerPadding.calculateEndPadding(LayoutDirection.Ltr),
                bottom = innerPadding.calculateBottomPadding() + 16.dp
            )
            content(contentPadding)
        }
    }
}

@Composable
fun LandscapeContainer(
    navHostController: NavHostController,
    content: @Composable (PaddingValues) -> Unit,
) {
    MovieDiscoverTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Box {
                Row {
                    NavigationRail(navHostController)
                    val systemPadding = WindowInsets.systemBars.asPaddingValues()
                    content(systemPadding)
                }
                NetworkStatusSnackbar(Modifier.align(Alignment.BottomCenter).padding(bottom = 16.dp))
            }
        }
    }
}