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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(repository: MovieRepository) : BaseViewModel(repository) {

    private val _movieLists = MutableStateFlow(
        mapOf(
            MovieListType.NOW_PLAYING to emptyList<Movie>(),
            MovieListType.POPULAR to emptyList<Movie>(),
            MovieListType.UPCOMING to emptyList<Movie>()
        )
    )
    val movieLists = _movieLists.asStateFlow()

    private val _movieListsScrollState = MutableStateFlow(
        mapOf(
            MovieListType.NOW_PLAYING to Pair(0, 0),
            MovieListType.POPULAR to Pair(0, 0),
            MovieListType.UPCOMING to Pair(0, 0)
        )
    )
    val movieListsScrollState = _movieListsScrollState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            getMovieList(MovieListType.NOW_PLAYING)
            getMovieList(MovieListType.POPULAR)
            getMovieList(MovieListType.UPCOMING)
            _isLoading.value = false
        }
    }

    private suspend fun getMovieList(type: MovieListType) {
        val response: HttpResponse =
            NetworkProvider.client.get(API_BASE + API_CATEGORY_MOVIE + type.endpoint) {
                headers {
                    append(HttpHeaders.Accept, "application/json")
                    append(HttpHeaders.Authorization, TOKEN)
                }
            }
        val responseString = response.body<String>()
        val movieListResponse =
            NetworkProvider.gson.fromJson(responseString, MovieListResponse::class.java)
        val originMovieList = movieListResponse.transformToMovieList()
        val movieListWithBookmark =
            originMovieList.map { if (it.id in bookmarkMovieIdList) it.copy(bookmark = true) else it }
        _movieLists.value = _movieLists.value.toMutableMap().apply {
            this[type] = movieListWithBookmark
        }
    }

    fun updateBookmark(movie: Movie, isAdding: Boolean) {
        super.updateBookmark(movie = movie, isAdding = isAdding) {
            val updatedMovieLists = _movieLists.value.toMutableMap().apply {
                for (key in keys) {
                    this[key] =
                        this[key]!!.map { if (it.id == movie.id) it.copy(bookmark = isAdding) else it }
                }
            }
            _movieLists.value = updatedMovieLists
        }
    }

    fun updateScrollState(type: MovieListType, index: Int, offset: Int) {
        _movieListsScrollState.value = _movieListsScrollState.value.toMutableMap().apply {
            this[type] = Pair(index, offset)
        }
    }
}