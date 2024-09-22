package com.example.backpaker_android.viewmodel.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.example.backpaker_android.network.home.HomeService
import com.example.backpaker_android.network.home.HomeResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HomeViewModel : ViewModel() {
    private val _homeData = MutableStateFlow<HomeResponse?>(null)
    val homeData: StateFlow<HomeResponse?> = _homeData

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _isDataLoaded = MutableStateFlow(false)
    val isDataLoaded: StateFlow<Boolean> = _isDataLoaded

    fun fetchHomeData() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response = HomeService.getHomeData()
                _isLoading.value = false

                if (response.success) {
                    _homeData.value = response
                    _isDataLoaded.value = true
                } else {
                    _errorMessage.value = response.message ?: "Failed to load home data"
                    _isDataLoaded.value = false
                }
            } catch (e: Exception) {
                _isLoading.value = false
                _errorMessage.value = "An error occurred: ${e.message}"
                _isDataLoaded.value = false
            }
        }
    }
}