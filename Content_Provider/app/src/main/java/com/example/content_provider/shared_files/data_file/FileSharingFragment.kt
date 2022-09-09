package com.example.content_provider.shared_files.data_file

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.content_provider.BuildConfig
import com.example.content_provider.R
import com.example.content_provider.databinding.FragmentSharingFileBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class FileSharingFragment : Fragment(R.layout.fragment_sharing_file) {

    private var frBind: FragmentSharingFileBinding? = null
    private val viewModelFile: ViewModelFile by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentSharingFileBinding.bind(view)
        frBind = binding
        initToolbar()
        observer()
        frBind?.buttonView?.setOnClickListener {
            viewModelFile.loadFile(
                binding.editTextView.text.toString(),
                requireContext()
            )
        }
        frBind?.sharingButton?.setOnClickListener {
            sharedFile()
        }
    }

    private fun observer() {
        viewModelFile.toastDownload.observe(viewLifecycleOwner) { result ->
            Toast.makeText(requireContext(), result, Toast.LENGTH_SHORT).show()
        }
        viewModelFile.loader.observe(viewLifecycleOwner, ::waitLoader)
    }

    private fun waitLoader(load: Boolean) {
        frBind?.loaderView?.isVisible = load
        frBind?.buttonView?.isEnabled = !load
        frBind?.editTextView?.isEnabled = !load
        frBind?.sharingButton?.isEnabled = !load
    }

    private fun initToolbar() {
        (activity as AppCompatActivity).setSupportActionBar(activity?.findViewById(R.id.toolbar))
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.name_share_fragment)
    }

    private fun sharedFile() {
        lifecycleScope.launch(Dispatchers.IO) {
            if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED) return@launch
            val dir = requireContext().getExternalFilesDir("saved_files")
            val file = File(dir, "text_file.txt")
            if (file.exists().not()) return@launch
            val uri = FileProvider.getUriForFile(
                requireContext(),
                "${BuildConfig.APPLICATION_ID}.file_provider",
                file
            )
            val intent = Intent(Intent.ACTION_SEND).apply {
                putExtra(Intent.EXTRA_STREAM, uri)
                type = requireContext().contentResolver.getType(uri)
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            }
            val shareIntent = Intent.createChooser(intent, null)
            startActivity(shareIntent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        frBind = null
    }
}