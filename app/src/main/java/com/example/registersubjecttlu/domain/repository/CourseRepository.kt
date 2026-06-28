package com.example.registersubjecttlu.domain.repository

import com.example.registersubjecttlu.domain.model.CourseResponse
import com.example.registersubjecttlu.domain.model.RegisterCourseResponse
import com.example.registersubjecttlu.domain.model.RegisterPeriodRequest

interface CourseRepository {
    suspend fun getCourse(studentId: Int, semesterId: Int): CourseResponse
    suspend fun registerCourse(studentId: Int,semesterId: Int,registerPeriodRequest: RegisterPeriodRequest): RegisterCourseResponse
    suspend fun removeRegister(studentId: Int,semesterId: Int,registerPeriodRequest: RegisterPeriodRequest): RegisterCourseResponse
}