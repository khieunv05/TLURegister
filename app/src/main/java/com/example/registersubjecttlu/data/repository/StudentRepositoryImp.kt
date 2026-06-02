package com.example.registersubjecttlu.data.repository

import com.example.registersubjecttlu.data.remote.StudentApiService
import com.example.registersubjecttlu.domain.model.StudentResponse
import com.example.registersubjecttlu.domain.repository.StudentRepository
import javax.inject.Inject

class StudentRepositoryImp @Inject constructor(
    private val studentApiService: StudentApiService
) : StudentRepository {
    override suspend fun getStudent(): StudentResponse {
        return studentApiService.fetchStudent()
    }

}