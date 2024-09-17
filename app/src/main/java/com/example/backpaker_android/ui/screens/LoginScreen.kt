package com.example.backpaker_android.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.backpaker_android.ui.components.CommonButton
import com.example.backpaker_android.ui.components.CommonInput
import com.example.backpaker_android.ui.components.Loading
import com.example.backpaker_android.network.auth.AuthService
import com.example.backpaker_android.network.auth.LoginResponse
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        CommonInput(
            value = email,
            onValueChange = { email = it },
            label = "Email",
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        CommonInput(
            value = password,
            onValueChange = { password = it },
            label = "Password",
            isPassword = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            // Mostrar el componente de carga mientras se realiza la solicitud
            Loading(modifier = Modifier.fillMaxSize())
        } else {
            CommonButton(
                text = "Login",
                onClick = {
                    // Iniciar la solicitud de login en una coroutine
                    scope.launch {
                        isLoading = true
                        errorMessage = null
                        val response = AuthService.login(email, password)
                        isLoading = false

                        if (response.success) {
                            onLoginSuccess()
                        } else {
                            errorMessage = response.message ?: "Login failed"
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            // Mostrar mensajes de error
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
