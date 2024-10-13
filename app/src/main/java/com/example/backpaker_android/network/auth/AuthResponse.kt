package com.example.backpaker_android.network.auth

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val success: Boolean,
    val message: String
)

@Serializable
data class LoginResponse(
    val success: Boolean,
    val message: String,
    val token: String = "",
    val isActive: Boolean? = null
)