package com.example.backpaker_android.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun CommonInput(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    inputType: InputType = InputType.Text,
    error: String? = null,
    placeholder: String? = null,
    required: Boolean = false,
    maxLength: Int? = null,
    singleLine: Boolean = false,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    onTrailingIconClick: (() -> Unit)? = null
) {
    Column(modifier) {
        Text(
            text = if (required) "$label *" else label,
            style = MaterialTheme.typography.labelMedium
        )

        val filteredValue = if (maxLength != null) {
            value.take(maxLength)
        } else {
            value
        }

        TextField(
            value = filteredValue,
            onValueChange = { newValue ->
                if (maxLength == null || newValue.length <= maxLength) {
                    onValueChange(newValue)
                }
            },
            placeholder = { Text(placeholder ?: "") },
            leadingIcon = if (leadingIcon != null) {
                { Icon(imageVector = leadingIcon, contentDescription = null) }
            } else null,
            trailingIcon = if (trailingIcon != null) {
                {
                    IconButton(onClick = { onTrailingIconClick?.invoke() }) {
                        Icon(imageVector = trailingIcon, contentDescription = null)
                    }
                }
            } else null,
            visualTransformation = when (inputType) {
                InputType.Password -> PasswordVisualTransformation()
                else -> VisualTransformation.None
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = when (inputType) {
                    InputType.Text -> KeyboardType.Text
                    InputType.Number -> KeyboardType.Number
                    InputType.Email -> KeyboardType.Email
                    InputType.Phone -> KeyboardType.Phone
                    InputType.Password -> KeyboardType.Password
                    InputType.Date -> KeyboardType.Number
                }
            ),
            isError = error != null,
            singleLine = singleLine,
            enabled = enabled,
            modifier = modifier
        )

        if (error != null) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

enum class InputType {
    Text,
    Number,
    Email,
    Phone,
    Password,
    Date
}
