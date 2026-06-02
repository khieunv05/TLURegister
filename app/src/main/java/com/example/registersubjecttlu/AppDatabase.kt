package com.example.registersubjecttlu

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.registersubjecttlu.data.local.dao.SemesterDao
import com.example.registersubjecttlu.data.local.entity.SemesterEntity

@Database(entities = [SemesterEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase(){
    abstract val semesterDao: SemesterDao
}