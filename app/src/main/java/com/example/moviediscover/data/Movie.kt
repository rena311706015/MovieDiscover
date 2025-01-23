package com.example.moviediscover.data

data class Movie(
    val id: Int = 0,
    val posterPath: String? = null,
    val title: String = "",
    val vote: Double = 9.99,
    val bookmark: Boolean = false,
    val genres: List<String> = listOf(),
    val releaseDate: String = "",
    val overview: String = "",
) {
    fun getCategoryString() = genres.joinToString(", ")
}

fun getSampleMovie(): Movie = Movie(
    id = 1111,
    posterPath = "/i47IUSsN126K11JUzqQIOi1Mg1M.jpg",
    title = "Kraven the Hunter",
    vote = 6.462,
    bookmark = false,
    genres = listOf("Action", "Adventure", "Thriller"),
    overview = "Kraven Kravinoff's complex relationship with his ruthless gangster father, Nikolai, starts him down a path of vengeance with brutal consequences, motivating him to become not only the greatest hunter in the world, but also one of its most feared."
)

fun getSampleMovieList(): List<Movie> {
    val movie = getSampleMovie()
    val list = List(30) { movie }
    return list
}

fun getSampleCategoryList() = listOf(
    "All",
    "Action",
    "Adventure",
    "Animation",
    "Comedy",
    "Crime",
    "Documentary",
    "Drama",
    "Family",
    "Fantasy",
    "History",
    "Horror",
    "Music",
    "Mystery",
    "Romance",
    "Science Fiction",
    "TV Movie",
    "Thriller",
    "War",
    "Western"
)
