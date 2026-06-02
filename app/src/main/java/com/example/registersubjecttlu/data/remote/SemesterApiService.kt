package com.example.registersubjecttlu.data.remote

import com.example.registersubjecttlu.domain.model.SemesterResponse
import retrofit2.http.GET

interface SemesterApiService {
    @GET("api/semester/getwithfullsub")
    suspend fun fetchSemesters(): List<SemesterResponse>
}