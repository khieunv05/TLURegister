package com.example.registersubjecttlu.presentation.main_screen

import com.example.registersubjecttlu.domain.model.CourseResponse
import com.example.registersubjecttlu.domain.model.RegisterCourseResponse

sealed interface MainScreenUIState {
    object Loading: MainScreenUIState
    object NavigateToLoginScreen: MainScreenUIState
    data class Success(val courseResponse: CourseResponse): MainScreenUIState
    data class RegisterSuccess(val registerCourseResponse: RegisterCourseResponse): MainScreenUIState
    data class RegisterError(val message: String): MainScreenUIState
    data class Error(val message: String): MainScreenUIState

}