package com.example.registersubjecttlu.domain.model

import com.google.gson.annotations.SerializedName

data class SemesterResponse(@SerializedName("semesterRegisterPeriods") val semesterRegisterPeriods: List<SemesterRegisterPeriod>?,
                            val isCurrent: Boolean?,
    val semesterName: String?,val id: Int)
data class SemesterRegisterPeriod(val id: Int, val name: String?,
    val startRegisterTime: Long?,val endRegisterTime: Long?,val isLockRegister: Boolean?,
    val displayOrder: Int? )

