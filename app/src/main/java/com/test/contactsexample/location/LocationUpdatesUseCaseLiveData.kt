package com.test.contactsexample.location

import android.content.Context
import android.location.Geocoder
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext
import kotlin.system.measureTimeMillis

class LocationUpdatesUseCaseLiveData @Inject constructor(
    @ApplicationContext context: Context,
    private val locationClient: FusedLocationProviderClient
) : LiveData<String>(), CoroutineScope {

    companion object {
        private const val TAG = "GetLocationUseCase"
    }

    private val job = SupervisorJob()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.IO

    private val geocoder: Geocoder by lazy {
        Geocoder(context)
    }

    private val locationRequest = LocationRequest.create().apply {
        interval = TimeUnit.SECONDS.toMillis(20) // This interval is inexact
        fastestInterval = TimeUnit.SECONDS.toMillis(2)
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)

            // Lint throws "Inappropriate blocking method call" on getFromLocation
            // when launching without explicit dispatcher, even though Dispatcher.IO is the context.
            launch(Dispatchers.IO) {
                val addresses = geocoder.getFromLocation(
                    locationResult.lastLocation.latitude,
                    locationResult.lastLocation.longitude,
                    1
                )

                postValue(addresses[0].getAddressLine(0))
            }
        }
    }

    override fun onActive() {
        super.onActive()

        try {
            locationClient.requestLocationUpdates(
                locationRequest, locationCallback, Looper.getMainLooper()
            )
        } catch (unlikely: SecurityException) {
            Log.e(TAG, "Lost location permissions. $unlikely")

            value = "N/A"
        }
    }

    override fun onInactive() {
        super.onInactive()
        job.cancel("Geocoder was running but there are no active observers")
        locationClient.removeLocationUpdates(locationCallback)
    }
}
