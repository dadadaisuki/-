package com.travel.superapp.ui.screens.map

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlin.coroutines.resume

/** GPS 位置跟踪器（Google FusedLocationProvider） */
class LocationTracker(context: Context) {

    private val fusedClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private val locationRequest = LocationRequest.Builder(
        PRIORITY_HIGH_ACCURACY,
        3000L,
    ).apply {
        setMinUpdateIntervalMillis(2000L)
        setWaitForAccurateLocation(false)
    }.build()

    @SuppressLint("MissingPermission")
    fun locationUpdates(): Flow<TrackPoint> = callbackFlow {
        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { loc ->
                    trySend(loc.toTrackPoint())
                }
            }
        }

        fusedClient.requestLocationUpdates(
            locationRequest,
            callback,
            Looper.getMainLooper(),
        )

        awaitClose {
            fusedClient.removeLocationUpdates(callback)
        }
    }

    @SuppressLint("MissingPermission")
    suspend fun getLastLocation(): TrackPoint? = suspendCancellableCoroutine { cont ->
        fusedClient.lastLocation
            .addOnSuccessListener { loc ->
                cont.resume(loc?.toTrackPoint())
            }
            .addOnFailureListener {
                cont.resume(null)
            }
    }

    private fun android.location.Location.toTrackPoint() = TrackPoint(
        lat = latitude,
        lon = longitude,
        altitude = altitude,
        speedMps = speed,
        timestamp = time,
        accuracy = accuracy,
    )

    companion object {
        @Volatile
        private var INSTANCE: LocationTracker? = null

        fun getInstance(context: Context): LocationTracker {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: LocationTracker(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}
