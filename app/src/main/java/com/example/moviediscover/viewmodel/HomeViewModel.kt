package com.example.moviediscover.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.moviediscover.data.Movie
import com.example.moviediscover.data.MovieListType
import com.example.moviediscover.database.MovieRepository
import com.example.moviediscover.network.NetworkClient
import com.example.moviediscover.network.NetworkStateUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.collections.emptyList
import kotlin.collections.map
import kotlin.collections.mapOf
import kotlin.collections.set
import kotlin.collections.toMutableMap

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

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            NetworkStateUtil.isConnectedFlow.collect {
                if (it) {
                    _isLoading.value = true
                    getMovieList(MovieListType.NOW_PLAYING)
                    getMovieList(MovieListType.POPULAR)
                    getMovieList(MovieListType.UPCOMING)
                    _isLoading.value = false
                }
            }
        }
    }

    private suspend fun getMovieList(type: MovieListType) {
        NetworkClient
            .getMovieList(type)
            .onSuccess { movieList ->
                val movieListWithBookmark =
                    movieList.map { if (it.id in bookmarkMovieIdList) it.copy(bookmark = true) else it }
                _movieLists.value = _movieLists.value.toMutableMap().apply {
                    this[type] = movieListWithBookmark
                }
            }
            .onFailure {
                _error.value = it.message
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

    fun resetError() {
        _error.value = null
    }
}