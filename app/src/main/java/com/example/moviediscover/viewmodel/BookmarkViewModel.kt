package com.example.moviediscover.viewmodel

import androidx.lifecycle.viewModelScope
import com.example.moviediscover.data.Movie
import com.example.moviediscover.database.MovieRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BookmarkViewModel(repository: MovieRepository) : BaseViewModel(repository) {

    private val _scrollState = MutableStateFlow(Pair(0, 0))
    val scrollState = _scrollState.asStateFlow()

    private val _bookmarkMovieList = MutableStateFlow(listOf<Movie>())
    val bookmarkMovieList = _bookmarkMovieList.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getBookmarkMovieList().collect {
                _bookmarkMovieList.value = it
            }
        }
    }

    fun updateBookmark(movie: Movie, isAdding: Boolean) {
        super.updateBookmark(movie = movie, isAdding = isAdding) {/* no-op */ }
    }

    fun updateScrollState(index: Int, offset: Int) {
        _scrollState.value = Pair(index, offset)
    }
}