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
            LoginResponse(success = false, message = "An error occurred: ${e.message}", token = null.toString())
        }
    }

    suspend fun register(
        firstName: String,
        lastName: String,
        email: String,
        birthDate: String,
        gender: String,
        password: String,
        confirmPassword: String
    ): AuthResponse {
        return try {
            val response: HttpResponse = client.post("http://10.0.2.2:8080/auth/register") {
                contentType(ContentType.Application.Json)
                setBody(
                    mapOf(
                        "firstName" to firstName,
                        "lastName" to lastName,
                        "email" to email,
                        "birthDate" to birthDate,
                        "gender" to gender,
                        "password" to password,
                        "confirmPassword" to confirmPassword
                    )
                )
            }

            val responseBody = response.bodyAsText()

            json.decodeFromString<AuthResponse>(responseBody)
        } catch (e: Exception) {
            e.printStackTrace()
            println("An error occurred: ${e.message}")
            AuthResponse(success = false, message = "An error occurred: ${e.message}")
        }
    }

    suspend fun activateAccount(email: String, token: String): AuthResponse {
        return try {
            val response: HttpResponse = client.post("http://10.0.2.2:8080/auth/activate-account") {
                contentType(ContentType.Application.Json)
                setBody(mapOf("email" to email, "token" to token))
            }

            val responseBody = response.bodyAsText()
            json.decodeFromString<AuthResponse>(responseBody)
        } catch (e: Exception) {
            e.printStackTrace()
            AuthResponse(success = false, message = "Error: ${e.message}")
        }
    }

    suspend fun resendToken(email: String, tokenType: String): AuthResponse {
        return try {
            val response: HttpResponse = client.post("http://10.0.2.2:8080/auth/resend-token") {
                contentType(ContentType.Application.Json)
                setBody(
                    mapOf(
                        "email" to email,
                        "tokenType" to tokenType
                    )
                )
            }

            val responseBody = response.bodyAsText()
            json.decodeFromString<AuthResponse>(responseBody)
        } catch (e: Exception) {
            e.printStackTrace()
            AuthResponse(success = false, message = "Error: ${e.message}")
        }
    }

    suspend fun forgotPassword(email: String): AuthResponse {
        return try {
            val response: HttpResponse = client.post("http://10.0.2.2:8080/auth/forgot-password") {
                contentType(ContentType.Application.Json)
                setBody(mapOf("email" to email))
            }

            val responseBody = response.bodyAsText()
            json.decodeFromString<AuthResponse>(responseBody)
        } catch (e: Exception) {
            e.printStackTrace()
            AuthResponse(success = false, message = "An error occurred: ${e.message}")
        }
    }

    suspend fun resetPassword(email: String, token: String, password: String, confirmPassword: String): AuthResponse {
        return try {
            val response: HttpResponse = client.post("http://10.0.2.2:8080/auth/reset-password") {
                contentType(ContentType.Application.Json)
                setBody(
                    mapOf(
                        "email" to email,
                        "token" to token,
                        "password" to password,
                        "confirmPassword" to confirmPassword
                    )
                )
            }

            val responseBody = response.bodyAsText()
            json.decodeFromString(AuthResponse.serializer(), responseBody)
        } catch (e: Exception) {
            e.printStackTrace()
            AuthResponse(success = false, message = "Error: ${e.message}")
        }
    }
}