package com.example.backpaker_android.ui.screens.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.backpaker_android.ui.components.CommonButton
import com.example.backpaker_android.ui.components.CommonInput
import com.example.backpaker_android.ui.components.InputType
import com.example.backpaker_android.ui.components.Loading
import com.example.backpaker_android.ui.components.CommonBasicAlert
import com.example.backpaker_android.viewmodel.auth.ForgotPasswordViewModel
import kotlinx.coroutines.launch

@Composable
fun ForgotPasswordScreen(
    onPasswordResetSuccess: () -> Unit,
    onBack: () -> Unit,
    forgotPasswordViewModel: ForgotPasswordViewModel = viewModel()
) {
    val email by forgotPasswordViewModel.email.collectAsState()
    val token by forgotPasswordViewModel.token.collectAsState()
    val newPassword by forgotPasswordViewModel.newPassword.collectAsState()
    val confirmPassword by forgotPasswordViewModel.confirmPassword.collectAsState()
    val isLoading by forgotPasswordViewModel.isLoading.collectAsState()
    val step by forgotPasswordViewModel.step.collectAsState()
    val errorMessage by forgotPasswordViewModel.errorMessage.collectAsState()
    val emailError by forgotPasswordViewModel.emailError.collectAsState()
    val tokenError by forgotPasswordViewModel.tokenError.collectAsState()
    val newPasswordError by forgotPasswordViewModel.newPasswordError.collectAsState()
    val confirmPasswordError by forgotPasswordViewModel.confirmPasswordError.collectAsState()
    val isResending by forgotPasswordViewModel.isResending.collectAsState()
    val resendMessageError by forgotPasswordViewModel.resendMessageError.collectAsState()
    val resendCooldown by forgotPasswordViewModel.resendCooldown.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    var showAlert by remember { mutableStateOf(false) }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

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
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isLoading
                        )
                    }
                    ForgotPasswordViewModel.Step.EnterTokenAndReset -> {

                        CommonInput(
                            value = token,
                            onValueChange = { forgotPasswordViewModel.onTokenChanged(it) },
                            label = "Código",
                            modifier = Modifier.fillMaxWidth(),
                            inputType = InputType.Number,
                            placeholder = "123456",
                            error = tokenError,
                            required = true,
                            maxLength = 6,
                            singleLine = true,
                            enabled = !isLoading
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        CommonInput(
                            value = newPassword,
                            onValueChange = { forgotPasswordViewModel.onNewPasswordChanged(it) },
                            label = "Nueva Contraseña",
                            modifier = Modifier.fillMaxWidth(),
                            inputType = InputType.Password,
                            placeholder = "Nueva contraseña",
                            error = newPasswordError,
                            required = true,
                            enabled = !isLoading
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        CommonInput(
                            value = confirmPassword,
                            onValueChange = { forgotPasswordViewModel.onConfirmPasswordChanged(it) },
                            label = "Confirmar Contraseña",
                            modifier = Modifier.fillMaxWidth(),
                            inputType = InputType.Password,
                            placeholder = "Confirmar contraseña",
                            error = confirmPasswordError,
                            required = true,
                            enabled = !isLoading
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        CommonButton(
                            text = "Restablecer Contraseña",
                            onClick = {
                                forgotPasswordViewModel.resetPassword {
                                    showAlert = true
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isLoading
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        TextButton(
                            onClick = {
                                forgotPasswordViewModel.resendResetToken {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Código de restablecimiento enviado exitosamente.")
                                    }
                                }
                            },
                            enabled = !isResending && resendCooldown == 0,
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Text(
                                text = when {
                                    isResending -> "Reenviando..."
                                    resendCooldown > 0 -> "Reenviar código en $resendCooldown s"
                                    else -> "Reenviar código"
                                },
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        if (resendMessageError != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = resendMessageError!!,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            AnimatedVisibility(
                visible = isLoading,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Loading()
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
}