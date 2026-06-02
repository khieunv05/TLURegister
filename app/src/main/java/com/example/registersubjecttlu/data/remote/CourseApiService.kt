package com.example.registersubjecttlu.data.remote

import com.example.registersubjecttlu.domain.model.CourseResponse
import com.example.registersubjecttlu.domain.model.RegisterCourseResponse
import com.example.registersubjecttlu.domain.model.RegisterPeriodRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface CourseApiService {
    @GET("api/cs_reg_mongo/findByPeriod/{idStudent}/{idSemester}")
    suspend fun fetchCourseBySemesterId(@Path("idStudent") studentId: Int,
                            @Path("idSemester") semesterId: Int): CourseResponse
    @POST("api/cs_reg_mongo/add-register/{idStudent}/{idSemester}")
    suspend fun registerCourse(
        @Path("idStudent") studentId: Int,
        @Path("idSemester") semesterId: Int,
        @Body registerPeriodRequest: RegisterPeriodRequest
    ): RegisterCourseResponse
}