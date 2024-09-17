package com.example.backpaker_android.network.auth

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val success: Boolean,
    val message: String? = null
)