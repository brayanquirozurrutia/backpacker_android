package com.example.backpaker_android.ui.screens.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.backpaker_android.ui.components.CommonBasicAlert
import com.example.backpaker_android.ui.components.CommonButton
import com.example.backpaker_android.ui.components.CommonInput
import com.example.backpaker_android.ui.components.InputType
import com.example.backpaker_android.ui.components.Loading
import com.example.backpaker_android.viewmodel.auth.ActivateAccountViewModel
import com.example.backpaker_android.user.TokenType
import kotlinx.coroutines.launch

@Composable
fun AccountActivationScreen(onActivationSuccess: () -> Unit) {
    val viewModel: ActivateAccountViewModel = viewModel()
    var token by remember { mutableStateOf("") }
    var showAlert by remember { mutableStateOf(false) }
    val errorMessage by viewModel.errorMessage.collectAsState()
    val tokenErrorMessage by viewModel.tokenErrorMessage.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isResending by viewModel.isResending.collectAsState()
    val resendMessageError by viewModel.resendMessageError.collectAsState()
    val resendCooldown by viewModel.resendCooldown.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

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
                CommonInput(
                    value = token,
                    onValueChange = { token = it },
                    label = "Token de activación",
                    modifier = Modifier.fillMaxWidth(),
                    inputType = InputType.Number,
                    error = tokenErrorMessage,
                    placeholder = "Ej: 000000",
                    required = true,
                    maxLength = 6,
                    singleLine = true,
                    enabled = !isLoading
                )

                Spacer(modifier = Modifier.height(16.dp))

                CommonButton(
                    text = if (isLoading) "Activando..." else "Activar Cuenta",
                    onClick = {
                        viewModel.activateAccount(token) {
                            showAlert = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading && token.length >= 6
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    TextButton(
                        onClick = {
                            viewModel.resendToken(TokenType.ACTIVATION) {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Código de reactivación enviado exitosamente.")
                                }
                            }
                        },
                        enabled = !isResending && resendCooldown == 0,
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text(
                            text = when {
                                isResending -> "Reenviando..."
                                resendCooldown > 0 -> "Reenviar token en $resendCooldown s"
                                else -> "Reenviar token"
                            },
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                if (errorMessage != null || resendMessageError != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = errorMessage ?: resendMessageError!!,
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
                    title = "Cuenta activada exitosamente",
                    message = "Tu cuenta ha sido activada exitosamente.",
                    buttonText = "Aceptar",
                    onDismiss = { showAlert = false },
                    onConfirm = {
                        showAlert = false
                        onActivationSuccess()
                    },
                    dismissOnOutsideClick = false
                )
            }
        }
    }
}
