package com.example.backpaker_android.ui.screens.trip

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import android.Manifest
import androidx.navigation.NavController
import com.example.backpaker_android.navigation.Routes
import com.example.backpaker_android.ui.components.CommonBasicAlert
import com.example.backpaker_android.ui.components.CommonButton
import com.example.backpaker_android.ui.components.CommonInput
import com.example.backpaker_android.ui.components.IconPosition
import com.example.backpaker_android.ui.screens.home.PermissionDeniedContent
import com.example.backpaker_android.viewmodel.trip.TripViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun TripScreen(
    onBack: () -> Unit,
    navController: NavController,
    tripViewModel: TripViewModel = viewModel()
) {
    val destination by tripViewModel.destination.collectAsState()
    val destinationError by tripViewModel.destinationError.collectAsState()
    val isLoading by tripViewModel.isLoading.collectAsState()
    val errorMessage by tripViewModel.errorMessage.collectAsState()
    val successMessage by tripViewModel.successMessage.collectAsState()
    val showSuccessDialog by tripViewModel.showSuccessDialog.collectAsState()

    val locationPermissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    LaunchedEffect(Unit) {
        locationPermissionsState.launchMultiplePermissionRequest()
    }

    LaunchedEffect(Unit) {
        tripViewModel.navigationEvent.collect {
            navController.navigate(Routes.HOME) {
                popUpTo(Routes.HOME) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    if (showSuccessDialog) {
        CommonBasicAlert(
            title = "¡Viaje Creado!",
            message = "El viaje se ha creado exitosamente.",
            buttonText = "Aceptar",
            onDismiss = { tripViewModel.resetMessages() },
            onConfirm = { tripViewModel.onConfirmDialog() }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Planificar Viaje") },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (locationPermissionsState.allPermissionsGranted) {
                CommonInput(
                    value = destination,
                    onValueChange = { tripViewModel.updateDestination(it) },
                    label = "¿Dónde quieres ir?",
                    placeholder = "Ingresa tu destino",
                    error = destinationError,
                    required = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                CommonButton(
                    text = "Enviar",
                    onClick = { tripViewModel.sendTrip() },
                    enabled = destination.isNotBlank(),
                    modifier = Modifier.fillMaxWidth(),
                    isLoading = isLoading,
                    icon = if (isLoading) null else Icons.AutoMirrored.Filled.Send,
                    iconPosition = IconPosition.Start
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

            } else {
                PermissionDeniedContent(
                    permissionsState = locationPermissionsState
                )
            }
        }
    }
}