package com.example.backpaker_android.network.home

import com.example.backpaker_android.network.NetworkService
import com.example.backpaker_android.network.trip.TripsRequest
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import kotlinx.serialization.json.Json

object HomeService {
    private val client = NetworkService.client
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun getHomeData(lat: Double, lon: Double, radiusKm: Double = 10.0): HomeResponse {
        return try {
            val token = NetworkService.tokenProvider?.invoke()
            val tripsRequest = TripsRequest(lat = lat, lon = lon, radiusKm = radiusKm)

            val response: HttpResponse = client.post("http://10.0.2.2:8080/home/") {
                contentType(ContentType.Application.Json)
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
                setBody(tripsRequest)
            }

            val responseBody = response.bodyAsText()
            json.decodeFromString<HomeResponse>(responseBody)
        } catch (e: Exception) {
            e.printStackTrace()
            HomeResponse(success = false, message = "An error occurred: ${e.message}")
        }
    }

    suspend fun getTripsWithinRadius(lat: Double, lon: Double, radiusKm: Double): List<TripData> {
        return try {
            val token = NetworkService.tokenProvider?.invoke()

            val response: HttpResponse = client.post("http://10.0.2.2:8080/home/trips") {
                contentType(ContentType.Application.Json)
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
                setBody(mapOf(
                    "lat" to lat,
                    "lon" to lon,
                    "radiusKm" to radiusKm
                ))
            }

            val responseBody = response.bodyAsText()
            json.decodeFromString<List<TripData>>(responseBody)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}