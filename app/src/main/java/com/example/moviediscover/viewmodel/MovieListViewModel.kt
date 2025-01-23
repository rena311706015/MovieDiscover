package com.example.moviediscover.viewmodel

import androidx.lifecycle.viewModelScope
import com.example.moviediscover.data.Movie
import com.example.moviediscover.data.MovieListType
import com.example.moviediscover.database.MovieRepository
import com.example.moviediscover.network.NetworkClient
import com.example.moviediscover.network.NetworkStateUtil
import kotlinx.coroutines.Dispatchers
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

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private var _currentPage = 1
    private var _movieListType: MovieListType? = null

    init {
        viewModelScope.launch(Dispatchers.IO) {
            NetworkStateUtil.isConnectedFlow.collect { isConnected ->
                if (isConnected) _movieListType?.let { it1 -> getMovieList(it1) }
            }
        }
    }

    fun getMovieList(type: MovieListType) {
        if (_isLoading.value) return

        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            NetworkClient
                .getMovieList(type, _currentPage)
                .onSuccess { movieList ->
                    val movieListWithBookmark =
                        movieList.map { if (it.id in bookmarkMovieIdList) it.copy(bookmark = true) else it }
                    _currentPage += 1
                    _movieList.value += movieListWithBookmark
                }.onFailure {
                    _error.value = it.message
                }
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

    fun updateMovieListType(type: MovieListType) {
        if (type == _movieListType) return

        _movieListType = type
        getMovieList(type)
    }

    fun resetError() {
        _error.value = null
    }
}