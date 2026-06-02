package com.example.registersubjecttlu.domain.repository

import com.example.registersubjecttlu.domain.model.LoginRequest
import com.example.registersubjecttlu.domain.model.UserResponse

interface UserRepository {
    suspend fun login(loginRequest: LoginRequest): Boolean
}