package com.example.moviediscover.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.moviediscover.data.Movie
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {
    @Query("SELECT * FROM movie")
    fun getAll(): Flow<List<Movie>>

    @Query("SELECT id FROM movie")
    fun getAllId(): Flow<List<Int>>

    @Insert
    fun insert(movie: Movie): Long

    @Delete
    fun delete(movie: Movie): Int
}