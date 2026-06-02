package com.example.registersubjecttlu.domain.repository

import com.example.registersubjecttlu.data.local.entity.SemesterEntity
import com.example.registersubjecttlu.domain.model.SemesterResponse
import kotlinx.coroutines.flow.Flow

interface SemesterRepository {
    suspend fun getSemesterFromLocal(): Flow<List<SemesterEntity>>
    suspend fun syncSemestersWithServer(): Unit
}