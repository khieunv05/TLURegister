package com.example.registersubjecttlu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.registersubjecttlu.presentation.login.LoginScreen
import com.example.registersubjecttlu.presentation.login.LoginViewModel
import com.example.registersubjecttlu.presentation.main_screen.MainScreen
import com.example.registersubjecttlu.presentation.main_screen.MainScreenViewModel
import com.example.registersubjecttlu.ui.theme.RegisterSubJectTLUTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RegisterSubJectTLUTheme {
                val navController = rememberNavController()
                
                NavHost(navController = navController, startDestination = "login") {
                    composable("login") {
                        val loginViewModel: LoginViewModel = hiltViewModel()
                        LoginScreen(viewModel = loginViewModel) {
                            navController.navigate("main") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    }
                    composable("main") {
                        val mainViewModel: MainScreenViewModel = hiltViewModel()
                        MainScreen(viewModel = mainViewModel)
                    }
                }
            }
        }
    }
}
