package com.example.backpaker_android.network.trip

import android.content.Context
import com.example.backpaker_android.network.NetworkService
import com.example.backpaker_android.utils.SessionManager
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import kotlinx.serialization.json.Json


object TripService {
    private val client = NetworkService.client
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun sendTrip(
        destination: String,
        latitude: Double,
        longitude: Double,
        context: Context
    ): TripResponse {
        return try {
            val token = NetworkService.tokenProvider?.invoke()
            val userId = SessionManager.getUserId(context)
                ?: return TripResponse(success = false, message = "User ID not found")

            val request = CreateTripRequest(
                userId = userId,
                destination = destination,
                latitudeRequested = latitude,
                longitudeRequested = longitude
            )

            val response: HttpResponse = client.post("http://10.0.2.2:8080/trip/create") {
                contentType(ContentType.Application.Json)
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
                setBody(request)
            }

            val responseBody = response.bodyAsText()
            json.decodeFromString<TripResponse>(responseBody)
        } catch (e: Exception) {
            e.printStackTrace()
            TripResponse(success = false, message = "An error occurred: ${e.message}")
        }
    }
}
