package com.example.content_provider.contacts.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ViewModelAdd(application: Application) : AndroidViewModel(application) {

    private val contactRepository = RepositoryContact(application)
    private val contactMutableLiveData = MutableLiveData<Unit>()
    private val errorMutableLiveData = MutableLiveData<String>()
    private val resultMutableLiveData = MutableLiveData<Boolean>()

    val errorLiveData: LiveData<String>
        get() = errorMutableLiveData

    val resultLiveData: LiveData<Boolean>
        get() = resultMutableLiveData

    fun saveContact(name: String, phone: String, email: String) {
        viewModelScope.launch {
            try {
                contactMutableLiveData.postValue(contactRepository.saveContact(name, phone, email))
                resultMutableLiveData.postValue(true)
            } catch (t: Throwable) {
                errorMutableLiveData.postValue(t.toString())
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        resultMutableLiveData.postValue(false)
    }
}