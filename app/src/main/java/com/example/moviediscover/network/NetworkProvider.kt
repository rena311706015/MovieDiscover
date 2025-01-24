package com.example.moviediscover.network

import com.google.gson.Gson
import io.ktor.client.HttpClient

object NetworkProvider {
    val client = HttpClient {
        expectSuccess = true
    }
    val gson = Gson()
}