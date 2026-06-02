package com.example.registersubjecttlu.data.repository

import com.example.registersubjecttlu.data.remote.CourseApiService
import com.example.registersubjecttlu.domain.model.CourseResponse
import com.example.registersubjecttlu.domain.model.RegisterCourseResponse
import com.example.registersubjecttlu.domain.model.RegisterPeriodRequest
import com.example.registersubjecttlu.domain.repository.CourseRepository
import javax.inject.Inject

class CourseRepositoryImp @Inject constructor(
    private val courseApiService: CourseApiService
): CourseRepository {
    override suspend fun getCourse(studentId: Int, semesterId: Int): CourseResponse {
        return courseApiService.fetchCourseBySemesterId(studentId,semesterId)
    }

    override suspend fun registerCourse(
        studentId: Int,
        semesterId: Int,
        registerPeriodRequest: RegisterPeriodRequest
    ): RegisterCourseResponse {
        return courseApiService.registerCourse(studentId,semesterId,registerPeriodRequest)
    }

}