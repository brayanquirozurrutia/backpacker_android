package com.example.backpaker_android.utils


import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Get the current location of the device
 * @param context The context of the application
 * @return The current location of the device
 * @throws Exception If the location is not available
 */
@SuppressLint("MissingPermission")
suspend fun getCurrentLocation(context: Context): Location? {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    return try {
        val location = fusedLocationClient.lastLocation.await()
        location
    } catch (e: Exception) {
        null
    }
}

/**
 * Suspend extension function to await a task
 * @return The result of the task
 */
suspend fun com.google.android.gms.tasks.Task<Location>.await(): Location? {
    return suspendCancellableCoroutine { cont ->
        addOnSuccessListener { location ->
            cont.resume(location)
        }
        addOnFailureListener { exception ->
            cont.resumeWithException(exception)
        }
        addOnCanceledListener {
            cont.resume(null)
        }
    }
}
