package com.example.content_provider.shared_files.data_file

import android.content.Context
import android.os.Environment
import android.util.Log
import com.example.content_provider.R
import com.example.content_provider.shared_files.network.NetworkFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class RepositoryFile {
    suspend fun downloadFile(link: String, context: Context): String {
        return suspendCoroutine { continuation ->
            CoroutineScope(Dispatchers.IO).launch {
                if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED) return@launch
                val sharedPrefs = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
                val folder = context.getExternalFilesDir("saved_files")
                val file = File(
                    folder,
                    "text_file.txt"
                )
                try {
                    if (sharedPrefs.contains(link)) {
                        continuation.resume(context.getString(R.string.file_already_download))
                    } else {
                        file.outputStream().use { outputStream ->
                            NetworkFile.api
                                .getFile(link)
                                .byteStream()
                                .use { inputStream ->
                                    inputStream.copyTo(outputStream)
                                }
                        }
                        continuation.resume(context.getString(R.string.download_successfully))
                        sharedPrefs.edit()
                            .putString(link, file.name)
                            .commit()
                    }
                } catch (t: Throwable) {
                    Log.d("RepositoryFile", "Error download", t)
                    continuation.resumeWithException(t)
                    file.delete()
                }
            }
        }
    }
    companion object {
        const val SHARED_PREFS = "skillbox_shared_prefs"
    }
}