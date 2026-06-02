package com.example.registersubjecttlu.data.repository

import com.example.registersubjecttlu.data.local.TokenStorage
import com.example.registersubjecttlu.data.remote.UserApiService
import com.example.registersubjecttlu.domain.model.LoginRequest
import com.example.registersubjecttlu.domain.model.UserResponse
import com.example.registersubjecttlu.domain.repository.UserRepository
import javax.inject.Inject

class UserRepositoryImp @Inject constructor(
    private val userApiService: UserApiService,
    private val tokenStorage: TokenStorage
) : UserRepository {
    override suspend fun login(loginRequest: LoginRequest): Boolean {
        val userResponse =  userApiService.login(
            user = loginRequest.username,
            password = loginRequest.password,
            grantType = "password",
            clientId = "education_client",
            clientSecret = "password"
        )
        if(userResponse.accessToken.isNotEmpty()){
            tokenStorage.saveToken(userResponse.accessToken)
            return true
        }
        return false


    }
}