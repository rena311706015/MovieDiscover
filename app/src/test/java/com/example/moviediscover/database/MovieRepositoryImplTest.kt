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

    // runTest { ... }：因為裡面有 suspend 函數（使用了 Flow），所以用 runTest 包起來
    @Test
    fun insert_success_returnsTrue() = runTest {
        println("Running insert_success_returnsTrue()...")
        // 先宣告只要有人呼叫 insert，無論傳入什麼，都會回傳 1L
        every { movieDao.insert(any()) } returns 1L
        // 傳入測試用的隨便一個 movie
        // 因為 repository.insert 會呼叫到 dao 的 insert，而我們剛剛指定他會正常回傳一個 Long
        // 所以這個測試應該要回傳 true
        val result = repository.insert(testMovie)
        // 檢查回傳值如果是 true 就成功，不是的話這個測試就失敗
        println("result = $result")
        assertTrue(result)
        // 檢查 movieDao.insert() 是否被呼叫過
        // verify 是 mockk 提供的方法，用來檢查寫的程式有沒有呼叫到某個函數
        verify { movieDao.insert(testMovie) }
    }

    @Test
    fun insert_exception_returnsFalse() = runTest {
        println("Running insert_exception_returnsFalse()...")
        every { movieDao.insert(any()) } throws Exception("DB error")

        val result = repository.insert(testMovie)

        println("result = $result")
        assertFalse(result)
        verify { movieDao.insert(testMovie) }
    }

    @Test
    fun delete_success_returnsTrue() = runTest {
        println("Running delete_success_returnsTrue()...")
        every { movieDao.delete(any()) } returns 1

        val result = repository.delete(testMovie)

        println("result = $result")
        assertTrue(result)
        verify { movieDao.delete(testMovie) }
    }

    @Test
    fun delete_exception_returnsFalse() = runTest {
        println("Running delete_exception_returnsFalse()...")
        every { movieDao.delete(any()) } throws Exception("Delete error")

        val result = repository.delete(testMovie)

        println("result = $result")
        assertFalse(result)
        verify { movieDao.delete(testMovie) }
    }

    @Test
    fun getBookmarkMovieList_returnsFlow() = runTest {
        println("Running getBookmarkMovieList_returnsFlow()...")
        val flow = flowOf(listOf(testMovie))
        every { movieDao.getAll() } returns flow

        val result = repository.getBookmarkMovieList().first()

        println("result = $result")
        println("listOf(testMovie) = ${listOf(testMovie)}")
        assertEquals(listOf(testMovie), result)
        verify { movieDao.getAll() }
    }

    @Test
    fun getBookmarkMovieIdList_returnsFlow() = runTest {
        println("Running getBookmarkMovieIdList_returnsFlow()...")
        val flow = flowOf(listOf(1))
        every { movieDao.getAllId() } returns flow

        val result = repository.getBookmarkMovieIdList().first()

        println("result = $result")
        println("listOf(1) = ${listOf(1)}")
        assertEquals(listOf(1), result)
        verify { movieDao.getAllId() }
    }
}