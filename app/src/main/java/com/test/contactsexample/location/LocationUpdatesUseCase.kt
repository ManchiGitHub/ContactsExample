package com.test.contactsexample.location

import android.os.Looper
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.system.measureTimeMillis

class LocationUpdatesUseCase @Inject constructor(
    private val client: FusedLocationProviderClient,
) {

//    val locationUpdates: Flow<LocationResult> = callbackFlow {
//
//        val callback = object : LocationCallback() {
//            override fun onLocationResult(locationResult: LocationResult) {
//                super.onLocationResult(locationResult)
//                trySend(locationResult)
//            }
//        }
//
//        startlocationUpdates(callback)
//
//        awaitClose {
//            client.removeLocationUpdates(callback)
//        }
//
//    }.conflate()

    val locationUpdates: Flow<String> = callbackFlow {
        var i = 1

        val start = System.currentTimeMillis()
        repeat(50) {
            delay(500)
            trySend("address ${i++}")
        }

        awaitClose {
            Log.d("_MARKO", "${System.currentTimeMillis() - start}")
//            client.removeLocationUpdates(callback)
        }


    }.conflate()


    private fun startlocationUpdates(callback: LocationCallback) {

        val request = LocationRequest.create().apply {
            interval = TimeUnit.SECONDS.toMillis(UPDATE_INTERVAL_SEC)
            fastestInterval = TimeUnit.SECONDS.toMillis(FASTEST_UPDATE_INTERVAL_SEC)
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        try {
            client.requestLocationUpdates(request, callback, Looper.getMainLooper())
        } catch (unlikely: SecurityException) {
            Log.d(TAG, "Lost location permissions. $unlikely")
        }
    }

    companion object {
        private const val UPDATE_INTERVAL_SEC = 10L
        private const val FASTEST_UPDATE_INTERVAL_SEC = 2L
        private const val TAG = "LocationUpdatesUseCase"
    }
}