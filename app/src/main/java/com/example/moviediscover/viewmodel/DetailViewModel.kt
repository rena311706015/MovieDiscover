package com.example.moviediscover.viewmodel

import androidx.lifecycle.viewModelScope
import com.example.moviediscover.data.Movie
import com.example.moviediscover.database.MovieRepository
import com.example.moviediscover.network.MovieResponse
import com.example.moviediscover.network.NetworkConst.Companion.API_BASE
import com.example.moviediscover.network.NetworkConst.Companion.API_CATEGORY_MOVIE
import com.example.moviediscover.network.NetworkConst.Companion.TOKEN
import com.example.moviediscover.network.NetworkProvider
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DetailViewModel(repository: MovieRepository) : BaseViewModel(repository) {

    private val _movie = MutableStateFlow<Movie?>(null)
    val movie = _movie.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun getMovieDetail(movieId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            val response: HttpResponse =
                NetworkProvider.client.get("$API_BASE$API_CATEGORY_MOVIE/$movieId") {
                    headers {
                        append(HttpHeaders.Accept, "application/json")
                        append(HttpHeaders.Authorization, TOKEN)
                    }
                }
            val responseString = response.body<String>()
            val movieResponse =
                NetworkProvider.gson.fromJson(responseString, MovieResponse::class.java)
            val originMovie = movieResponse.transformToMovie()
            val movieWithBookmark =
                if (originMovie.id in bookmarkMovieIdList) originMovie.copy(bookmark = true) else originMovie
            _movie.value = movieWithBookmark
            _isLoading.value = false
        }
    }

    fun updateBookmark(movie: Movie, isAdding: Boolean) {
        super.updateBookmark(movie = movie, isAdding = isAdding) {
            _movie.value = movie.copy(bookmark = isAdding)
        }
    }
}