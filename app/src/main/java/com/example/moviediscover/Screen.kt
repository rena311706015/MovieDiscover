package com.example.moviediscover


sealed class Screen(val route: String) {
    data object Home : Screen("Home")
    data object Search : Screen("Search")
    data object Bookmark : Screen("Bookmark")
    data object Detail : Screen("Detail")
    data class MovieList(val listType: MovieListType) : Screen("MovieList/${listType.name}") {
        companion object {
            const val baseRoute = "MovieList"
            fun createRoute(listType: MovieListType): String {
                return "$baseRoute/${listType.name}"
            }
        }
    }
}