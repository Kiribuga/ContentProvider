package com.example.content_provider.contacts.data

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*

class ViewModelContacts(application: Application) : AndroidViewModel(application) {

    private val contactRepository = RepositoryContact(application)
    private val contactMutableLiveData = MutableLiveData<List<Contacts>>()
    private val contactDetailMutableLiveData = MutableLiveData<List<Contacts>>()
    private val contactDeleteMutableLiveData = MutableLiveData<Boolean>()
    private val loaderMutableLiveData = MutableLiveData<Boolean>()

    val contactsLiveData: LiveData<List<Contacts>>
        get() = contactMutableLiveData

    val contactDetail: LiveData<List<Contacts>>
        get() = contactDetailMutableLiveData

    val contactDelete: LiveData<Boolean>
        get() = contactDeleteMutableLiveData

    val loaderLiveData: LiveData<Boolean>
        get() = loaderMutableLiveData

    fun loadList() {
        viewModelScope.launch {
            try {
                contactMutableLiveData.postValue(contactRepository.getAllContacts())
            } catch (t: Throwable) {
                Log.d("ViewModelContacts", "contact list error", t)
                contactMutableLiveData.postValue(emptyList())
            }
        }
    }

    fun loadDetailContact(id: Long, name: String) {
        viewModelScope.launch {
            try {
                contactDetailMutableLiveData.postValue(contactRepository.getDataContact(id, name))
            } catch (t: Throwable) {
                Log.d("ViewModelContacts", "contact detail error", t)
                contactDetailMutableLiveData.postValue(emptyList())
            }
        }
    }

    fun deleteContact(idContact: Long) {
        viewModelScope.launch {
            try {
                loaderMutableLiveData.postValue(true)
                contactDeleteMutableLiveData.postValue(contactRepository.deleteContact(idContact))
            } catch (t: Throwable) {
                Log.d("ViewModelContacts", "delete contact error", t)
            } finally {
                loaderMutableLiveData.postValue(false)
            }
        }
    }
}