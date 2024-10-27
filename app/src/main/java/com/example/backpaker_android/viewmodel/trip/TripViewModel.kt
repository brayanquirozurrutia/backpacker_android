package com.example.backpaker_android.viewmodel.trip

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import android.location.Location
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.backpaker_android.network.trip.TripService
import com.example.backpaker_android.network.trip.TripResponse
import com.example.backpaker_android.utils.SessionManager
import com.example.backpaker_android.utils.getCurrentLocation
import kotlinx.coroutines.flow.asStateFlow

class TripViewModel(application: Application) : AndroidViewModel(application) {
    private val _destination = MutableStateFlow("")
    val destination: StateFlow<String> = _destination.asStateFlow()

    private val _destinationError = MutableStateFlow<String?>(null)
    val destinationError: StateFlow<String?> = _destinationError.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage

    fun updateDestination(newDestination: String) {
        _destination.value = newDestination
        if (newDestination.isNotBlank()) {
            _destinationError.value = null
        }
    }

    fun sendTrip() {
        viewModelScope.launch {
            val context = getApplication<Application>().applicationContext

            if (_destination.value.isBlank()) {
                _destinationError.value = "Este campo es requerido."
                return@launch
            }

            val userId = SessionManager.getUserId(context)
            val token = SessionManager.getAccessToken(context)

            if (userId == null) {
                _errorMessage.value = "ID de usuario no encontrado."
                return@launch
            }

            val location: Location? = getCurrentLocation(context)

            if (location == null) {
                _errorMessage.value = "No se pudo obtener la ubicación."
                return@launch
            }

            _isLoading.value = true
            _errorMessage.value = null
            _successMessage.value = null

            try {
                val response: TripResponse = TripService.sendTrip(
                    userId = userId,
                    destination = _destination.value,
                    latitude = location.latitude,
                    longitude = location.longitude,
                    token = token
                )
                _isLoading.value = false
                if (response.success) {
                    _successMessage.value = "Viaje creado exitosamente."
                    _destination.value = ""
                } else {
                    _errorMessage.value = response.message ?: "Error al crear el viaje."
                }
            } catch (e: Exception) {
                _isLoading.value = false
                _errorMessage.value = "Ocurrió un error: ${e.message}"
            }
        }
    }

    fun resetSuccessMessage() {
        _successMessage.value = null
    }

    fun resetErrorMessage() {
        _errorMessage.value = null
    }
}