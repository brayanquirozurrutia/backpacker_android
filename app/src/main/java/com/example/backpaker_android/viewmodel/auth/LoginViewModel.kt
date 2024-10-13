package com.example.backpaker_android.viewmodel.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.example.backpaker_android.network.auth.AuthService
import com.example.backpaker_android.network.auth.LoginResponse
import com.example.backpaker_android.utils.SessionManager
import com.example.backpaker_android.utils.Utils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _emailError = MutableStateFlow<String?>(null)
    val emailError: StateFlow<String?> = _emailError

    private val _passwordError = MutableStateFlow<String?>(null)
    val passwordError: StateFlow<String?> = _passwordError

    fun onLogin(onLoginSuccess: () -> Unit, onAccountNotActive: () -> Unit) {
        if (validateInputs()) {
            viewModelScope.launch {
                _isLoading.value = true
                _errorMessage.value = null
                try {
                    val response: LoginResponse = AuthService.login(_email.value, _password.value)
                    _isLoading.value = false

                    if (response.success) {
                        SessionManager.setAccessToken(getApplication(), response.token)
                        onLoginSuccess()
                    } else {
                        if (response.isActive == false) {
                            SessionManager.setUserEmail(getApplication(), _email.value)
                            onAccountNotActive()
                        }
                        _errorMessage.value = response.message
                    }
                } catch (e: Exception) {
                    _isLoading.value = false
                    _errorMessage.value = "Ocurrió un error: ${e.message}"
                }
            }
        }
    }

    private fun validateInputs(): Boolean {
        var isValid = true

        if (_email.value.isBlank()) {
            _emailError.value = "El correo no debe estar vacío"
            isValid = false
        } else if (!Utils.isValidEmail(_email.value)) {
            _emailError.value = "El formato del correo no es válido"
            isValid = false
        } else {
            _emailError.value = null
        }

        if (_password.value.isBlank()) {
            _passwordError.value = "La contraseña no debe estar vacía"
            isValid = false
        } else {
            _passwordError.value = null
        }

        return isValid
    }

    fun onEmailChanged(newEmail: String) {
        _email.value = newEmail
    }

    fun onPasswordChanged(newPassword: String) {
        _password.value = newPassword
    }
}
