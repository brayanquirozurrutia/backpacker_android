package com.example.backpaker_android.viewmodel.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.example.backpaker_android.network.auth.AuthService
import com.example.backpaker_android.network.auth.AuthResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LoginViewModel : ViewModel() {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun onLogin(onLoginSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response: AuthResponse = AuthService.login(_email.value, _password.value)
                _isLoading.value = false

                if (response.success) {
                    onLoginSuccess()
                } else {
                    _errorMessage.value = response.message ?: "Login failed"
                }
            } catch (e: Exception) {
                _isLoading.value = false
                _errorMessage.value = "An error occurred: ${e.message}"
            }
        }
    }

    fun onEmailChanged(newEmail: String) {
        _email.value = newEmail
    }

    fun onPasswordChanged(newPassword: String) {
        _password.value = newPassword
    }
}
