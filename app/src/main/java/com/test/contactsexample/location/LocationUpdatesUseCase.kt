package com.test.contactsexample.location

import android.os.Looper
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class LocationUpdatesUseCase @Inject constructor(
    private val client: FusedLocationProviderClient
) {

    // Cold stream: the code inside a flow builder does not run until the Flow is collected.
    // (As opposed to Channel which is considered "hot")
    fun fetchUpdates(): Flow<LocationResult> = callbackFlow {

        val callback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                trySend(locationResult)
            }
        }

        startlocationUpdates(callback)

        // awaitClose should be used to keep the flow running,
        // otherwise the channel will be closed immediately when this block completes.
        // Using awaitClose is mandatory in order to prevent memory leaks when the flow collection is cancelled,
        // otherwise the callback may keep running even when the flow collector is already completed.
        awaitClose {
            client.removeLocationUpdates(callback)
        }

    }.conflate()

    private fun startlocationUpdates(callback: LocationCallback) {

        val request = LocationRequest.create().apply {
            interval = TimeUnit.SECONDS.toMillis(UPDATE_INTERVAL_SECS)
            fastestInterval = TimeUnit.SECONDS.toMillis(FASTEST_UPDATE_INTERVAL_SECS)
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        try {
            client.requestLocationUpdates(
                request, callback, Looper.getMainLooper()
            )
        } catch (unlikely: SecurityException) {
            Log.d(TAG, "Lost location permissions. $unlikely")
        }
    }

    companion object {
        private const val UPDATE_INTERVAL_SECS = 10L
        private const val FASTEST_UPDATE_INTERVAL_SECS = 2L
        private const val TAG = "LocationUpdatesUseCase"
    }
}