package com.example.moviediscover.database

import androidx.annotation.WorkerThread
import com.example.moviediscover.data.Movie
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    suspend fun getBookmarkMovieList(): Flow<List<Movie>>
    suspend fun getBookmarkMovieIdList(): Flow<List<Int>>
    suspend fun insert(movie: Movie): Boolean
    suspend fun delete(movie: Movie): Boolean
}

class MovieRepositoryImpl(private val movieDao: MovieDao) : MovieRepository {

    @WorkerThread
    override suspend fun getBookmarkMovieList(): Flow<List<Movie>> = movieDao.getAll()

    @WorkerThread
    override suspend fun getBookmarkMovieIdList(): Flow<List<Int>> = movieDao.getAllId()

    @WorkerThread
    override suspend fun insert(movie: Movie): Boolean {
        return try {
            movieDao.insert(movie)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    @WorkerThread
    override suspend fun delete(movie: Movie): Boolean {
        return try {
            movieDao.delete(movie)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}