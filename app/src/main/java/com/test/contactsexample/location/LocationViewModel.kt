package com.test.contactsexample.location

import android.app.Application
import androidx.lifecycle.*
import com.test.contactsexample.utils.AppUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(
    application: Application,
    locationUpdatesUseCaseLiveData: LocationUpdatesUseCaseLiveData,
    locationUpdatesUseCase: LocationUpdatesUseCase,
) : AndroidViewModel(application) {

//    val address = locationUpdatesUseCaseLiveData

    val address = locationUpdatesUseCase.locationUpdates
        .take(1)
        .map { AppUtils.resolveAddress(application, it) }
        .flowOn(Dispatchers.IO)
        .asLiveData()

}

