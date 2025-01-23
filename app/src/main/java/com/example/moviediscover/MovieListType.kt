package com.example.moviediscover

import kotlinx.serialization.Serializable

@Serializable
enum class MovieListType(val endpoint: String = "", val header: String = "") {
    POPULAR("/popular", "Popular"),
    NOW_PLAYING("/now_playing", "Now Playing"),
    TOP_RATED("/top_rated", "Top Rated"),
    UPCOMING("/upcoming", "Upcoming"),
}