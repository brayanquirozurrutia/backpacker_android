package com.example.backpaker_android.viewmodel.auth

import androidx.lifecycle.AndroidViewModel
import android.app.Application
import androidx.lifecycle.viewModelScope
import com.example.backpaker_android.network.auth.AuthService
import com.example.backpaker_android.network.auth.AuthResponse
import com.example.backpaker_android.user.TokenType
import com.example.backpaker_android.utils.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

class ActivateAccountViewModel(application: Application) : AndroidViewModel(application) {

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _tokenErrorMessage = MutableStateFlow<String?>(null)
    val tokenErrorMessage: StateFlow<String?> = _tokenErrorMessage

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val userEmail: StateFlow<String?> = SessionManager.getUserEmail(application)
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private val _resendMessageError = MutableStateFlow<String?>(null)
    val resendMessageError: StateFlow<String?> = _resendMessageError

    private val _isResending = MutableStateFlow(false)
    val isResending: StateFlow<Boolean> = _isResending

    private val _resendCooldown = MutableStateFlow(0)
    val resendCooldown: StateFlow<Int> = _resendCooldown

    fun activateAccount(token: String, onSuccess: () -> Unit) {
        val email = userEmail.value

        _errorMessage.value = null
        _tokenErrorMessage.value = null

        if (token.isBlank()) {
            _tokenErrorMessage.value = "El token no puede estar vacío."
            return
        }

        if (token.length < 6) {
            _tokenErrorMessage.value = "El token debe tener al menos 6 caracteres."
            return
        }

        if (email.isNullOrEmpty()) {
            _errorMessage.value = "El correo electrónico no está disponible."
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response: AuthResponse = AuthService.activateAccount(email, token)
                if (response.success) {
                    SessionManager.setUserEmail(getApplication(), null)
                    onSuccess()
                } else {
                    _errorMessage.value = response.message
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resendToken(tokenType: TokenType, onSuccess: () -> Unit) {
        val email = userEmail.value

        _resendMessageError.value = null

        if (email.isNullOrEmpty()) {
            _resendMessageError.value = "El correo electrónico no está disponible."
            return
        }

        viewModelScope.launch {
            _isResending.value = true
            try {
                val response = AuthService.resendToken(email, tokenType.name)
                if (response.success) {
                    onSuccess()
                    _resendCooldown.value = 60
                    for (i in 60 downTo 1) {
                        _resendCooldown.value = i
                        delay(1000)
                    }
                    _resendCooldown.value = 0
                } else {
                    _resendMessageError.value = response.message
                }
            } catch (e: Exception) {
                _resendMessageError.value = "Error: ${e.message}"
            } finally {
                _isResending.value = false
            }
        }
    }
}
