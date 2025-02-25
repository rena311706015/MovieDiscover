package com.example.moviediscover.viewmodel

import androidx.lifecycle.viewModelScope
import com.example.moviediscover.data.Movie
import com.example.moviediscover.data.SortCriteria
import com.example.moviediscover.database.MovieRepository
import com.example.moviediscover.network.MovieListResponse
import com.example.moviediscover.network.NetworkConst.Companion.API_BASE
import com.example.moviediscover.network.NetworkConst.Companion.API_CATEGORY_SEARCH_MOVIE
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

    private var currentPage = 1

    fun search() {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            val response: HttpResponse =
                NetworkProvider.client.get(API_BASE + API_CATEGORY_SEARCH_MOVIE) {
                    headers {
                        append(HttpHeaders.Accept, "application/json")
                        append(HttpHeaders.Authorization, TOKEN)
                    }
                    url {
                        parameters.append("query", _searchString.value)
                        parameters.append("page", currentPage.toString())
                    }
                }
            val responseString = response.body<String>()
            val movieListResponse =
                NetworkProvider.gson.fromJson(responseString, MovieListResponse::class.java)
            val originMovieList = movieListResponse.transformToMovieList()
            val movieListWithBookmark = originMovieList
                .map { if (it.id in bookmarkMovieIdList) it.copy(bookmark = true) else it }
                .sortedByDescending { it.vote }
            currentPage += 1
            _totalResult.value = movieListResponse.total_results
            _searchResult.value += movieListWithBookmark
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
}