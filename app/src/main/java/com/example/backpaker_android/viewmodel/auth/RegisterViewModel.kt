package com.example.backpaker_android.viewmodel.auth

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.backpaker_android.network.auth.AuthService
import com.example.backpaker_android.network.auth.AuthResponse
import com.example.backpaker_android.utils.SessionManager
import com.example.backpaker_android.utils.Utils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@RequiresApi(Build.VERSION_CODES.O)
class RegisterViewModel(application: Application) : AndroidViewModel(application) {

    private val _firstName = MutableStateFlow("")
    val firstName: StateFlow<String> = _firstName

    private val _lastName = MutableStateFlow("")
    val lastName: StateFlow<String> = _lastName

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _birthDate = MutableStateFlow("")
    val birthDate: StateFlow<String> = _birthDate

    private val _gender = MutableStateFlow<String?>(null)
    val gender: StateFlow<String?> = _gender

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword: StateFlow<String> = _confirmPassword

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _firstNameError = MutableStateFlow<String?>(null)
    val firstNameError: StateFlow<String?> = _firstNameError

    private val _lastNameError = MutableStateFlow<String?>(null)
    val lastNameError: StateFlow<String?> = _lastNameError

    private val _emailError = MutableStateFlow<String?>(null)
    val emailError: StateFlow<String?> = _emailError

    private val _birthDateError = MutableStateFlow<String?>(null)
    val birthDateError: StateFlow<String?> = _birthDateError

    private val _genderError = MutableStateFlow<String?>(null)
    val genderError: StateFlow<String?> = _genderError

    private val _passwordError = MutableStateFlow<String?>(null)
    val passwordError: StateFlow<String?> = _passwordError

    private val _confirmPasswordError = MutableStateFlow<String?>(null)
    val confirmPasswordError: StateFlow<String?> = _confirmPasswordError

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    @RequiresApi(Build.VERSION_CODES.O)
    fun onRegister(onRegisterSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            clearErrors()

            val cleanedFirstName = _firstName.value.trim().uppercase()
            val cleanedLastName = _lastName.value.trim().uppercase()
            val cleanedEmail = _email.value.trim()
            val cleanedBirthDate = _birthDate.value.trim()
            val cleanedGender = _gender.value?.trim()?.uppercase() ?: ""
            val cleanedPassword = _password.value.trim()
            val cleanedConfirmPassword = _confirmPassword.value.trim()

            var hasError = false

            if (cleanedFirstName.isEmpty()) {
                _firstNameError.value = "El nombre es requerido"
                hasError = true
            }

            if (cleanedLastName.isEmpty()) {
                _lastNameError.value = "El apellido es requerido"
                hasError = true
            }

            if (cleanedEmail.isEmpty()) {
                _emailError.value = "El correo es requerido"
                hasError = true
            }

            if (cleanedBirthDate.isEmpty()) {
                _birthDateError.value = "La fecha de nacimiento es requerida"
                hasError = true
            }

            if (cleanedGender.isEmpty()) {
                _genderError.value = "El género es requerido"
                hasError = true
            }

            if (cleanedPassword.isEmpty()) {
                _passwordError.value = "La contraseña es requerida"
                hasError = true
            }

            if (cleanedConfirmPassword.isEmpty()) {
                _confirmPasswordError.value = "La contraseña es requerida"
                hasError = true
            }

            if (cleanedPassword != cleanedConfirmPassword) {
                _confirmPasswordError.value = "Las contraseñas no coinciden"
                hasError = true
            }

            if (!Utils.isValidEmail(cleanedEmail)) {
                _emailError.value = "Correo electrónico no válido"
                hasError = true
            }

            val formattedBirthDate = try {
                convertDateFormat(cleanedBirthDate)
            } catch (e: Exception) {
                _birthDateError.value = "Formato de fecha no válido"
                hasError = true
                null
            }

            if (formattedBirthDate != null && !isAgeValid(formattedBirthDate)) {
                _birthDateError.value = "La edad mínima es de 18 años"
                hasError = true
            }

            if (hasError) {
                _isLoading.value = false
                return@launch
            }

            try {
                if (formattedBirthDate != null) {
                    val response: AuthResponse = AuthService.register(
                        cleanedFirstName,
                        cleanedLastName,
                        cleanedEmail,
                        formattedBirthDate,
                        cleanedGender,
                        cleanedPassword,
                        cleanedConfirmPassword
                    )
                    _isLoading.value = false

                    if (response.success) {
                        SessionManager.setUserEmail(getApplication(), cleanedEmail)
                        onRegisterSuccess()
                    } else {
                        _errorMessage.value = response.message ?: "Registration failed"
                    }
                } else {
                    _errorMessage.value = "Error en el formato de la fecha"
                }
            } catch (e: Exception) {
                _isLoading.value = false
                _errorMessage.value = "An error occurred: ${e.message}"
            }
        }
    }

    private fun clearErrors() {
        _firstNameError.value = null
        _lastNameError.value = null
        _emailError.value = null
        _birthDateError.value = null
        _genderError.value = null
        _passwordError.value = null
        _confirmPasswordError.value = null
        _errorMessage.value = null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun isAgeValid(birthDate: String): Boolean {
        val date = LocalDate.parse(birthDate)
        val age = Period.between(date, LocalDate.now()).years
        return age >= 18
    }

    private fun convertDateFormat(date: String): String {
        val inputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        return try {
            val parsedDate = LocalDate.parse(date, inputFormatter)
            parsedDate.format(outputFormatter)
        } catch (e: DateTimeParseException) {
            throw IllegalArgumentException("Formato de fecha no válido")
        }
    }

    fun onFirstNameChanged(newFirstName: String) {
        _firstName.value = newFirstName
    }

    fun onLastNameChanged(newLastName: String) {
        _lastName.value = newLastName
    }

    fun onEmailChanged(newEmail: String) {
        _email.value = newEmail
    }

    fun onBirthDateChanged(newBirthDate: String) {
        _birthDate.value = newBirthDate
    }

    fun onGenderChanged(newGender: String?) {
        _gender.value = newGender
    }

    fun onPasswordChanged(newPassword: String) {
        _password.value = newPassword
    }

    fun onConfirmPasswordChanged(newConfirmPassword: String) {
        _confirmPassword.value = newConfirmPassword
    }
}
