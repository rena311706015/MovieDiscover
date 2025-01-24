package com.example.moviediscover.network

import com.example.moviediscover.data.Movie

data class MovieListResponse(
    val page: Int,
    val results: List<MovieResponse>,
    val total_pages: Int,
    val total_results: Int,
) {
    fun transformToMovieList(): List<Movie> = results.map { it.transformToMovie() }
}