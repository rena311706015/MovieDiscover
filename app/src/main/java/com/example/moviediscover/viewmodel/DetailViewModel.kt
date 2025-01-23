package com.example.moviediscover.viewmodel

import androidx.lifecycle.viewModelScope
import com.example.moviediscover.data.Movie
import com.example.moviediscover.database.MovieRepository
import com.example.moviediscover.network.NetworkClient
import com.example.moviediscover.network.NetworkStateUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DetailViewModel(repository: MovieRepository) : BaseViewModel(repository) {

    private val _movie = MutableStateFlow<Movie?>(null)
    val movie = _movie.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private var _movieId = -1

    init {
        viewModelScope.launch(Dispatchers.IO) {
            NetworkStateUtil.isConnectedFlow.collect {
                if (it) getMovieDetail(_movieId)
            }
        }
    }

    fun updateMovieId(movieId: Int) {
        if (_movieId == movieId) return

        _movieId = movieId
        getMovieDetail(movieId)
    }

    private fun getMovieDetail(movieId: Int) {
        if (_movieId == -1) return
        if (_isLoading.value) return

        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            NetworkClient.getMovieDetail(movieId)
                .onSuccess { movie ->
                    val movieWithBookmark =
                        if (movie.id in bookmarkMovieIdList) movie.copy(bookmark = true) else movie
                    _movie.value = movieWithBookmark
                    _error.value = null
                }.onFailure {
                    _error.value = it.message
                }
            _isLoading.value = false
        }
    }

    fun updateBookmark(movie: Movie, isAdding: Boolean) {
        super.updateBookmark(movie = movie, isAdding = isAdding) {
            _movie.value = movie.copy(bookmark = isAdding)
        }
    }
}