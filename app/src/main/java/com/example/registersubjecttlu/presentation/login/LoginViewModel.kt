package com.example.registersubjecttlu.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.registersubjecttlu.domain.model.LoginRequest
import com.example.registersubjecttlu.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState

    fun login(username: String,password: String){
        val loginRequest = LoginRequest(username,password)
        viewModelScope.launch{
            try{
                _uiState.value = LoginUiState.Loading
                userRepository.login(loginRequest)
                _uiState.value = LoginUiState.Success
            }
            catch (e : Exception){
                _uiState.value = LoginUiState.Error(e.message.toString())
            }
        }
    }
}