package com.example.moviediscover.network

import com.example.moviediscover.data.Movie

data class MovieApiResponse(
    val page: Int,
    val results: List<MovieResponse>,
    val totalPage: Int,
    val totalResults: Int,
) {
    fun getMutableMovieList(): MutableList<Movie> {
        return results.map {
            Movie(
                id = it.id,
                genres = if (it.genres == null) listOf() else it.genres.map { genre -> genre.name },
                title = it.title,
                vote = it.vote_average,
                posterPath = if (it.poster_path == null) null else "https://image.tmdb.org/t/p/original" + it.poster_path,
                releaseDate = it.release_date,
                overview = it.overview
            )
        }.toMutableList() ?: mutableListOf()
    }
}