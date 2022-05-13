package com.test.contactsexample.contacts

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.test.contactsexample.R
import com.test.contactsexample.databinding.FragmentContactsBinding
import com.test.contactsexample.utils.AppUtils
import com.test.contactsexample.utils.autoCleared
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ContactsFragment : Fragment() {

    private var binding: FragmentContactsBinding by autoCleared()
    private val viewModel: ContactsViewModel by viewModels()

    private lateinit var _adapter: MyContactsAdapter

    private val contactsPermissions: ContactsPermissions by lazy {
        ContactsPermissions(requireActivity().activityResultRegistry)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentContactsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecycler()

        arguments?.let {
            binding.addressTv.text = it.getString(ADDRESS_KEY, getString(R.string.address_not_available))
        }

        binding.fab.setOnClickListener {
            getContactsOrSnackbar()
        }
    }

    private fun getContactsOrSnackbar() {
        lifecycleScope.launch {
            val granted = contactsPermissions.requestContactsPermission()
            if (granted) getContacts() else {

                showSnackbar(R.string.grant_access_to_contacts, R.string.settings) {
                    AppUtils.openAppSettingsScreen(requireContext())
                }
            }
        }
    }


    private fun showSnackbar(
        @StringRes stringID: Int,
        @StringRes buttonText: Int = R.string.ok,
        duration: Int = Snackbar.LENGTH_LONG,
        listener: View.OnClickListener
    ) {
        Snackbar.make(
            binding.addressTv,
            getString(stringID),
            duration
        )
            .setAction(buttonText, listener).show()
    }

    private fun getContacts() {

        viewModel.contacts.observe(viewLifecycleOwner) { result ->
            when (result) {
                ContactResult.Loading -> {
                    //TODO
                }
                is ContactResult.Success -> {
                    _adapter.setList(result.contacts)

                    // Important for recyclerView item animation
                    binding.contactsRecyclerView.scheduleLayoutAnimation()

                    binding.contactsLayout.transitionToState(R.id.fin)
                    binding.fab.hide()
                }
                is ContactResult.Failure -> {
                    Snackbar.make(binding.addressTv, result.message, Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun initRecycler() {

        _adapter = MyContactsAdapter(listOf()) { contact -> sendLocationTo(contact) }

        binding.contactsRecyclerView.apply {
            adapter = _adapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun sendLocationTo(contact: Contact) {
        val sendIntent = Intent(Intent.ACTION_SENDTO)
        sendIntent.data = Uri.parse("smsto:${contact.phoneNumber}")
        sendIntent.putExtra("sms_body", binding.addressTv.text)
        startActivity(Intent.createChooser(sendIntent, getString(R.string.choose_app)))
    }

    companion object {
        const val ADDRESS_KEY = "ContactsFragment.contactKey"
    }

    override fun onStop() {
        super.onStop()
        contactsPermissions.unregisterHandlers()
    }
}

