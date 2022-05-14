package com.test.contactsexample.location

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.test.contactsexample.contacts.ContactsFragment
import com.test.contactsexample.R
import com.test.contactsexample.databinding.FragmentLocationBinding
import com.test.contactsexample.utils.autoCleared
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LocationFragment : Fragment() {

    private val locationViewModel by viewModels<LocationViewModel>()
    private var binding: FragmentLocationBinding by autoCleared()

    private val locationHelper: LocationHelper by lazy {
        LocationHelper(requireActivity().activityResultRegistry)
    }

    private var snackbar: Snackbar? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLocationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // disable refresh by swipe. I just want the cool loading icon :)
        binding.fragmentLocationLayout.isEnabled = false

        revealLocationButton()

        binding.fab.setOnClickListener {
            attemptGetLocation()
        }
    }

    private fun attemptGetLocation() {

        lifecycleScope.launch {

            if (locationHelper.requestLocationPermission()) {
                attemptEnableLocation()
            } else {
                findNavController().navigate(R.id.action_location_to_locationDenied)
            }

        }
    }

    private suspend fun attemptEnableLocation() {

        val enabled = locationHelper.enableLocation(requireContext())

        if (enabled) {
            getLocationUpdates()
        } else {
            showLocationSnackbar((getString(R.string.location_activation_denied)))
        }
    }

    private fun getLocationUpdates() {

        // Normally I would observe a "loading" LiveData to control the refresh icon.
        binding.fragmentLocationLayout.isRefreshing = true

        locationViewModel.addressFlow.observe(viewLifecycleOwner) { address ->

            Log.d("LocationUpdatesUseCase", "getLocationUpdates: ")
            binding.fragmentLocationLayout.isRefreshing = false

            val bundle = Bundle().apply {
                putString(ContactsFragment.ADDRESS_KEY, address)
            }

            findNavController().navigate(R.id.action_location_to_contacts, bundle)
        }
    }

    private fun revealLocationButton() {

        if (binding.locationMotionLayout.currentState != R.id.center) {
            binding.locationMotionLayout.transitionToState(R.id.center)
        }
    }

    private fun showLocationSnackbar(message: String) {
        snackbar = Snackbar.make(
            binding.fab,
            message,
            Snackbar.LENGTH_INDEFINITE
        )
            .setTextColor(resources.getColor(R.color.black, null))
            .setBackgroundTint(resources.getColor(R.color.white, null))
            .setActionTextColor(resources.getColor(R.color.base_light_blue, null))
            .setAction(R.string.settings) {
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }

        snackbar?.show()
    }

    override fun onStop() {
        super.onStop()
        locationHelper.unregisterHandlers()

        Log.d("LocationUpdatesUseCase", "onStop: ")
        // Need to dismiss manually because snackbar duration is indefinite.
        // Perhaps using an indefinite snackbar wasn't the best choice, considering I need to take care of it.
        snackbar?.let { if (it.isShown) it.dismiss() }
    }
}