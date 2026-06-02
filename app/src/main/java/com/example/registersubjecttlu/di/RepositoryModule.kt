package com.example.registersubjecttlu.di

import com.example.registersubjecttlu.data.repository.CourseRepositoryImp
import com.example.registersubjecttlu.data.repository.SemesterRepositoryImp
import com.example.registersubjecttlu.data.repository.StudentRepositoryImp
import com.example.registersubjecttlu.data.repository.UserRepositoryImp
import com.example.registersubjecttlu.domain.repository.CourseRepository
import com.example.registersubjecttlu.domain.repository.SemesterRepository
import com.example.registersubjecttlu.domain.repository.StudentRepository
import com.example.registersubjecttlu.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindUserRepository(userRepositoryImp: UserRepositoryImp): UserRepository

    @Binds
    @Singleton
    abstract fun bindStudentRepository(studentRepositoryImp: StudentRepositoryImp): StudentRepository

    @Binds
    @Singleton
    abstract fun bindSemesterRepository(semesterRepositoryImp: SemesterRepositoryImp): SemesterRepository

    @Binds
    @Singleton
    abstract fun bindCourseRepository(courseRepositoryImp: CourseRepositoryImp): CourseRepository
}