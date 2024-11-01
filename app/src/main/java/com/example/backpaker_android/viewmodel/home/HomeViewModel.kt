package com.example.backpaker_android.viewmodel.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.backpaker_android.network.home.HomeResponse
import kotlinx.coroutines.launch
import com.example.backpaker_android.network.home.HomeService
import com.example.backpaker_android.network.home.TripData
import com.example.backpaker_android.network.trip.TripsRequest
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce

sealed class HomeUiState {
    data object Loading : HomeUiState()
    data class Success(val data: HomeResponse) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}

@OptIn(FlowPreview::class)
class HomeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState

    private val _trips = MutableStateFlow<List<TripData>>(emptyList())
    val trips: StateFlow<List<TripData>> = _trips

    private val _tripsRequestFlow = MutableSharedFlow<TripsRequest>(extraBufferCapacity = 64)

    init {
        viewModelScope.launch {
            _tripsRequestFlow
                .debounce(1000)
                .collect { tripsRequest ->
                    val trips = HomeService.getTripsWithinRadius(
                        lat = tripsRequest.lat,
                        lon = tripsRequest.lon,
                        radiusKm = tripsRequest.radiusKm
                    )
                    _trips.value = trips
                }
        }
    }

    fun fetchHomeData(lat: Double, lon: Double, radiusKm: Double = 10.0) {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            try {
                val response = HomeService.getHomeData(lat, lon, radiusKm)
                if (response.success) {
                    _uiState.value = HomeUiState.Success(response)
                    _trips.value = response.trips
                } else {
                    _uiState.value = HomeUiState.Error(response.message ?: "Failed to load home data")
                }
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error("An error occurred: ${e.message}")
            }
        }
    }

    fun onMapMoved(lat: Double, lon: Double, radiusKm: Double = 10.0) {
        viewModelScope.launch {
            _tripsRequestFlow.emit(TripsRequest(lat, lon, radiusKm))
        }
    }
}