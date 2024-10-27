package com.example.backpaker_android.viewmodel.home

import com.example.backpaker_android.network.home.HomeResponse

sealed class HomeUiState {
    data object Loading : HomeUiState()
    data class Success(val data: HomeResponse) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}