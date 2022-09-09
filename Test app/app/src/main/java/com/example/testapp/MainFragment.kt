package com.example.testapp

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.testapp.databinding.FragmentMainBinding

class MainFragment : Fragment(R.layout.fragment_main) {

    private var frBind: FragmentMainBinding? = null
    private val viewModel: ViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentMainBinding.bind(view)
        frBind = binding
        frBind?.getListCourses?.setOnClickListener {
            viewModel.getAllCourses()
        }
        frBind?.insertCourse?.setOnClickListener {
            viewModel.insertCourse(
                frBind?.insertIdCourseEditText?.text.toString().toLong(),
                frBind?.insertTitleCourseEditText?.text.toString()
            )
        }
        frBind?.getCourseById?.setOnClickListener {
            viewModel.getCourseById(
                frBind?.getCourseByIdEditText?.text.toString().toLong()
            )
        }
        frBind?.updateCourseById?.setOnClickListener {
            viewModel.updateCourseById(
                frBind?.updateCourseByIdEditText?.text.toString().toLong(),
                frBind?.updateCourseTitleEditText?.text.toString()
            )
        }
        frBind?.deleteCourseById?.setOnClickListener {
            viewModel.deleteCourseById(
                frBind?.deleteCourseByIdEditText?.text.toString().toLong()
            )
        }
        frBind?.deleteAllCourses?.setOnClickListener {
            viewModel.deleteAllCourses()
        }
        observer()
    }

    private fun observer() {
        viewModel.coursesAllLiveData.observe(viewLifecycleOwner) { result ->
            resultWindow(result)
        }
        viewModel.courseByIdGetLiveData.observe(viewLifecycleOwner) { result ->
            resultWindow(result)
        }
    }

    private fun resultWindow(result: List<Courses>) {
        frBind?.resultWindow?.isVisible = true
        frBind?.resultWindow?.text = result.toString()
    }

    override fun onDestroy() {
        super.onDestroy()
        frBind = null
    }
}