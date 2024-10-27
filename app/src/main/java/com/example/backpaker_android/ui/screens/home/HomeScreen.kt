package com.example.backpaker_android.ui.screens.home

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.locationcomponent.location
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.example.backpaker_android.viewmodel.home.HomeViewModel
import com.example.backpaker_android.ui.components.Loading
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import android.provider.Settings
import androidx.compose.material3.*
import com.example.backpaker_android.utils.getCurrentLocation
import com.example.backpaker_android.viewmodel.home.HomeUiState
import com.google.accompanist.permissions.*
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(
    navController: NavController,
    homeViewModel: HomeViewModel = viewModel(),
) {
    val context = LocalContext.current
    val mapView = rememberMapView(context)

    val uiState by homeViewModel.uiState.collectAsState()

    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycle = lifecycleOwner.lifecycle

    val locationPermissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    LaunchedEffect(Unit) {
        homeViewModel.fetchHomeData()
        locationPermissionsState.launchMultiplePermissionRequest()
    }

    DisposableEffect(lifecycle, mapView) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            when (event) {
                androidx.lifecycle.Lifecycle.Event.ON_START -> mapView.onStart()
                androidx.lifecycle.Lifecycle.Event.ON_STOP -> mapView.onStop()
                else -> {}
            }
        }
        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
            mapView.onDestroy()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        if (locationPermissionsState.allPermissionsGranted) {
            Box(modifier = Modifier.weight(1f)) {
                AndroidView({ mapView }) { mapView ->
                    mapView.mapboxMap.loadStyle(
                        style = Style.MAPBOX_STREETS
                    ) {
                        mapView.location.updateSettings {
                            enabled = true
                            pulsingEnabled = true
                        }
                    }
                }

                LaunchedEffect(Unit) {
                    val location = getCurrentLocation(context)
                    location?.let {
                        mapView.mapboxMap.setCamera(
                            CameraOptions.Builder()
                                .center(Point.fromLngLat(it.longitude, it.latitude))
                                .zoom(15.0)
                                .build()
                        )
                    } ?: run {
                        // TODO: MOSTRAR UN SNACKBAR O UN TOAST CUANDO NO SE PUEDE OBTENER LA UBICACIÓN
                        println("Ubicación no disponible al cargar el mapa.")
                    }
                }

                when (uiState) {
                    is HomeUiState.Loading -> Loading()
                    is HomeUiState.Success -> {
                        // Aquí puedes mostrar datos adicionales si lo deseas
                    }
                    is HomeUiState.Error -> {
                        val error = (uiState as HomeUiState.Error).message
                        Text(text = error, color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        } else {
            PermissionDeniedContent(
                permissionsState = locationPermissionsState
            )
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionDeniedContent(
    permissionsState: MultiplePermissionsState
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        when {
            permissionsState.shouldShowRationale -> {
                Text("La aplicación necesita acceder a tu ubicación para mostrar tu posición en el mapa.")
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { permissionsState.launchMultiplePermissionRequest() }) {
                    Text("Permitir")
                }
            }
            permissionsState.permissions.any {
                !it.status.isGranted && !it.status.shouldShowRationale
            } -> {
                Text("Permisos de ubicación denegados. Puedes habilitarlos desde la configuración.")
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = {
                    val intent = Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", context.packageName, null)
                    )
                    context.startActivity(intent)
                }) {
                    Text("Configuración")
                }
            }
            else -> {
                Button(onClick = { permissionsState.launchMultiplePermissionRequest() }) {
                    Text("Solicitar Permisos")
                }
            }
        }
    }
}

@Composable
fun rememberMapView(context: Context): MapView {
    return remember {
        MapView(context).apply {
            // Configuraciones adicionales si es necesario
        }
    }
}