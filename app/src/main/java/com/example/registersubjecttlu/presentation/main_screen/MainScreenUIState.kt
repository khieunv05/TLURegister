package com.example.registersubjecttlu.presentation.main_screen

import com.example.registersubjecttlu.domain.model.CourseResponse

sealed interface MainScreenUIState {
    object Loading: MainScreenUIState
    data class Success(val courseResponse: CourseResponse): MainScreenUIState
    data class Error(val message: String): MainScreenUIState

}