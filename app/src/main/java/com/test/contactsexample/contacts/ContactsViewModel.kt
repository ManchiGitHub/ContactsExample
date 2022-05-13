package com.test.contactsexample.contacts

import android.app.Application
import androidx.lifecycle.*
import com.test.contactsexample.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class ContactsViewModel @Inject constructor(
    application: Application,
    private val contactsHelper: ContactsHelper
) : AndroidViewModel(application) {

    val contacts: LiveData<ContactResult> = liveData(Dispatchers.IO) {

        emit(ContactResult.Loading)

        val contactList = contactsHelper.getContacts(getApplication())

        if (contactList.isEmpty()) emit(ContactResult.Failure(getApplication<Application>().getString(R.string.no_contacts_have_been_found)))
        else emit(ContactResult.Success(contactList))
    }
}

sealed class ContactResult {
    data class Success(val contacts: List<Contact>) : ContactResult()
    data class Failure(val message: String) : ContactResult()
    object Loading : ContactResult()
}