package com.example.moviediscover.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.moviediscover.data.Movie
import com.example.moviediscover.data.SortCriteria
import com.example.moviediscover.database.MovieRepository
import com.example.moviediscover.network.NetworkClient
import com.example.moviediscover.network.NetworkStateUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchViewModel(repository: MovieRepository) : BaseViewModel(repository) {

    private val _scrollState = MutableStateFlow(Pair(0, 0))
    val scrollState = _scrollState.asStateFlow()

    private val _searchString = MutableStateFlow("")
    val searchString = _searchString.asStateFlow()

    private val _searchResult = MutableStateFlow(listOf<Movie>())
    val searchResult = _searchResult.asStateFlow()

    private val _totalResult = MutableStateFlow(0)
    val totalResult = _totalResult.asStateFlow()

    private val _sortCriteria = MutableStateFlow(SortCriteria.Rating)
    val sortCriteria = _sortCriteria.asStateFlow()

    private val _showSortCriteria = MutableStateFlow(false)
    val showSortCriteria = _showSortCriteria.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private var _isNetworkConnected = true
    private var _currentPage = 1

    init {
        viewModelScope.launch(Dispatchers.IO) {
            NetworkStateUtil.isConnectedFlow.collect {
                _isNetworkConnected = it
                if (it && _error.value != null){
                    if(_searchResult.value.isNotEmpty()) search()
                    resetError()
                }
            }
        }
    }

    fun search(isLoadMore: Boolean = false) {
        if (_searchString.value.isEmpty()) return
        if (_isLoading.value) return
        if (!_isNetworkConnected) {
            _error.value = "Please connect to the internet first."
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            NetworkClient
                .search(_searchString.value, _currentPage)
                .onSuccess { (total, movies) ->
                    _totalResult.value = total
                    val movieListWithBookmark = movies
                        .map { if (it.id in bookmarkMovieIdList) it.copy(bookmark = true) else it }
                        .sortedByDescending { it.vote }
                    if(isLoadMore){
                        _searchResult.value += movieListWithBookmark
                    }else{
                        _searchResult.value = movieListWithBookmark
                    }
                    _currentPage += 1
                }.onFailure {
                    _error.value = it.message
                }
            _isLoading.value = false
        }
    }

    fun onSearchTextChange(text: String) {
        _searchString.value = text
    }

    fun updateSortCriteria(sortCriteria: SortCriteria) {
        _searchResult.value = when (sortCriteria) {
            SortCriteria.NAME -> _searchResult.value.sortedBy { it.title }
            SortCriteria.Rating -> _searchResult.value.sortedByDescending { it.vote }
            SortCriteria.DATE_LATEST -> _searchResult.value.sortedByDescending { it.releaseDate }
            SortCriteria.DATE_OLDEST -> _searchResult.value.sortedBy { it.releaseDate }
        }
        _sortCriteria.value = sortCriteria
    }

    fun updateBookmark(movie: Movie, isAdding: Boolean) {
        super.updateBookmark(movie = movie, isAdding = isAdding) {
            _searchResult.update { movies ->
                movies.map { if (it.id == movie.id) it.copy(bookmark = isAdding) else it }
            }
        }
    }

    fun updateScrollState(index: Int, offset: Int) {
        _scrollState.value = Pair(index, offset)
    }

    fun toggleShowSortCriteria() {
        _showSortCriteria.value = !_showSortCriteria.value
    }

    fun resetError() {
        _error.value = null
    }
}