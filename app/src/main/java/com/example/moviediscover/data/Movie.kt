package com.example.moviediscover.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Movie(
    @PrimaryKey val id: Int = 0,
    @ColumnInfo(name = "poster_path") val posterPath: String? = null,
    @ColumnInfo(name = "title") val title: String = "",
    @ColumnInfo(name = "vote") val vote: Double = 0.0,
    @ColumnInfo(name = "bookmark") val bookmark: Boolean = false,
    @ColumnInfo(name = "genres") val genres: String = "",
    @ColumnInfo(name = "release_date") val releaseDate: String = "",
    @ColumnInfo(name = "overview") val overview: String = "",
)
// --- CodeQL 測試用錯誤觸發類別 ---
class CodeQLTestTrigger {

    fun testNPE() {
        val s: String? = null
        println(s!!.length) // 潛在 NullPointerException
    }

    fun deadCode() {
        if (true) return
        println("Unreachable!") // 永遠不會被執行
    }

    fun hardcodedSecret() {
        val apiKey = "sk-FAKE-1234567890abcdefg" // 模擬硬編碼敏感資訊
    }
}
