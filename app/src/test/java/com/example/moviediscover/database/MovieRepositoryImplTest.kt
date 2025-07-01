package com.example.moviediscover.database

import com.example.moviediscover.data.Movie
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class MovieRepositoryImplTest {

    private lateinit var repository: MovieRepositoryImpl
    private val movieDao: MovieDao = mockk()
    private val testMovie = Movie(id = 1, title = "Inception")

    @Before
    fun setUp() {
        repository = MovieRepositoryImpl(movieDao)
    }

    @Test
    fun insert_success_returnsTrue() = runTest {
        every { movieDao.insert(any()) } returns 1L

        val result = repository.insert(testMovie)

        assertTrue(result)
        verify { movieDao.insert(testMovie) }
    }

    @Test
    fun insert_exception_returnsFalse() = runTest {
        every { movieDao.insert(any()) } throws Exception("DB error")

        val result = repository.insert(testMovie)

        assertFalse(result)
        verify { movieDao.insert(testMovie) }
    }

    @Test
    fun delete_success_returnsTrue() = runTest {
        every { movieDao.delete(any()) } returns 1

        val result = repository.delete(testMovie)

        assertTrue(result)
        verify { movieDao.delete(testMovie) }
    }

    @Test
    fun delete_exception_returnsFalse() = runTest {
        every { movieDao.delete(any()) } throws Exception("Delete error")

        val result = repository.delete(testMovie)

        assertFalse(result)
        verify { movieDao.delete(testMovie) }
    }

    @Test
    fun getBookmarkMovieList_returnsFlow() = runTest {
        val flow = flowOf(listOf(testMovie))
        every { movieDao.getAll() } returns flow

        val result = repository.getBookmarkMovieList().first()

        assertEquals(listOf(testMovie), result)
        verify { movieDao.getAll() }
    }

    @Test
    fun getBookmarkMovieIdList_returnsFlow() = runTest {
        val flow = flowOf(listOf(1))
        every { movieDao.getAllId() } returns flow

        val result = repository.getBookmarkMovieIdList().first()

        assertEquals(listOf(1), result)
        verify { movieDao.getAllId() }
    }
}