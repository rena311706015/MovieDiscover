package com.example.moviediscover.data

import kotlinx.serialization.Serializable

@Serializable
enum class MovieListType(val endpoint: String = "", val displayName: String = "") {
    POPULAR("/popular", "Popular"),
    NOW_PLAYING("/now_playing", "Now Playing"),
    TOP_RATED("/top_rated", "Top Rated"),
    UPCOMING("/upcoming", "Upcoming"),
}