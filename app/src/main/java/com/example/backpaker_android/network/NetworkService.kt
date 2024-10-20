package com.example.backpaker_android.network

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*

object NetworkService {
    var tokenProvider: (() -> String?)? = null

    fun initialize(tokenProvider: () -> String?) {
        this.tokenProvider = tokenProvider
    }

    val client: HttpClient by lazy {
        HttpClient(CIO) {
            install(ContentNegotiation) {
                json()
            }
            install(Logging) {
                level = LogLevel.ALL
            }
            HttpResponseValidator {
                validateResponse { response ->
                    val statusCode = response.status.value
                    if (statusCode >= 400) {
                        throw ResponseException(response, "HTTP error with status code $statusCode")
                    }
                }
            }
        }
    }
}