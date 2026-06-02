package com.example.registersubjecttlu.data.repository

import com.example.registersubjecttlu.data.local.dao.SemesterDao
import com.example.registersubjecttlu.data.local.entity.SemesterEntity
import com.example.registersubjecttlu.data.local.entity.SemesterRegisterPeriodEntity
import com.example.registersubjecttlu.data.remote.SemesterApiService
import com.example.registersubjecttlu.domain.repository.SemesterRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SemesterRepositoryImp @Inject constructor(
    private val semesterApiService: SemesterApiService,
    private val semesterDao: SemesterDao
) : SemesterRepository {
    override suspend fun getSemesterFromLocal(): Flow<List<SemesterEntity>> {
        return semesterDao.getAllSemesters()
    }

    override suspend fun syncSemestersWithServer() {
        try {
            val apiResponse = semesterApiService.fetchSemesters()
            val entities = apiResponse.map {semesterDto->
                SemesterEntity(
                    id = semesterDto.id,
                    semesterName = semesterDto.semesterName ?: "",
                    isCurrent = semesterDto.isCurrent ?: false,
                    semesterRegisterPeriods = semesterDto.semesterRegisterPeriods?.map {
                            periodDto ->
                        SemesterRegisterPeriodEntity(
                            id = periodDto.id,
                            name = periodDto.name ?: "",
                            displayOrder = periodDto.displayOrder ?: 1,
                            startRegisterTime = periodDto.startRegisterTime ?: 0,
                            endRegisterTime = periodDto.endRegisterTime ?: 0,
                            isLockRegister = periodDto.isLockRegister ?: false
                        )
                    } ?: emptyList()
                )
            }
            semesterDao.insertSemesters(entities)
        }
        catch (e: Exception){
            e.printStackTrace()
            throw e
        }

    }
}