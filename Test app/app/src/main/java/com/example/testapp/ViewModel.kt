package com.example.testapp

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = Repository(application)
    private val coursesAllMutableLiveData = MutableLiveData<List<Courses>>()
    private val courseByIdGetMutableLiveData = MutableLiveData<List<Courses>>()

    val coursesAllLiveData: LiveData<List<Courses>>
        get() = coursesAllMutableLiveData

    val courseByIdGetLiveData: LiveData<List<Courses>>
        get() = courseByIdGetMutableLiveData

    fun insertCourse(id: Long, title: String) {
        viewModelScope.launch {
            try {
                repository.insertCourse(id, title)
            } catch (t: Throwable) {
                Log.d("ViewModel", "error insert course", t)
            }
        }
    }

    fun getAllCourses() {
        viewModelScope.launch {
            try {
                coursesAllMutableLiveData.postValue(repository.getAllCourses())
            } catch (t: Throwable) {
                coursesAllMutableLiveData.postValue(emptyList())
                Log.d("ViewModel", "error get all courses", t)
            }
        }
    }

    fun getCourseById(idCourse: Long) {
        viewModelScope.launch {
            try {
                courseByIdGetMutableLiveData.postValue(repository.getCourseById(idCourse))
            } catch (t: Throwable) {
                courseByIdGetMutableLiveData.postValue(emptyList())
                Log.d("ViewModel", "error get course by id", t)
            }
        }
    }

    fun updateCourseById(idCourse: Long, title: String) {
        viewModelScope.launch {
            try {
                repository.updateCourse(idCourse, title)
            } catch (t: Throwable) {
                Log.d("ViewModel", "error update course", t)
            }
        }
    }

    fun deleteCourseById(idCourse: Long) {
        viewModelScope.launch {
            try {
                repository.deleteCourseById(idCourse)
            } catch (t: Throwable) {
                Log.d("ViewModel", "error delete course", t)
            }
        }
    }

    fun deleteAllCourses() {
        viewModelScope.launch {
            try {
                repository.deleteAllCourses()
            } catch (t: Throwable) {
                Log.d("ViewModel", "error delete all courses", t)
            }
        }
    }
}