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

    val currentAddress = locationUpdatesUseCaseLiveData

//    val address = locationUpdatesUseCase.locationUpdates
//        .map { AppUtils.resolveAddress(application, it) }
//        .take(10).flowOn(Dispatchers.IO).asLiveData()

    val address = locationUpdatesUseCase.locationUpdates.flowOn(Dispatchers.IO).asLiveData()
}

