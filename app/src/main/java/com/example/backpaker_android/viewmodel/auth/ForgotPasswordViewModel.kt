package com.example.backpaker_android.viewmodel.auth


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.example.backpaker_android.network.auth.AuthService
import com.example.backpaker_android.network.auth.AuthResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ForgotPasswordViewModel : ViewModel() {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

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

    private val _newPasswordError = MutableStateFlow<String?>(null)
    val newPasswordError: StateFlow<String?> = _newPasswordError

    private val _confirmPasswordError = MutableStateFlow<String?>(null)
    val confirmPasswordError: StateFlow<String?> = _confirmPasswordError

    private val _step = MutableStateFlow(Step.RequestEmail)
    val step: StateFlow<Step> = _step

    fun onEmailChanged(newEmail: String) {
        _email.value = newEmail
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

            try {
                val response: AuthResponse = AuthService.forgotPassword(_email.value)
                _isLoading.value = false

                if (response.success) {
                    _step.value = Step.ResetPassword
                } else {
                    _errorMessage.value = response.message ?: "Request failed"
                }
            } catch (e: Exception) {
                _isLoading.value = false
                _errorMessage.value = "An error occurred: ${e.message}"
            }
        }
    }

    fun resetPassword(onPasswordResetSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _newPasswordError.value = null
            _confirmPasswordError.value = null

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
                val response: AuthResponse = AuthService.resetPassword(_email.value, _newPassword.value, _confirmPassword.value)
                _isLoading.value = false

                if (response.success) {
                    onPasswordResetSuccess()
                } else {
                    _errorMessage.value = response.message ?: "Reset failed"
                }
            } catch (e: Exception) {
                _isLoading.value = false
                _errorMessage.value = "An error occurred: ${e.message}"
            }
        }
    }

    enum class Step {
        RequestEmail,
        ResetPassword
    }
}