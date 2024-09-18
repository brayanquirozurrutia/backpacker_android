package com.example.backpaker_android.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.backpaker_android.ui.components.CommonButton
import com.example.backpaker_android.ui.components.CommonInput
import com.example.backpaker_android.ui.components.InputType
import com.example.backpaker_android.ui.components.Loading
import com.example.backpaker_android.viewmodel.auth.LoginViewModel

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onCreateAccount: () -> Unit,
    loginViewModel: LoginViewModel = viewModel()
) {
    val email by loginViewModel.email.collectAsState()
    val password by loginViewModel.password.collectAsState()
    val isLoading by loginViewModel.isLoading.collectAsState()
    val errorMessage by loginViewModel.errorMessage.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        CommonInput(
            value = email,
            onValueChange = { loginViewModel.onEmailChanged(it) },
            label = "Correo",
            modifier = Modifier.fillMaxWidth(),
            inputType = InputType.Email
        )
        Spacer(modifier = Modifier.height(8.dp))
        CommonInput(
            value = password,
            onValueChange = { loginViewModel.onPasswordChanged(it) },
            label = "Contraseña",
            inputType = InputType.Password,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            Loading(modifier = Modifier.fillMaxSize())
        } else {
            CommonButton(
                text = "Iniciar Sesión",
                onClick = { loginViewModel.onLogin(onLoginSuccess) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                TextButton(onClick = onCreateAccount) {
                    Text("Crear cuenta")
                }
            }

            errorMessage?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
