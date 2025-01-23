package com.example.moviediscover.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviediscover.data.Movie
import com.example.moviediscover.data.SortCriteria
import com.example.moviediscover.network.MovieApiResponse
import com.example.moviediscover.network.NetworkConst.Companion.API_BASE
import com.example.moviediscover.network.NetworkConst.Companion.API_CATEGORY_SEARCH_MOVIE
import com.example.moviediscover.network.NetworkConst.Companion.TOKEN
import com.google.gson.Gson
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchViewModel : ViewModel() {
    private val client = HttpClient {
        expectSuccess = true
    }
    private val gson = Gson()

    private val _searchString = MutableStateFlow("")
    val searchString = _searchString.asStateFlow()

    private val _searchResult = MutableStateFlow(listOf<Movie>())
    val searchResult = _searchResult.asStateFlow()

    private val _sortCriteria = MutableStateFlow(SortCriteria.Rating)
    val sortCriteria = _sortCriteria.asStateFlow()

    private val _showSortCriteria = MutableStateFlow(false)
    val showSortCriteria = _showSortCriteria.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    fun search() {
        _loading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            val response: HttpResponse = client.get(API_BASE + API_CATEGORY_SEARCH_MOVIE) {
                headers {
                    append(HttpHeaders.Accept, "application/json")
                    append(HttpHeaders.Authorization, TOKEN)
                }
                url {
                    parameters.append("query", _searchString.value)
                }
            }
            val responseString = response.body<String>()
            val movieApiResponse = gson.fromJson(responseString, MovieApiResponse::class.java)
            withContext(Dispatchers.Default) {
                _searchResult.value =
                    movieApiResponse.getMutableMovieList().sortedByDescending { it.vote }
                _loading.value = false
            }
        }
    }

    fun onSearchTextChange(text: String) {
        _searchString.value = text
    }

    fun updateSortCriteria(sortCriteria: SortCriteria) {
        _searchResult.value = when (sortCriteria) {
            SortCriteria.NAME -> _searchResult.value.sortedByDescending { it.title }
            SortCriteria.Rating -> _searchResult.value.sortedByDescending { it.vote }
            SortCriteria.DATE_LATEST -> _searchResult.value.sortedByDescending { it.releaseDate }
            SortCriteria.DATE_OLDEST -> _searchResult.value.sortedBy { it.releaseDate }
        }
        _sortCriteria.value = sortCriteria
    }

    fun updateShowSortCriteria() {
        _showSortCriteria.value = !_showSortCriteria.value
    }
}