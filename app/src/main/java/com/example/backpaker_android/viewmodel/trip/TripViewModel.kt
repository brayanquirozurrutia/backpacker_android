package com.example.backpaker_android.viewmodel.trip

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.backpaker_android.network.trip.TripService
import com.example.backpaker_android.network.trip.TripResponse

class TripViewModel : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage

    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    fun updateCoordinates(lat: Double, lon: Double) {
        latitude = lat
        longitude = lon
    }

    fun sendTrip(destination: String, context: Context) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _successMessage.value = null
            try {
                val response: TripResponse = TripService.sendTrip(
                    destination,
                    latitude,
                    longitude,
                    context
                )
                _isLoading.value = false
                if (response.success) {
                    _successMessage.value = "Viaje creado exitosamente."
                } else {
                    _errorMessage.value = response.message ?: "Error al crear el viaje."
                }
            } catch (e: Exception) {
                _isLoading.value = false
                _errorMessage.value = "Ocurrió un error: ${e.message}"
            }
        }
    }
}