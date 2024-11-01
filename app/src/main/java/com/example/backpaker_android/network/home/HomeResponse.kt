package com.example.backpaker_android.network.home

import kotlinx.serialization.Serializable

@Serializable
data class HomeResponse(
    val success: Boolean,
    val trips: List<TripData> = emptyList(),
    val message: String? = null,
)

@Serializable
data class TripData(
    val id: Int,
    val userId: Int,
    val destination: String,
    val latitude: Double,
    val longitude: Double,
    val status: String
)