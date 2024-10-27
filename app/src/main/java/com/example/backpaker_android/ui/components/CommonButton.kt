package com.example.backpaker_android.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun CommonButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null,
    iconPosition: IconPosition = IconPosition.Start,
    isLoading: Boolean = false
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled && !isLoading
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .size(20.dp),
                strokeWidth = 2.dp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text)
        } else {
            if (icon != null) {
                when (iconPosition) {
                    IconPosition.Start -> {
                        Icon(imageVector = icon, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    IconPosition.End -> {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(imageVector = icon, contentDescription = null)
                    }
                }
            }
            Text(text)
        }
    }
}

enum class IconPosition {
    Start,
    End
}