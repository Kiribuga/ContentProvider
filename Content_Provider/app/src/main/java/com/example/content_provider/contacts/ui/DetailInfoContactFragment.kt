package com.example.content_provider.contacts.ui

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.content_provider.R
import com.example.content_provider.contacts.data.AdapterInfoContact
import com.example.content_provider.contacts.data.ViewModelContacts
import com.example.content_provider.databinding.ContactInfoDetailBinding
import com.example.content_provider.utils.autoCleared
import permissions.dispatcher.PermissionRequest
import permissions.dispatcher.ktx.constructPermissionsRequest

class DetailInfoContactFragment: Fragment(R.layout.contact_info_detail) {

    private var fragmentBind: ContactInfoDetailBinding? = null
    private val viewModel by viewModels<ViewModelContacts>()
    private var contactAdapter: AdapterInfoContact by autoCleared()
    private val args: DetailInfoContactFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = ContactInfoDetailBinding.bind(view)
        fragmentBind = binding
        initToolbar()
        initList()
        bindViewModel()
        fragmentBind?.removeContact?.setOnClickListener {
            viewModel.deleteContact(args.idContact)
        }
        Handler(Looper.getMainLooper()).post {
            constructPermissionsRequest(
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.WRITE_CONTACTS,
                onShowRationale = ::onContactPermissionShowRationale,
                onPermissionDenied = ::onContactPermissionDenied,
                onNeverAskAgain = ::onContactPermissionNeverAskAgain,
                requiresPermission = { viewModel.loadDetailContact(args.idContact, args.nameContact) }
            ).launch()
        }
    }

    private fun waitDelete(wait: Boolean) {
        fragmentBind?.loader?.isVisible = wait
        fragmentBind?.removeContact?.isEnabled = !wait
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun bindViewModel() {
        viewModel.contactDetail.observe(viewLifecycleOwner) {
            contactAdapter.updateListContacts(viewModel.contactDetail.value!!)
            contactAdapter.notifyDataSetChanged()
        }
        viewModel.loaderLiveData.observe(viewLifecycleOwner) {
            waitDelete(it)
            findNavController().popBackStack()
        }
        viewModel.contactDelete.observe(viewLifecycleOwner) { result ->
            toastAfterDeleted(result)
        }
    }

    private fun toastAfterDeleted(result: Boolean) {
        if (result) {
            Toast.makeText(requireContext(), "Contact was deleted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Contact wasn't deleted", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun initList() {
        contactAdapter = AdapterInfoContact()
        with(fragmentBind?.infoContact) {
            this?.adapter = contactAdapter
            this?.setHasFixedSize(true)
            this?.layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun initToolbar() {
        (activity as AppCompatActivity).setSupportActionBar(activity?.findViewById(R.id.toolbar))
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.detail_info)
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