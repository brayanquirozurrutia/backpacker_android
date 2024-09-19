package com.example.backpaker_android.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun CommonBasicAlert(
    title: String,
    message: String,
    buttonText: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
    dismissOnOutsideClick: Boolean = true
) {
    AlertDialog(
        onDismissRequest = {
            if (dismissOnOutsideClick) {
                onDismiss()
            }
        },
        title = { Text(text = title) },
        text = { Text(text = message) },
        confirmButton = {
            Button(onClick = { onConfirm() }) {
                Text(text = buttonText)
            }
        },
        modifier = modifier
    )
}
