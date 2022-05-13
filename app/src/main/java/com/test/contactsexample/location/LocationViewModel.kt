package com.test.contactsexample.location

import android.app.Application
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(
    application: Application,
    locationUpdatesLiveData: LocationUpdatesUseCaseLiveData
) : AndroidViewModel(application) {

    val address: LiveData<String> = locationUpdatesLiveData
}