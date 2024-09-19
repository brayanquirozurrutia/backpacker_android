package com.example.backpaker_android.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.backpaker_android.ui.components.CommonSelect
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.backpaker_android.ui.components.*
import com.example.backpaker_android.viewmodel.auth.RegisterViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CreateAccountScreen(
    onRegisterSuccess: () -> Unit
) {
    val viewModel: RegisterViewModel = viewModel()
    val firstName by viewModel.firstName.collectAsState()
    val lastName by viewModel.lastName.collectAsState()
    val email by viewModel.email.collectAsState()
    val birthDate by viewModel.birthDate.collectAsState()
    val gender by viewModel.gender.collectAsState()
    val password by viewModel.password.collectAsState()
    val confirmPassword by viewModel.confirmPassword.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val firstNameError by viewModel.firstNameError.collectAsState()
    val lastNameError by viewModel.lastNameError.collectAsState()
    val emailError by viewModel.emailError.collectAsState()
    val birthDateError by viewModel.birthDateError.collectAsState()
    val genderError by viewModel.genderError.collectAsState()
    val passwordError by viewModel.passwordError.collectAsState()
    val confirmPasswordError by viewModel.confirmPasswordError.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var showAlert by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        CommonInput(
            value = firstName,
            onValueChange = { viewModel.onFirstNameChanged(it) },
            label = "Nombre",
            modifier = Modifier.fillMaxWidth(),
            inputType = InputType.Text,
            error = firstNameError
        )
        Spacer(modifier = Modifier.height(8.dp))
        CommonInput(
            value = lastName,
            onValueChange = { viewModel.onLastNameChanged(it) },
            label = "Apellido",
            modifier = Modifier.fillMaxWidth(),
            inputType = InputType.Text,
            error = lastNameError
        )
        Spacer(modifier = Modifier.height(8.dp))
        CommonInput(
            value = email,
            onValueChange = { viewModel.onEmailChanged(it) },
            label = "Correo",
            modifier = Modifier.fillMaxWidth(),
            inputType = InputType.Email,
            error = emailError
        )
        Spacer(modifier = Modifier.height(8.dp))

        DatePickerButton(
            dateState = remember { mutableStateOf(birthDate) },
            label = "Fecha de nacimiento"
        ) { date ->
            viewModel.onBirthDateChanged(date)
        }

        Spacer(modifier = Modifier.height(8.dp))

        CommonSelect(
            options = listOf("MALE", "FEMALE", "OTHER"),
            selectedOption = gender,
            onOptionSelected = { viewModel.onGenderChanged(it) },
            label = "Género",
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(8.dp))
        CommonInput(
            value = password,
            onValueChange = { viewModel.onPasswordChanged(it) },
            label = "Contraseña",
            modifier = Modifier.fillMaxWidth(),
            inputType = InputType.Password,
            error = passwordError
        )
        Spacer(modifier = Modifier.height(8.dp))
        CommonInput(
            value = confirmPassword,
            onValueChange = { viewModel.onConfirmPasswordChanged(it) },
            label = "Confirmar Contraseña",
            modifier = Modifier.fillMaxWidth(),
            inputType = InputType.Password,
            error = confirmPasswordError
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            Loading(modifier = Modifier.fillMaxSize())
        } else {
            CommonButton(
                text = "Crear Cuenta",
                onClick = {
                    viewModel.onRegister {
                        showAlert = true // Show alert when registration is successful
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
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
            title = "Registro Exitoso",
            message = "Tu cuenta ha sido creada exitosamente.",
            buttonText = "Aceptar",
            onDismiss = { showAlert = false },
            onConfirm = {
                showAlert = false
                onRegisterSuccess()
            },
            dismissOnOutsideClick = false
        )
    }
}