package com.example.content_provider.contacts.ui

import android.Manifest
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.content_provider.R
import com.example.content_provider.contacts.data.AdapterContacts
import com.example.content_provider.contacts.data.ViewModelContacts
import com.example.content_provider.databinding.ContactsFragmentBinding
import com.example.content_provider.utils.autoCleared
import permissions.dispatcher.PermissionRequest
import permissions.dispatcher.ktx.constructPermissionsRequest

class FragmentContacts : Fragment(R.layout.contacts_fragment) {

    private var fragmentBind: ContactsFragmentBinding? = null
    private val viewModel by viewModels<ViewModelContacts>()
    private var contactAdapter: AdapterContacts by autoCleared()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = ContactsFragmentBinding.bind(view)
        fragmentBind = binding
        initToolbar()
        initList()
        fragmentBind?.fabInsert?.setOnClickListener {
            val action =
                FragmentContactsDirections.actionFragmentContactsToAddContactFragment()
            findNavController().navigate(action)
        }

        Handler(Looper.getMainLooper()).post {
            constructPermissionsRequest(
                Manifest.permission.READ_CONTACTS,
                onShowRationale = ::onContactPermissionShowRationale,
                onPermissionDenied = ::onContactPermissionDenied,
                onNeverAskAgain = ::onContactPermissionNeverAskAgain,
                requiresPermission = { viewModel.loadList() }
            ).launch()
        }
        bindViewModel()
    }

    private fun bindViewModel() {
        viewModel.contactsLiveData.observe(viewLifecycleOwner) {
            contactAdapter.updateListContacts(it)
        }
    }

    private fun initList() {
        contactAdapter = AdapterContacts { contact ->
            val action =
                FragmentContactsDirections.actionFragmentContactsToDetailInfoContactFragment(
                    contact.id,
                    contact.name
                )
            findNavController().navigate(action)
        }
        with(fragmentBind?.listContacts) {
            this?.adapter = contactAdapter
            this?.setHasFixedSize(true)
            this?.layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun initToolbar() {
        (activity as AppCompatActivity).setSupportActionBar(activity?.findViewById(R.id.toolbar))
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.name_fragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentBind = null
    }

    private fun onContactPermissionDenied() {
        Toast.makeText(requireContext(), getString(R.string.denied_permission), Toast.LENGTH_SHORT).show()
    }

    private fun onContactPermissionShowRationale(request: PermissionRequest) {
        request.proceed()
    }

    private fun onContactPermissionNeverAskAgain() {
        Toast.makeText(requireContext(), getString(R.string.never_permission), Toast.LENGTH_SHORT).show()
    }
}