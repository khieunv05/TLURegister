package com.example.registersubjecttlu.presentation.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(viewModel: LoginViewModel,
                onLoginSuccess: ()-> Unit){
    val uiState by viewModel.uiState.collectAsState()
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    LaunchedEffect(uiState) {
        if(uiState == LoginUiState.Success){
            onLoginSuccess()
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text("Đăng ký học")
            })
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
        )
        {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Text("Tớ iu công chúa nhìu lắm hehe", style = MaterialTheme.typography.labelLarge.copy(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                ))
                Spacer(modifier = Modifier.height(8.dp))
                Text("Đăng nhập để đăng ký học", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(24.dp))
                OutlinedTextField(
                    value = username,
                    onValueChange = {username = it},
                    label = {Text("Mã sinh viên")},
                    placeholder = {Text("Nhập mã sinh viên của bạn")},
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = {password = it},
                    label = {Text("Mật khẩu")},
                    placeholder = {Text("Nhập mật khẩu của bạn vào đây")},
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation()
                )
                Spacer(modifier = Modifier.height(16.dp))
                if(uiState is LoginUiState.Error){
                    Text(
                        text = (uiState as LoginUiState.Error).message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                Button(
                    onClick = {viewModel.login(username,password)},
                    modifier = Modifier.fillMaxWidth().height(50.dp).padding(8.dp),
                    enabled = uiState !is LoginUiState.Loading
                ) {
                    if(uiState is LoginUiState.Loading){
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    }
                    else{
                        Text("Đăng nhập", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }

}