package com.example.moviediscover.network

import com.example.moviediscover.BuildConfig

class NetworkConst {
    companion object {
        val TOKEN: String
            get() = BuildConfig.API_TOKEN

        const val API_BASE = "https://api.themoviedb.org/3"
        const val API_CATEGORY_MOVIE = "/movie"
        const val API_CATEGORY_SEARCH_MOVIE = "/search/movie"
        const val API_IMAGE = "https://image.tmdb.org/t/p/original"
    }
}
