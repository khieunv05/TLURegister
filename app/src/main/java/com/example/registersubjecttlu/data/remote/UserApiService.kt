package com.example.registersubjecttlu.data.remote

import com.example.registersubjecttlu.domain.model.UserResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface UserApiService {
    @FormUrlEncoded
    @POST("oauth/token")
    suspend fun login(
        @Field("username") user: String,
        @Field("password") password: String,
        @Field("grant_type") grantType: String = "password",
        @Field("client_id") clientId: String = "education_client",
        @Field("client_secret") clientSecret: String = "password"
    ): UserResponse
}