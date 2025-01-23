package com.example.moviediscover.data

enum class SortCriteria(val displayName: String) {
    NAME("Name"),
    Rating("Rating (Highest)"),
    DATE_LATEST("Release Date (Latest)"),
    DATE_OLDEST("Release Date (Oldest)"),
}