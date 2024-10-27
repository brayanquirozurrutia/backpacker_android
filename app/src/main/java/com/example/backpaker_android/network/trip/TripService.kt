package com.example.backpaker_android.network.trip

import com.example.backpaker_android.network.NetworkService
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
        userId: Int,
        destination: String,
        latitude: Double,
        longitude: Double,
        token: String?
    ): TripResponse {
        return try {
            val request = CreateTripRequest(
                userId = userId,
                destination = destination,
                latitudeRequested = latitude,
                longitudeRequested = longitude
            )

            val response: HttpResponse = client.post("http://10.0.2.2:8080/trip/create") {
                contentType(ContentType.Application.Json)
                headers {
                    token?.let {
                        append(HttpHeaders.Authorization, "Bearer $it")
                    }
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
