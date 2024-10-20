package com.example.backpaker_android.network.home

import com.example.backpaker_android.network.NetworkService
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import kotlinx.serialization.json.Json

object HomeService {
    private val client = NetworkService.client
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun getHomeData(): HomeResponse {
        return try {

            val token = NetworkService.tokenProvider?.invoke()

            val response: HttpResponse = client.post("http://10.0.2.2:8080/home/") {
                contentType(ContentType.Application.Json)
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
            }

            val responseBody = response.bodyAsText()
            json.decodeFromString<HomeResponse>(responseBody)
        } catch (e: Exception) {
            e.printStackTrace()
            HomeResponse(success = false, message = "An error occurred: ${e.message}")
        }
    }
}