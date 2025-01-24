package com.example.moviediscover.viewmodel

import androidx.lifecycle.viewModelScope
import com.example.moviediscover.data.Movie
import com.example.moviediscover.data.MovieListType
import com.example.moviediscover.database.MovieRepository
import com.example.moviediscover.network.MovieListResponse
import com.example.moviediscover.network.NetworkConst.Companion.API_BASE
import com.example.moviediscover.network.NetworkConst.Companion.API_CATEGORY_MOVIE
import com.example.moviediscover.network.NetworkConst.Companion.TOKEN
import com.example.moviediscover.network.NetworkProvider
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MovieListViewModel(repository: MovieRepository) : BaseViewModel(repository) {

    private val _scrollState = MutableStateFlow(Pair(0, 0))
    val scrollState = _scrollState.asStateFlow()

    private val _movieList = MutableStateFlow(listOf<Movie>())
    val movieList = _movieList.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private var currentPage = 1

    fun getMovieList(type: MovieListType) {
        viewModelScope.launch {
            _isLoading.value = true
            val response: HttpResponse =
                NetworkProvider.client.get(API_BASE + API_CATEGORY_MOVIE + type.endpoint) {
                    headers {
                        append(HttpHeaders.Accept, "application/json")
                        append(HttpHeaders.Authorization, TOKEN)
                    }
                    url {
                        parameters.append("page", currentPage.toString())
                    }
                }
            val responseString = response.body<String>()
            val movieListResponse =
                NetworkProvider.gson.fromJson(responseString, MovieListResponse::class.java)
            val originMovieList = movieListResponse.transformToMovieList()
            val movieListWithBookmark =
                originMovieList.map { if (it.id in bookmarkMovieIdList) it.copy(bookmark = true) else it }
            currentPage += 1
            _movieList.value += movieListWithBookmark
            _isLoading.value = false
        }
    }

    fun updateBookmark(movie: Movie, isAdding: Boolean) {
        super.updateBookmark(movie = movie, isAdding = isAdding) {
            _movieList.update { movies ->
                movies.map { if (it.id == movie.id) it.copy(bookmark = isAdding) else it }
            }
        }
    }

    fun updateScrollState(index: Int, offset: Int) {
        _scrollState.value = Pair(index, offset)
    }
}