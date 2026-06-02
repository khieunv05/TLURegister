package com.example.registersubjecttlu.data.remote

import com.example.registersubjecttlu.domain.model.StudentResponse
import retrofit2.http.GET

interface StudentApiService {
    @GET("api/student/getstudentbylogin")
    suspend fun fetchStudent(): StudentResponse
}