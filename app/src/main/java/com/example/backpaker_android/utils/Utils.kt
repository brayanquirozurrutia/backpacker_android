package com.example.backpaker_android.utils

import android.util.Patterns

object Utils {
    fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}