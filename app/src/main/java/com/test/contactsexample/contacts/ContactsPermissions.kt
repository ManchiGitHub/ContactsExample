package com.test.contactsexample.contacts

import android.Manifest
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import kotlin.coroutines.suspendCoroutine

/**
 * Created by Marko
 */

class ContactsPermissions(private val resultRegistry: ActivityResultRegistry) {

    private val handlers = mutableListOf<ActivityResultLauncher<*>>()

    suspend fun requestContactsPermission(): Boolean = suspendCoroutine { cont ->

        val launcher = resultRegistry.register(
            CONTACTS_REQUEST, ActivityResultContracts.RequestPermission()
        ) {
            cont.resumeWith(Result.success(it))
        }
        handlers.add(launcher)
        launcher.launch(Manifest.permission.READ_CONTACTS)
    }

    companion object {
        private const val CONTACTS_REQUEST = "ContactsUtil.CONTACTS_REQUEST"
    }

    fun unregisterHandlers() {
        handlers.forEach {
            it.unregister()
        }
    }
}