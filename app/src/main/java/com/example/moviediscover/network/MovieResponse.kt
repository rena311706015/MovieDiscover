package com.example.moviediscover.network

import com.example.moviediscover.data.Genre
import com.example.moviediscover.data.Movie

data class MovieResponse(
    val adult: Boolean,
    val genres: List<Genre>? = null,
    val homepage: String,
    val id: Int,
    val original_language: String,
    val original_title: String,
    val overview: String,
    val popularity: Double,
    val poster_path: String?,
    val release_date: String?,
    val title: String,
    val vote_average: Double,
    val vote_count: Int
) {
    fun transformToMovie(): Movie = Movie(
        id = id,
        title = title,
        vote = vote_average,
        posterPath = if (poster_path == null) null else NetworkConst.API_IMAGE + poster_path,
        genres = genres?.joinToString(", ") { genre -> genre.name } ?: "",
        releaseDate = release_date ?: "",
        overview = overview,
    )
}