package com.example.testapp

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class Repository(
    private val context: Context
) {

    private val authorities = Uri.parse(
        "content://com.example.content_provider.provider/courses"
    )

    companion object {
        const val TITLE = "title"
        const val ID_COURSE = "id"
    }

    suspend fun getAllCourses(): List<Courses> = withContext(Dispatchers.IO) {
        context.contentResolver.query(
            authorities,
            null,
            null,
            null,
            null
        )?.use {
            getCoursesFromCursor(it)
        }.orEmpty()
    }

    private fun getCoursesFromCursor(cursor: Cursor): List<Courses> {
        if (cursor.moveToFirst().not()) return emptyList()
        val listCourses = mutableListOf<Courses>()
        do {
            val titleIndex = cursor.getColumnIndex(TITLE)
            val title = cursor.getString(titleIndex).orEmpty()

            val idIndex = cursor.getColumnIndex(ID_COURSE)
            val id = cursor.getLong(idIndex)

            listCourses.add(Courses(id, title))
        } while (cursor.moveToNext())
        return listCourses
    }

    suspend fun insertCourse(idCourse: Long, title: String) = withContext(Dispatchers.IO) {
        val contentValues = ContentValues().apply {
            put(ID_COURSE, idCourse)
            put(TITLE, title)
        }
        context.contentResolver.insert(authorities, contentValues)
    }

    suspend fun getCourseById(idCourse: Long) = withContext(Dispatchers.IO) {
        context.contentResolver.query(
            authorities,
            null,
            null,
            null,
            null
        )?.use {
            getCourse(it, idCourse)
        }.orEmpty()
    }

    private fun getCourse(cursor: Cursor, idCourse: Long): List<Courses> {
        if (cursor.moveToFirst().not()) return emptyList()
        val course = mutableListOf<Courses>()
        do {
            val titleIndex = cursor.getColumnIndex(TITLE)
            val title = cursor.getString(titleIndex).orEmpty()

            val idIndex = cursor.getColumnIndex(ID_COURSE)
            val id = cursor.getLong(idIndex)
            if (id == idCourse) {
                course.add(Courses(id, title))
            }
        } while (cursor.moveToNext())
        return course
    }

    suspend fun updateCourse(idCourse: Long, title: String) = withContext(Dispatchers.IO) {
        val contentValues = ContentValues().apply {
            put(ID_COURSE, idCourse)
            put(TITLE, title)
        }
        context.contentResolver.update(
            Uri.parse("$authorities/$idCourse"),
            contentValues,
            null,
            null
        )
    }

    suspend fun deleteCourseById(idCourse: Long) = withContext(Dispatchers.IO) {
        context.contentResolver.query(
            authorities,
            null,
            null,
            null,
            null
        )?.use {
            deleteCourseByCursor(it, idCourse)
        }
    }

    private fun deleteCourseByCursor(cursor: Cursor, idCourse: Long) {
        if (cursor.moveToFirst().not()) return
        do {
            val idIndex = cursor.getColumnIndex(ID_COURSE)
            val id = cursor.getLong(idIndex)
            val uri = Uri.withAppendedPath(
                authorities, "$id"
            )
            if (id == idCourse) {
                context.contentResolver.delete(uri, null, null)
            }
        } while (cursor.moveToNext())
    }

    suspend fun deleteAllCourses() = withContext(Dispatchers.IO) {
        context.contentResolver.delete(authorities, null, null)
    }
}