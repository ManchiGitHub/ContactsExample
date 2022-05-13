package com.test.contactsexample.location

import android.Manifest
import android.app.Activity
import android.content.Context
import android.location.LocationRequest
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


/**
 * Class for handling location permissions and activation.
 * All requests are done in coroutines.
 * @param resultRegistry [ActivityResultRegistry] for registering the contract and callback
 */
@Singleton
class LocationHelper(private val resultRegistry: ActivityResultRegistry) {

    private val handlers = mutableListOf<ActivityResultLauncher<*>>()

    suspend fun requestLocationPermission(): Boolean = suspendCoroutine { cont ->

        val launcher = resultRegistry.register(
            LOCATION_REQUEST, ActivityResultContracts.RequestPermission()
        ) {
            cont.resumeWith(Result.success(it))
        }
        handlers.add(launcher)
        launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    suspend fun enableLocation(context: Context): Boolean = suspendCoroutine { cont ->

        val locationRequest = com.google.android.gms.location.LocationRequest.create()

        val locationSettingsRequest =
            LocationSettingsRequest.Builder().addLocationRequest(locationRequest.apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    priority = LocationRequest.QUALITY_HIGH_ACCURACY
                }
            }).setAlwaysShow(true).build()

        val client: SettingsClient = LocationServices.getSettingsClient(context)

        // Check if the relevant system settings are enabled on the device to carry out the desired location requests.
        val task: Task<LocationSettingsResponse> =
            client.checkLocationSettings(locationSettingsRequest)

        task.addOnSuccessListener {
            // Location is turned on
            cont.resume(true)
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException &&
                exception.statusCode == LocationSettingsStatusCodes.RESOLUTION_REQUIRED
            ) {
                val intentSenderRequest =
                    IntentSenderRequest.Builder(exception.resolution).build()

                CoroutineScope(cont.context).launch {
                    val result = requestLocationActivation(intentSenderRequest)
                    cont.resume(result)
                }
            } else {
                cont.resume(false)
            }
        }
    }

    private suspend fun requestLocationActivation(request: IntentSenderRequest): Boolean =
        suspendCoroutine { cont ->

            val launcher = resultRegistry.register(
                LOCATION_REQUEST,
                ActivityResultContracts.StartIntentSenderForResult()
            ) {
                cont.resume(it.resultCode == Activity.RESULT_OK)
            }

            handlers.add(launcher)
            launcher.launch(request)
        }

    /**
     * Unregisters the ActivityResultLaunchers
     */
    fun unregisterHandlers() {
        handlers.forEach {
            it.unregister()
        }
    }

    companion object {
        private const val LOCATION_REQUEST = "LocationHandler.LOCATION_REQUEST"
    }

}