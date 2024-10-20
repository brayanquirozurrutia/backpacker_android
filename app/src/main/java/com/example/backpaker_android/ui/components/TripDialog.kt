package com.example.backpaker_android.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TripDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
    isLoading: Boolean,
    errorMessage: String?
) {
    var destination by remember { mutableStateOf("") }
    var inputError by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = "Planificar Viaje") },
        text = {
            Column {
                CommonInput(
                    value = destination,
                    onValueChange = { destination = it },
                    label = "¿Dónde quieres ir?",
                    placeholder = "Ingresa tu destino",
                    required = true,
                    error = inputError
                )
                if (errorMessage != null) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (destination.isBlank()) {
                        inputError = "Este campo es requerido."
                    } else {
                        onConfirm(destination)
                    }
                },
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Enviar")
                }
            }
        },
        dismissButton = {
            CommonButton(
                text = "Cancelar",
                onClick = { onDismiss() },
                enabled = !isLoading
            )
        }
    )
}
