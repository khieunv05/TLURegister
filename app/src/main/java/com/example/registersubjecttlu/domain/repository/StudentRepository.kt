package com.example.registersubjecttlu.domain.repository

import com.example.registersubjecttlu.domain.model.StudentResponse

interface StudentRepository {
    suspend fun getStudent(): StudentResponse
}