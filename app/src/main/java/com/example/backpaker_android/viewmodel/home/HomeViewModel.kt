package com.example.backpaker_android.viewmodel.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.example.backpaker_android.network.home.HomeService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HomeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState

    fun fetchHomeData() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            try {
                val response = HomeService.getHomeData()
                if (response.success) {
                    _uiState.value = HomeUiState.Success(response)
                } else {
                    _uiState.value = HomeUiState.Error(response.message ?: "Failed to load home data")
                }
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error("An error occurred: ${e.message}")
            }
        }
    }
}