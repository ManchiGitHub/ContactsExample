package com.test.contactsexample.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings

/**
 * Created by Marko
 */
class AppUtils {

    companion object{

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
    }

}