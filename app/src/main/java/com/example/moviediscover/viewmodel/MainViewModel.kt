package com.example.moviediscover.viewmodel

import androidx.lifecycle.ViewModel
import com.example.moviediscover.MovieListType
import com.example.moviediscover.data.Movie
import com.example.moviediscover.network.MovieApiResponse
import com.example.moviediscover.network.NetworkConst.Companion.API_BASE
import com.example.moviediscover.network.NetworkConst.Companion.API_CATEGORY_MOVIE
import com.example.moviediscover.network.NetworkConst.Companion.TOKEN
import com.google.gson.Gson
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.runBlocking

class MainViewModel : ViewModel() {
    val movieLists = mutableMapOf<MovieListType, MutableList<Movie>>(
        MovieListType.POPULAR to mutableListOf(),
        MovieListType.NOW_PLAYING to mutableListOf(),
        MovieListType.TOP_RATED to mutableListOf(),
        MovieListType.UPCOMING to mutableListOf()
    )

    private val client = HttpClient {
        expectSuccess = true
    }
    private val gson = Gson()

    init {
        getMovieList(MovieListType.NOW_PLAYING)
        getMovieList(MovieListType.TOP_RATED)
        getMovieList(MovieListType.UPCOMING)
    }

    fun getMovieList(type: MovieListType) {
        runBlocking {
            val response: HttpResponse = client.get(API_BASE + API_CATEGORY_MOVIE + type.endpoint) {
                headers {
                    append(HttpHeaders.Accept, "application/json")
                    append(HttpHeaders.Authorization, TOKEN)
                }
            }
            val responseString = response.body<String>()
            val movieApiResponse = gson.fromJson(responseString, MovieApiResponse::class.java)
            movieLists[type] = movieApiResponse.getMutableMovieList()
        }
    }
}