package com.example.moviediscover.data

sealed class Screen(val route: String) {
    data object Home : Screen("Home")
    data object Search : Screen("Search")
    data object Bookmark : Screen("Bookmark")
    data class Detail(val movieId: Int) : Screen("Detail/${movieId}") {
        companion object {
            const val BASE_ROUTE = "Detail"
            fun createRoute(movieId: Int): String {
                return "$BASE_ROUTE/${movieId}"
            }
        }
    }

    data class MovieList(val listType: MovieListType) : Screen("MovieList/${listType.name}") {
        companion object {
            const val BASE_ROUTE = "MovieList"
            fun createRoute(listType: MovieListType): String {
                return "$BASE_ROUTE/${listType.name}"
            }
        }
    }
}