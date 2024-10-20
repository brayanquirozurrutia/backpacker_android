package com.example.backpaker_android.network.trip

import kotlinx.serialization.Serializable

@Serializable
data class TripResponse(
    val success: Boolean,
    val message: String?,
)