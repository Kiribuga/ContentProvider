package com.example.content_provider

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.content_provider.databinding.FragmentMainBinding

class MainFragment : Fragment(R.layout.fragment_main) {

    private var frBind: FragmentMainBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentMainBinding.bind(view)
        frBind = binding
        initToolbar()
        frBind?.buttonContact?.setOnClickListener {
            findNavController().navigate(MainFragmentDirections.actionMainFragmentToFragmentContacts())
        }
        frBind?.buttonShareFile?.setOnClickListener {
            findNavController().navigate(MainFragmentDirections.actionMainFragmentToFileSharingFragment())
        }
    }

    private fun initToolbar() {
        (activity as AppCompatActivity).setSupportActionBar(activity?.findViewById(R.id.toolbar))
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.name_main_fragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        frBind = null
    }
}