package com.example.registersubjecttlu.di

import com.example.registersubjecttlu.data.local.TokenStorage
import com.example.registersubjecttlu.data.remote.CourseApiService
import com.example.registersubjecttlu.data.remote.SemesterApiService
import com.example.registersubjecttlu.data.remote.StudentApiService
import com.example.registersubjecttlu.data.remote.UserApiService
import com.example.registersubjecttlu.domain.model.UserResponse
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)

object NetworkModule {
    @Provides
    @Singleton
    fun provideOkHttpClient(tokenStorage: TokenStorage): OkHttpClient{
        return OkHttpClient.Builder()
            .addInterceptor {
                chain->
                val originalRequest = chain.request()

                // Không thêm Token nếu đang gọi API login
                if (originalRequest.url.encodedPath.contains("oauth/token")) {
                    return@addInterceptor chain.proceed(originalRequest)
                }

                val myToken = runBlocking {
                    tokenStorage.tokenFlow.first()
                }
                val newRequest = if(!myToken.isNullOrEmpty()){
                    originalRequest.newBuilder()
                        .header("Authorization", "Bearer $myToken")
                        .header("Accept","application/json")
                        .build()
                }
                else{
                    originalRequest.newBuilder()
                        .header("Accept","application/json")
                        .build()
                }

                chain.proceed(newRequest)
            }
            .connectTimeout(30, TimeUnit.SECONDS)
            .build()
    }
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit{
        return Retrofit.Builder()
            .baseUrl("https://sinhvien1.tlu.edu.vn/education/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    @Provides
    @Singleton
    fun provideUserApiService(retrofit: Retrofit): UserApiService{
        return retrofit.create(UserApiService::class.java)
    }
    @Provides
    @Singleton
    fun provideStudentApiService(retrofit: Retrofit): StudentApiService{
        return  retrofit.create(StudentApiService::class.java)
    }
    @Provides
    @Singleton
    fun provideSemesterApiService(retrofit: Retrofit): SemesterApiService{
        return retrofit.create(SemesterApiService::class.java)
    }
    @Provides
    @Singleton
    fun provideCourseApiService(retrofit: Retrofit): CourseApiService{
        return retrofit.create(CourseApiService::class.java)
    }
}