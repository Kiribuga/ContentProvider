package com.example.content_provider.contacts.ui

import android.Manifest
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.content_provider.R
import com.example.content_provider.contacts.data.ViewModelAdd
import com.example.content_provider.databinding.AddContactFragmentBinding
import permissions.dispatcher.PermissionRequest
import permissions.dispatcher.ktx.constructPermissionsRequest

class AddContactFragment : Fragment(R.layout.add_contact_fragment) {

    private var fragmentBind: AddContactFragmentBinding? = null
    private val viewModel by viewModels<ViewModelAdd>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = AddContactFragmentBinding.bind(view)
        fragmentBind = binding
        fragmentBind?.addButtonContact?.isEnabled = false
        initToolbar()

        Handler(Looper.getMainLooper()).post {
            constructPermissionsRequest(
                Manifest.permission.WRITE_CONTACTS,
                onShowRationale = ::onContactPermissionShowRationale,
                onPermissionDenied = ::onContactPermissionDenied,
                onNeverAskAgain = ::onContactPermissionNeverAskAgain,
                requiresPermission = { saveContactButton() }
            ).launch()
        }

        fragmentBind?.nameContactEditText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                fragmentBind?.addButtonContact?.isEnabled = p0?.isNotEmpty() ?: false &&
                        fragmentBind?.lastNameContactEditText?.length() != 0 &&
                        fragmentBind?.phoneContactEditText?.length() != 0
            }
        })
        fragmentBind?.lastNameContactEditText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                fragmentBind?.addButtonContact?.isEnabled = p0?.isNotEmpty() ?: false &&
                        fragmentBind?.nameContactEditText?.length() != 0 &&
                        fragmentBind?.phoneContactEditText?.length() != 0
            }
        })
        fragmentBind?.phoneContactEditText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                fragmentBind?.addButtonContact?.isEnabled = p0?.isNotEmpty() ?: false &&
                        fragmentBind?.lastNameContactEditText?.length() != 0 &&
                        fragmentBind?.nameContactEditText?.length() != 0
            }
        })
    }

    private fun saveContactButton() {
        fragmentBind?.addButtonContact?.setOnClickListener {
            viewModel.saveContact(
                fragmentBind?.nameContactEditText?.text.toString() + " " +
                        fragmentBind?.lastNameContactEditText?.text.toString(),
                fragmentBind?.phoneContactEditText?.text.toString(),
                fragmentBind?.emailContactEditText?.text.toString()
            )
        }
        bindViewModel()
    }

    private fun bindViewModel() {
        viewModel.errorLiveData.observe(viewLifecycleOwner) {
            Toast.makeText(
                requireContext(),
                "Contact wasn't added, error = $it",
                Toast.LENGTH_SHORT
            ).show()
            findNavController().popBackStack()
        }
        viewModel.resultLiveData.observe(viewLifecycleOwner) {
            if (it) {
                Toast.makeText(requireContext(), "Contact was added", Toast.LENGTH_SHORT).show()
            }
            findNavController().popBackStack()
        }
    }

    private fun initToolbar() {
        (activity as AppCompatActivity).setSupportActionBar(activity?.findViewById(R.id.toolbar))
        (activity as AppCompatActivity).supportActionBar?.title =
            getString(R.string.add_contact_toolbar)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentBind = null
    }

    private fun onContactPermissionDenied() {
        Toast.makeText(requireContext(), getString(R.string.denied_permission), Toast.LENGTH_SHORT)
            .show()
    }

    private fun onContactPermissionShowRationale(request: PermissionRequest) {
        request.proceed()
    }

    private fun onContactPermissionNeverAskAgain() {
        Toast.makeText(requireContext(), getString(R.string.never_permission), Toast.LENGTH_SHORT)
            .show()
    }
}