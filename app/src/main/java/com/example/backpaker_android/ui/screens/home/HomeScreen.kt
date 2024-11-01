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
import com.example.backpaker_android.network.home.TripData
import com.example.backpaker_android.utils.getCurrentLocation
import com.example.backpaker_android.viewmodel.home.HomeUiState
import com.google.accompanist.permissions.*
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.extension.style.expressions.generated.Expression
import com.mapbox.maps.extension.style.layers.addLayer
import com.mapbox.maps.extension.style.layers.generated.symbolLayer
import com.mapbox.maps.extension.style.sources.addSource
import com.mapbox.maps.extension.style.sources.generated.geoJsonSource
import com.mapbox.maps.plugin.gestures.OnMoveListener
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.extension.style.layers.getLayer
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.runtime.Composable
import com.example.backpaker_android.R


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(
    navController: NavController,
    homeViewModel: HomeViewModel = viewModel(),
) {
    val context = LocalContext.current
    val mapView = rememberMapView(context)

    val uiState by homeViewModel.uiState.collectAsState()
    val trips by homeViewModel.trips.collectAsState()

    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycle = lifecycleOwner.lifecycle

    val locationPermissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    LaunchedEffect(Unit) {
        locationPermissionsState.launchMultiplePermissionRequest()
    }

    LaunchedEffect(locationPermissionsState.allPermissionsGranted) {
        if (locationPermissionsState.allPermissionsGranted) {
            val location = getCurrentLocation(context)
            location?.let {
                homeViewModel.fetchHomeData(it.latitude, it.longitude, 10.0)
                mapView.mapboxMap.setCamera(
                    CameraOptions.Builder()
                        .center(Point.fromLngLat(it.longitude, it.latitude))
                        .zoom(15.0)
                        .build()
                )
            } ?: run {
                println("Ubicación no disponible al cargar el mapa.")
            }
        }
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

                        val onMoveListener = object : OnMoveListener {
                            override fun onMoveBegin(detector: MoveGestureDetector) {}
                            override fun onMove(detector: MoveGestureDetector): Boolean = false
                            override fun onMoveEnd(detector: MoveGestureDetector) {
                                val cameraState = mapView.mapboxMap.cameraState
                                val center = cameraState.center
                                homeViewModel.onMapMoved(
                                    center.latitude(),
                                    center.longitude(),
                                    10.0
                                )
                            }
                        }

                        mapView.gestures.addOnMoveListener(onMoveListener)
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
                        homeViewModel.fetchHomeData(it.latitude, it.longitude, 10.0)
                    } ?: run {
                        println("Ubicación no disponible al cargar el mapa.")
                    }
                }

                TripsOnMap(trips = trips, mapView = mapView)

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
        }
    }
}


@Composable
fun TripsOnMap(trips: List<TripData>, mapView: MapView) {
    val context = LocalContext.current

    val pinRequested: Bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.pin_requested)
    val pinInProgress: Bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.pin_in_progress)
    val pinCompleted: Bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.pin_completed)

    LaunchedEffect(trips, mapView) {
        mapView.mapboxMap.getStyle { style ->
            if (!style.hasStyleImage("pin-requested")) {
                style.addImage("pin-requested", pinRequested)
            }
            if (!style.hasStyleImage("pin-in_progress")) {
                style.addImage("pin-in_progress", pinInProgress)
            }
            if (!style.hasStyleImage("pin-completed")) {
                style.addImage("pin-completed", pinCompleted)
            }

            if (style.getLayer("trips-layer") != null) {
                style.removeStyleLayer("trips-layer")
                style.removeStyleSource("trips-source")
            }

            val features = trips.map { trip ->
                Feature.fromGeometry(
                    Point.fromLngLat(trip.longitude, trip.latitude)
                ).apply {
                    addStringProperty("status", trip.status)
                    addStringProperty("id", trip.id.toString())
                }
            }

            val featureCollection = FeatureCollection.fromFeatures(features)
            val geoJsonString = featureCollection.toJson()

            style.addSource(
                geoJsonSource("trips-source") {
                    data(geoJsonString)
                }
            )

            style.addLayer(
                symbolLayer("trips-layer", "trips-source") {
                    iconImage(
                        Expression.match(
                            Expression.get("status"),
                            Expression.literal("REQUESTED"),
                            Expression.literal("pin-requested"),
                            Expression.literal("IN_PROGRESS"),
                            Expression.literal("pin-in_progress"),
                            Expression.literal("COMPLETED"),
                            Expression.literal("pin-completed"),
                            Expression.literal("pin-requested")
                        )
                    )
                    iconSize(0.125)
                }
            )
        }
    }
}
