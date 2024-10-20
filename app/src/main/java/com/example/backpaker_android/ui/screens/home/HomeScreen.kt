package com.example.backpaker_android.ui.screens.home

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.location.LocationServices
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.locationcomponent.location
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.example.backpaker_android.viewmodel.home.HomeViewModel
import com.example.backpaker_android.ui.components.Loading
import com.example.backpaker_android.viewmodel.trip.TripViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    homeViewModel: HomeViewModel = viewModel(),
    tripViewModel: TripViewModel = viewModel()
) {
    val context = LocalContext.current
    val mapView = rememberMapView(context)
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    var latitude by remember { mutableDoubleStateOf(0.0) }
    var longitude by remember { mutableDoubleStateOf(0.0) }

    var showPermissionDialog by remember { mutableStateOf(false) }

    val isLoading by homeViewModel.isLoading.collectAsState()
    val errorMessage by homeViewModel.errorMessage.collectAsState()
    val isDataLoaded by homeViewModel.isDataLoaded.collectAsState()

    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycle = lifecycleOwner.lifecycle

    DisposableEffect(lifecycle, mapView) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                        location?.let {
                            latitude = it.latitude
                            longitude = it.longitude

                            mapView.mapboxMap.setCamera(
                                CameraOptions.Builder()
                                    .center(Point.fromLngLat(longitude, latitude))
                                    .zoom(15.0)
                                    .build()
                            )
                        }
                    }
                } else {
                    showPermissionDialog = true
                }
            }
        }

        lifecycle.addObserver(observer)

        onDispose {
            lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(Unit) {
        homeViewModel.fetchHomeData()
        if (
            context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    latitude = it.latitude
                    longitude = it.longitude
                    tripViewModel.updateCoordinates(latitude, longitude)
                }
            }
        } else {
            showPermissionDialog = true
        }
    }

    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionDialog = false },
            title = { Text("Permisos de ubicación") },
            text = { Text("Esta aplicación necesita acceso a su ubicación para mostrar su posición en el mapa.") },
            confirmButton = {
                Button(onClick = {
                    requestLocationPermission(context)
                    showPermissionDialog = false
                }) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                Button(onClick = { showPermissionDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (isDataLoaded) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.weight(1f)) {
                AndroidView({ mapView }) { mapView ->
                    mapView.mapboxMap.loadStyle(
                        style = Style.MAPBOX_STREETS,
                        onStyleLoaded = {
                            mapView.location.updateSettings {
                                enabled = true
                                pulsingEnabled = true
                            }

                            if (latitude != 0.0 && longitude != 0.0) {
                                mapView.mapboxMap.setCamera(
                                    CameraOptions.Builder()
                                        .center(Point.fromLngLat(longitude, latitude))
                                        .zoom(15.0)
                                        .build()
                                )
                            }
                        }
                    )
                }

                if (isLoading) {
                    Loading()
                }

                if (errorMessage != null) {
                    Text(text = errorMessage!!)
                }
            }
        }
    }
}

private fun requestLocationPermission(context: Context) {
    if (context is ComponentActivity) {
        context.requestPermissions(
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }
}

@Composable
fun rememberMapView(context: Context): MapView {
    return remember {
        MapView(context)
    }
}

const val LOCATION_PERMISSION_REQUEST_CODE = 1000
