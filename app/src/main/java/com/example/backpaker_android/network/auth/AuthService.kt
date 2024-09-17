package com.example.backpaker_android.network.auth

import com.example.backpaker_android.network.NetworkService
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.Json

object AuthService {
    private val client = NetworkService.client
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun login(email: String, password: String): LoginResponse {
        return try {
            val response: HttpResponse = client.post("http://10.0.2.2:8080/auth/login") {
                contentType(ContentType.Application.Json)
                setBody(mapOf("email" to email, "password" to password))
            }

            val responseBody = response.bodyAsText()

            json.decodeFromString<LoginResponse>(responseBody)
        } catch (e: Exception) {
            e.printStackTrace()
            println("An error occurred: ${e.message}")
            LoginResponse(success = false, message = "An error occurred: ${e.message}")
        }
    }
}
