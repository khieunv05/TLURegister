package com.example.registersubjecttlu.domain.model

import com.google.gson.annotations.SerializedName

data class RegisterPeriodRequest(
    val id: Int,
    val subjectId: Int,
)
