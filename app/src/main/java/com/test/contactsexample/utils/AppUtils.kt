package com.test.contactsexample.utils

import android.content.Context
import android.content.Intent
import android.location.Geocoder
import android.net.Uri
import android.provider.Settings
import com.google.android.gms.location.LocationResult

class AppUtils {

    companion object {

        fun openAppSettingsScreen(context: Context) {
            val uri = Uri.fromParts(
                "package",
                context.packageName,
                null
            )

            val intent = Intent().apply {
                this.data = uri
                this.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                this.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            }

            context.startActivity(intent)
        }

        fun resolveAddress(context: Context, location: LocationResult): String {

            val geocoder = Geocoder(context)

            val addresses = geocoder.getFromLocation(
                location.lastLocation.latitude,
                location.lastLocation.longitude,
                1
            )

            return addresses[0].getAddressLine(0)
        }
    }

}