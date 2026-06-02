package com.example.registersubjecttlu.di

import android.content.Context
import androidx.room.Room
import com.example.registersubjecttlu.AppDatabase
import com.example.registersubjecttlu.data.local.dao.SemesterDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase{
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "tlu_course_register.db"
        ).build()
    }
    @Provides
    @Singleton
    fun provideSemesterDao(database: AppDatabase): SemesterDao{
        return database.semesterDao
    }
}