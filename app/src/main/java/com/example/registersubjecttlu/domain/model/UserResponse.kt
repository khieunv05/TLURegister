package com.example.registersubjecttlu.domain.model

import com.google.gson.annotations.SerializedName



data class UserResponse(
    @SerializedName("access_token")
    val accessToken: String
)

data class LoginRequest(
    val username: String, 
    val password: String
)
