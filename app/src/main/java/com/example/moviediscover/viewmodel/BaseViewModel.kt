package com.example.moviediscover.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviediscover.data.Movie
import com.example.moviediscover.data.ToastState
import com.example.moviediscover.database.MovieRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

open class BaseViewModel(private val repository: MovieRepository) : ViewModel() {

    private val _toastState = MutableStateFlow<ToastState>(ToastState.Hidden)
    val toastState = _toastState.asStateFlow()

    protected lateinit var bookmarkMovieIdList: List<Int>

    init {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getBookmarkMovieIdList().collect {
                bookmarkMovieIdList = it
            }
        }
    }

    fun updateBookmark(movie: Movie, isAdding: Boolean, updateResult: (Movie) -> Unit) {
        _toastState.value = ToastState.Hidden
        viewModelScope.launch(Dispatchers.IO) {
            val updatedMovie = movie.copy(bookmark = isAdding)
            val result = if (isAdding) repository.insert(updatedMovie) else repository.delete(movie)

            if (result) {
                updateResult(updatedMovie)
                _toastState.value =
                    if (isAdding) ToastState.AddSuccess(movie.id) else ToastState.RemoveSuccess(
                        movie.id
                    )
            } else {
                _toastState.value =
                    if (isAdding) ToastState.AddFailed(movie.id) else ToastState.RemoveFailed(movie.id)
            }
        }
    }

    fun resetToastState() {
        _toastState.value = ToastState.Hidden
    }
}