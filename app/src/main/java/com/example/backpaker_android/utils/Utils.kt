package com.example.backpaker_android.utils

import android.content.Context
import android.util.Patterns
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

object Utils {
    fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isValidToken(token: String): Boolean {
        return token.matches(Regex("^\\d{6}$"))
    }
}

object SessionManager {
    private const val FILE_NAME = "encrypted_session_preferences"
    private const val USER_EMAIL_KEY = "user_email"
    private const val TOKEN_KEY = "access_token"

    @Volatile
    private var inMemoryToken: String? = null

    private fun getEncryptedPreferences(context: Context) =
        EncryptedSharedPreferences.create(
            FILE_NAME,
            MasterKey.DEFAULT_MASTER_KEY_ALIAS,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

    fun setUserEmail(context: Context, email: String?) {
        val prefs = getEncryptedPreferences(context).edit()
        if (email == null) {
            prefs.remove(USER_EMAIL_KEY)
        } else {
            prefs.putString(USER_EMAIL_KEY, email)
        }
        prefs.apply()
    }

    fun getUserEmail(context: Context): Flow<String?> = flow {
        val prefs = getEncryptedPreferences(context)
        emit(prefs.getString(USER_EMAIL_KEY, null))
    }

    fun setAccessToken(context: Context, token: String?) {
        val prefs = getEncryptedPreferences(context).edit()
        if (token == null) {
            prefs.remove(TOKEN_KEY)
        } else {
            prefs.putString(TOKEN_KEY, token)
            println("Token almacenado: $token")
        }
        prefs.apply()
        inMemoryToken = token
    }

    fun getAccessToken(context: Context): String? {
        return inMemoryToken ?: getEncryptedPreferences(context).getString(TOKEN_KEY, null).also {
            inMemoryToken = it
            println("Token recuperado: $it")
        }
    }

    fun getCurrentToken(): String? = inMemoryToken

    fun clearAccessToken(context: Context) {
        setAccessToken(context, null)
    }
}