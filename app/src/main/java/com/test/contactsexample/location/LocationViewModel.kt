package com.test.contactsexample.location

import android.app.Application
import android.location.Geocoder
import androidx.lifecycle.*
import com.google.android.gms.location.LocationResult
import com.test.contactsexample.utils.AppUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(
    application: Application,
    locationUpdatesLiveData: LocationUpdatesUseCaseLiveData,
    locationUpdatesUseCase: LocationUpdatesUseCase
) : AndroidViewModel(application) {

    val address: LiveData<String> = locationUpdatesLiveData


    val addressFlow = locationUpdatesUseCase.fetchUpdates()

        // Normally, At this point I would save the new location to a database.
        .map { AppUtils.resolveAddress(application, it) }

        // affects only preceding operators that do not have their own context.
        .flowOn(Dispatchers.IO)

        // Flow behaves like sequence...
        // This will cancel the flow.
        .take(1)

        // Conveniently convert to liveData.
        .asLiveData()

}