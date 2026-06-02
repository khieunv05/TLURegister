package com.example.registersubjecttlu.data.local

import androidx.room.TypeConverter
import com.example.registersubjecttlu.data.local.entity.SemesterRegisterPeriodEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SemesterConverters {
    private val gson = Gson()
    @TypeConverter
    fun fromPeriodList(periods: List<SemesterRegisterPeriodEntity>?): String?{
        return gson.toJson(periods)
    }
    @TypeConverter
    fun toPeriodList(periodsString: String?):List<SemesterRegisterPeriodEntity>?{
        if(periodsString == null) return null
        val type = object : TypeToken<List<SemesterRegisterPeriodEntity>>(){}.type
        return gson.fromJson(periodsString,type)
    }
}