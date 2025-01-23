package com.example.moviediscover.network

import com.example.moviediscover.data.Movie
import com.example.moviediscover.data.MovieListType
import com.example.moviediscover.network.NetworkConst.Companion.API_BASE
import com.example.moviediscover.network.NetworkConst.Companion.API_CATEGORY_MOVIE
import com.example.moviediscover.network.NetworkConst.Companion.API_CATEGORY_SEARCH_MOVIE
import com.example.moviediscover.network.NetworkConst.Companion.TOKEN
import com.google.gson.Gson
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpHeaders

object NetworkClient {
    private val client = HttpClient {
        expectSuccess = true
    }
    private val gson = Gson()

    suspend fun getMovieList(type: MovieListType, page: Int = 1): ApiOperation<List<Movie>> {
        return safeApiCall {
            val response: HttpResponse = client.get(API_BASE + API_CATEGORY_MOVIE + type.endpoint) {
                headers {
                    append(HttpHeaders.Accept, "application/json")
                    append(HttpHeaders.Authorization, TOKEN)
                }
                url {
                    parameters.append("page", page.toString())
                }
            }
            val responseString = response.body<String>()
            val movieListResponse = gson.fromJson(responseString, MovieListResponse::class.java)
            movieListResponse.transformToMovieList()
        }
    }

    suspend fun getMovieDetail(id: Int): ApiOperation<Movie> {
        return safeApiCall {
            val response: HttpResponse =
                client.get("$API_BASE$API_CATEGORY_MOVIE/$id") {
                    headers {
                        append(HttpHeaders.Accept, "application/json")
                        append(HttpHeaders.Authorization, TOKEN)
                    }
                }
            val responseString = response.body<String>()
            val movieResponse = gson.fromJson(responseString, MovieResponse::class.java)
            movieResponse.transformToMovie()
        }
    }

    suspend fun search(query: String, page: Int = 1): ApiOperation<Pair<Int, List<Movie>>> {
        return safeApiCall {
            val response: HttpResponse = client.get(API_BASE + API_CATEGORY_SEARCH_MOVIE) {
                headers {
                    append(HttpHeaders.Accept, "application/json")
                    append(HttpHeaders.Authorization, TOKEN)
                }
                url {
                    parameters.append("query", query)
                    parameters.append("page", page.toString())
                }
            }
            val responseString = response.body<String>()
            val movieListResponse = gson.fromJson(responseString, MovieListResponse::class.java)
            Pair(movieListResponse.total_results, movieListResponse.transformToMovieList())
        }
    }

    private inline fun <T> safeApiCall(apiCall: () -> T): ApiOperation<T> {
        return try {
            ApiOperation.Success(data = apiCall())
        } catch(e: Exception) {
            ApiOperation.Failure(e)
        }
    }
}

sealed interface ApiOperation<T> {
    data class Success<T>(val data: T) : ApiOperation<T>
    data class Failure<T>(val exception: Exception) : ApiOperation<T>

    fun onSuccess(block: (T) -> Unit): ApiOperation<T> {
        if (this is Success) block(data)
        return this
    }

    fun onFailure(block: (Exception) -> Unit): ApiOperation<T> {
        if (this is Failure) block(exception)
        return this
    }
}

fun MovieResponse.transformToMovie(): Movie = Movie(
    id = id,
    title = title,
    vote = vote_average,
    posterPath = if (poster_path == null) null else NetworkConst.API_IMAGE + poster_path,
    genres = genres?.joinToString(", ") { genre -> genre.name } ?: "",
    releaseDate = release_date ?: "",
    overview = overview,
)

fun MovieListResponse.transformToMovieList(): List<Movie> = results.map { it.transformToMovie() }