package com.example.backpaker_android.viewmodel.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.backpaker_android.network.auth.AuthService
import com.example.backpaker_android.network.auth.AuthResponse
import com.example.backpaker_android.user.TokenType
import com.example.backpaker_android.utils.Utils
import com.example.backpaker_android.utils.Utils.isValidToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ForgotPasswordViewModel : ViewModel() {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _token = MutableStateFlow("")
    val token: StateFlow<String> = _token

    private val _newPassword = MutableStateFlow("")
    val newPassword: StateFlow<String> = _newPassword

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword: StateFlow<String> = _confirmPassword

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _emailError = MutableStateFlow<String?>(null)
    val emailError: StateFlow<String?> = _emailError

    private val _tokenError = MutableStateFlow<String?>(null)
    val tokenError: StateFlow<String?> = _tokenError

    private val _newPasswordError = MutableStateFlow<String?>(null)
    val newPasswordError: StateFlow<String?> = _newPasswordError

    private val _confirmPasswordError = MutableStateFlow<String?>(null)
    val confirmPasswordError: StateFlow<String?> = _confirmPasswordError

    private val _step = MutableStateFlow(Step.RequestEmail)
    val step: StateFlow<Step> = _step

    private val _isResending = MutableStateFlow(false)
    val isResending: StateFlow<Boolean> = _isResending

    private val _resendMessageError = MutableStateFlow<String?>(null)
    val resendMessageError: StateFlow<String?> = _resendMessageError

    private val _resendCooldown = MutableStateFlow(0)
    val resendCooldown: StateFlow<Int> = _resendCooldown

    fun onEmailChanged(newEmail: String) {
        _email.value = newEmail
    }

    fun onTokenChanged(newToken: String) {
        _token.value = newToken
    }

    fun onNewPasswordChanged(newPassword: String) {
        _newPassword.value = newPassword
    }

    fun onConfirmPasswordChanged(confirmPassword: String) {
        _confirmPassword.value = confirmPassword
    }

    fun requestPasswordReset() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _emailError.value = null

            if (_email.value.isEmpty()) {
                _emailError.value = "El correo no puede estar vacío"
                _isLoading.value = false
                return@launch
            }

            if (!Utils.isValidEmail(_email.value)) {
                _emailError.value = "El formato del correo no es válido"
                _isLoading.value = false
                return@launch
            }

            try {
                val response: AuthResponse = AuthService.forgotPassword(_email.value)
                _isLoading.value = false

                if (response.success) {
                    _step.value = Step.EnterTokenAndReset
                } else {
                    _errorMessage.value = response.message ?: "La solicitud falló"
                }
            } catch (e: Exception) {
                _isLoading.value = false
                _errorMessage.value = "Ocurrió un error: ${e.message}"
            }
        }
    }

    fun resetPassword(onPasswordResetSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _tokenError.value = null
            _newPasswordError.value = null
            _confirmPasswordError.value = null

            if (_token.value.isEmpty()) {
                _tokenError.value = "El token no puede estar vacío"
                _isLoading.value = false
                return@launch
            }

            if (!isValidToken(_token.value)) {
                _tokenError.value = "Token no válido"
                _isLoading.value = false
                return@launch
            }

            if (_newPassword.value.isEmpty()) {
                _newPasswordError.value = "La nueva contraseña no puede estar vacía"
                _isLoading.value = false
                return@launch
            }

            if (_confirmPassword.value.isEmpty()) {
                _confirmPasswordError.value = "La confirmación de contraseña no puede estar vacía"
                _isLoading.value = false
                return@launch
            }

            if (_newPassword.value != _confirmPassword.value) {
                _confirmPasswordError.value = "Las contraseñas no coinciden"
                _isLoading.value = false
                return@launch
            }

            try {
                val response: AuthResponse = AuthService.resetPassword(
                    email = _email.value,
                    token = _token.value,
                    password = _newPassword.value,
                    confirmPassword = _confirmPassword.value
                )
                _isLoading.value = false

                if (response.success) {
                    onPasswordResetSuccess()
                } else {
                    _errorMessage.value = response.message ?: "El restablecimiento falló"
                }
            } catch (e: Exception) {
                _isLoading.value = false
                _errorMessage.value = "Ocurrió un error: ${e.message}"
            }
        }
    }

    fun resendResetToken(onResendSuccess: () -> Unit) {
        viewModelScope.launch {
            _isResending.value = true
            _resendMessageError.value = null

            if (_email.value.isEmpty()) {
                _resendMessageError.value = "El correo no puede estar vacío."
                _isResending.value = false
                return@launch
            }

            try {
                val response: AuthResponse = AuthService.resendToken(
                    email = _email.value,
                    tokenType = TokenType.PASSWORD_RESET.name
                )
                if (response.success) {
                    onResendSuccess()
                    _resendCooldown.value = 60
                    for (i in 60 downTo 1) {
                        _resendCooldown.value = i
                        kotlinx.coroutines.delay(1000)
                    }
                    _resendCooldown.value = 0
                } else {
                    _resendMessageError.value = response.message ?: "El reenvío falló."
                }
            } catch (e: Exception) {
                _resendMessageError.value = "Ocurrió un error: ${e.message}"
            } finally {
                _isResending.value = false
            }
        }
    }

    enum class Step {
        RequestEmail,
        EnterTokenAndReset
    }
}