package com.example.content_provider.shared_files.data_file

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ViewModelFile : ViewModel() {

    private val repository = RepositoryFile()
    private val toastDownloadLiveData = MutableLiveData<String>()
    private val loaderLiveData = MutableLiveData<Boolean>()

    val toastDownload: LiveData<String>
        get() = toastDownloadLiveData

    val loader: LiveData<Boolean>
        get() = loaderLiveData

    fun loadFile(link: String, context: Context) {
        viewModelScope.launch {
            loaderLiveData.postValue(true)
            try {
                val result = repository.downloadFile(link, context)
                toastDownloadLiveData.postValue(result)
            } catch (t: Throwable) {
                Log.d("ViewModelFiles", "Error", t)
            } finally {
                loaderLiveData.postValue(false)
            }
        }
    }
}