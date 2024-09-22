package com.example.backpaker_android.ui.screens.auth

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
import com.example.backpaker_android.viewmodel.auth.ForgotPasswordViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import com.example.backpaker_android.ui.components.CommonBasicAlert

@Composable
fun ForgotPasswordScreen(
    onPasswordResetSuccess: () -> Unit,
    onBack: () -> Unit,
    forgotPasswordViewModel: ForgotPasswordViewModel = viewModel()
) {
    val email by forgotPasswordViewModel.email.collectAsState()
    val newPassword by forgotPasswordViewModel.newPassword.collectAsState()
    val confirmPassword by forgotPasswordViewModel.confirmPassword.collectAsState()
    val isLoading by forgotPasswordViewModel.isLoading.collectAsState()
    val step by forgotPasswordViewModel.step.collectAsState()
    val errorMessage by forgotPasswordViewModel.errorMessage.collectAsState()
    val newPasswordError by forgotPasswordViewModel.newPasswordError.collectAsState()
    val confirmPasswordError by forgotPasswordViewModel.confirmPasswordError.collectAsState()
    val emailError by forgotPasswordViewModel.emailError.collectAsState()

    var showAlert by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            when (step) {
                ForgotPasswordViewModel.Step.RequestEmail -> {
                    CommonInput(
                        value = email,
                        onValueChange = { forgotPasswordViewModel.onEmailChanged(it) },
                        label = "Correo",
                        modifier = Modifier.fillMaxWidth(),
                        inputType = InputType.Email,
                        placeholder = "correo@mail.com",
                        error = emailError,
                        required = true
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    CommonButton(
                        text = "Verificar correo",
                        onClick = { forgotPasswordViewModel.requestPasswordReset() },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                ForgotPasswordViewModel.Step.ResetPassword -> {
                    CommonInput(
                        value = newPassword,
                        onValueChange = { forgotPasswordViewModel.onNewPasswordChanged(it) },
                        label = "Nueva Contraseña",
                        modifier = Modifier.fillMaxWidth(),
                        inputType = InputType.Password,
                        placeholder = "Nueva contraseña",
                        error = newPasswordError
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    CommonInput(
                        value = confirmPassword,
                        onValueChange = { forgotPasswordViewModel.onConfirmPasswordChanged(it) },
                        label = "Confirmar Contraseña",
                        modifier = Modifier.fillMaxWidth(),
                        inputType = InputType.Password,
                        placeholder = "Confirmar contraseña",
                        error = confirmPasswordError
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    CommonButton(
                        text = "Restablecer Contraseña",
                        onClick = {
                            forgotPasswordViewModel.resetPassword {
                                showAlert = true
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                Loading(modifier = Modifier.fillMaxSize())
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

        if (showAlert) {
            CommonBasicAlert(
                title = "Restablecimiento Exitoso",
                message = "Tu contraseña ha sido restablecida correctamente.",
                buttonText = "Aceptar",
                onDismiss = { showAlert = false },
                onConfirm = {
                    showAlert = false
                    onPasswordResetSuccess()
                },
                dismissOnOutsideClick = false
            )
        }
    }
}