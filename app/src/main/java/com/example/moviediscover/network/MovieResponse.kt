package com.example.moviediscover.network

import com.example.moviediscover.data.Genre

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
    val release_date: String,
    val title: String,
    val vote_average: Double,
    val vote_count: Int
)
/*
{
    "adult": false,
    "backdrop_path": "/HLNprTMU4dZeroXmSBbgdzt6f6.jpg",
    "belongs_to_collection": null,
    "budget": 0,
    "genres": [
    {
        "id": 27,
        "name": "Horror"
    },],
    "homepage": "https://film-seekers.com/movie/pandemonium/",
    "id": 1080512,
    "imdb_id": "tt22299048",
    "origin_country": [
    "FR"
    ],
    "original_language": "fr",
    "original_title": "Pandemonium",
    "overview": "Nathan and Daniel are caught in a road accident that kills them both. As they come to grips with their deaths, Nathan confronts his past and the consequences of his actions. Now trapped in the hellish void of Pandemonium, he encounters tortured souls like Jeanne, a disturbed child; Julia, a grief-stricken mother; and Norghul, the guide of the great void. Will he find a way to escape the torment that awaits him for eternity?",
    "popularity": 33.356,
    "poster_path": "/xKZZN3aDsg6DZNnHZUDkTXKvMiA.jpg",
    "production_companies": [
    {
        "id": 132315,
        "logo_path": null,
        "name": "Transgressive Production",
        "origin_country": ""
    }
    ],
    "production_countries": [
    {
        "iso_3166_1": "FR",
        "name": "France"
    }
    ],
    "release_date": "2024-12-25",
    "revenue": 0,
    "runtime": 95,
    "spoken_languages": [
    {
        "english_name": "French",
        "iso_639_1": "fr",
        "name": "Français"
    }
    ],
    "status": "Released",
    "tagline": "Hell knows no forgiveness.",
    "title": "Pandemonium",
    "video": false,
    "vote_average": 7.3,
    "vote_count": 3
}
 */