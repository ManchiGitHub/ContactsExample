package com.test.contactsexample.location

import android.content.Context
import android.location.Geocoder
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class LocationUpdatesUseCaseLiveData @Inject constructor(
    @ApplicationContext context: Context,
    private val locationClient: FusedLocationProviderClient
) : LiveData<String>() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val geocoder : Geocoder by lazy {
        Geocoder(context)
    }

    private val locationRequest = LocationRequest.create().apply {
        interval = TimeUnit.SECONDS.toMillis(20) // This interval is inexact
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)

            // Normally, At this point I would save the new location, and observe the database, or use Flow
            // This is just a simplification of things.
            scope.launch(Dispatchers.IO) {
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
        locationClient.removeLocationUpdates(locationCallback)
    }

    companion object {
        private const val TAG = "GetLocationUseCase"
    }
}
