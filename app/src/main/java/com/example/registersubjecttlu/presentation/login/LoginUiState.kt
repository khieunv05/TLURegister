package com.example.registersubjecttlu.presentation.login

import com.example.registersubjecttlu.domain.model.UserResponse

sealed interface LoginUiState {
    object Idle: LoginUiState
    object Loading: LoginUiState
    object Success: LoginUiState
    data class Error(val message: String): LoginUiState
}