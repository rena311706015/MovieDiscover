package com.example.moviediscover.viewmodel

import androidx.lifecycle.ViewModel
import com.example.moviediscover.data.Movie
import com.example.moviediscover.network.MovieResponse
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.runBlocking

class DetailViewModel : ViewModel() {

    private val client = HttpClient {
        expectSuccess = true
    }
    private val gson = Gson()
    private val _movie = MutableStateFlow(Movie())
    val movie = _movie.asStateFlow()

    fun getMovieDetail(movieId: Int) {
        runBlocking {
            val response: HttpResponse = client.get("$API_BASE$API_CATEGORY_MOVIE/$movieId") {
                headers {
                    append(HttpHeaders.Accept, "application/json")
                    append(HttpHeaders.Authorization, TOKEN)
                }

            }
            val responseString = response.body<String>()
            val movieResponse = gson.fromJson(responseString, MovieResponse::class.java)
            _movie.value = Movie(
                id = movieResponse.id,
                title = movieResponse.title,
                vote = movieResponse.vote_average,
                posterPath = if (movieResponse.poster_path == null) null else "https://image.tmdb.org/t/p/original" + movieResponse.poster_path,
//                posterPath = if(movieResponse.poster_path == "null") "null" else "https://image.tmdb.org/t/p/original" + movieResponse.poster_path,
                genres = movieResponse.genres!!.map { it.name },
                releaseDate = movieResponse.release_date,
                overview = movieResponse.overview
            )
        }
    }
}