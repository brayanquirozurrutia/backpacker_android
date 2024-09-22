package com.example.backpaker_android.network.auth

import android.content.Context
import android.content.SharedPreferences
import kotlinx.serialization.json.Json

object TokenService {
    private const val PREFS_NAME = "AuthPrefs"
    private const val TOKEN_KEY = "token"

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    private lateinit var appContext: Context

    private fun getSharedPreferences(): SharedPreferences {
        return appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveToken(token: String) {
        with(getSharedPreferences().edit()) {
            putString(TOKEN_KEY, token)
            apply()
        }
    }

    fun getToken(): String? {
        return getSharedPreferences().getString(TOKEN_KEY, null)
    }

    fun clearToken() {
        with(getSharedPreferences().edit()) {
            remove(TOKEN_KEY)
            apply()
        }
    }
}
