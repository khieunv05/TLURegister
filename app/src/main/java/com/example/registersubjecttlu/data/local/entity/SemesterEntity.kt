package com.example.registersubjecttlu.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.registersubjecttlu.data.local.SemesterConverters

@Entity(tableName = "semesters")
@TypeConverters(SemesterConverters::class)
data class SemesterEntity (
    @PrimaryKey val id: Int ,
    val semesterName: String,
    val isCurrent: Boolean,
    val semesterRegisterPeriods: List<SemesterRegisterPeriodEntity>?

)
data class SemesterRegisterPeriodEntity(
    val id: Int,
    val name: String,
    val displayOrder: Int,
    val startRegisterTime: Long,
    val endRegisterTime: Long,
    val isLockRegister: Boolean
)