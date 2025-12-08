package com.practicum.myapplication.data.db

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ImageFileManager(private val context: Context) {

    private val imagesDir: File by lazy {
        File(context.filesDir, "playlist_covers").apply {
            if (!exists()) mkdirs()
        }
    }
    suspend fun copyImageToAppStorage(uri: Uri): Uri? {
        return try {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "playlist_cover_$timeStamp.jpg"
            val destinationFile = File(imagesDir, fileName)

            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                FileOutputStream(destinationFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            Uri.fromFile(destinationFile)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    fun getUriFromPath(path: String?): Uri? {
        if (path.isNullOrEmpty()) return null
        return try {
            val file = File(path)
            if (file.exists()) Uri.fromFile(file) else null
        } catch (e: Exception) {
            null
        }
    }

}