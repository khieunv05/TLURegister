package com.example.registersubjecttlu.presentation.main_screen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

enum class ScreenTab(val title: String,val icon: ImageVector) {
    HOME("Trang chủ", Icons.Default.Home),
    PROFILE("Cài đặt",Icons.Default.Settings)
}