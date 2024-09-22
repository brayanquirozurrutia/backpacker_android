package com.example.backpaker_android.network.home

import kotlinx.serialization.Serializable

@Serializable
data class HomeResponse(
    val success: Boolean,
    val message: String?,
)