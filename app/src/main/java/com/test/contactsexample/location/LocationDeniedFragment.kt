package com.test.contactsexample.location

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.test.contactsexample.R
import com.test.contactsexample.databinding.FragmentLocationDeniedBinding
import com.test.contactsexample.utils.AppUtils
import com.test.contactsexample.utils.autoCleared
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * Created by Marko
 */
@AndroidEntryPoint
class LocationDeniedFragment : Fragment() {

    private var binding: FragmentLocationDeniedBinding by autoCleared()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLocationDeniedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.permissionBtn.setOnClickListener {
            AppUtils.openAppSettingsScreen(requireContext())
        }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {

            val permissionGranted =
                ContextCompat.checkSelfPermission(
                    requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

            if (permissionGranted) {
                findNavController().navigate(R.id.action_locationDenied_to_location)
            }
        }
    }
}