package com.giraone.archiver.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.time.LocalDateTime

class ContentHandler(private val context: Context) {

    data class FileData(
        val fileName: String,
        val mimeType: String?,
        val size: Long,
        val inputStream: InputStream
    )

    fun extractFileDataFromIntent(intent: Intent): List<FileData> {
        val fileDataList = mutableListOf<FileData>()

        when (intent.action) {
            Intent.ACTION_SEND -> {
                val uri = intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)
                uri?.let { fileDataList.add(extractFileDataFromUri(it)) }
            }
            Intent.ACTION_SEND_MULTIPLE -> {
                val uris = intent.getParcelableArrayListExtra<Uri>(Intent.EXTRA_STREAM)
                uris?.forEach { uri ->
                    fileDataList.add(extractFileDataFromUri(uri))
                }
            }
        }

        return fileDataList
    }

    private fun extractFileDataFromUri(uri: Uri): FileData {
        val contentResolver = context.contentResolver
        var fileName = "unknown"
        var size = 0L

        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)

                if (nameIndex != -1) {
                    fileName = cursor.getString(nameIndex) ?: "unknown"
                }
                if (sizeIndex != -1) {
                    size = cursor.getLong(sizeIndex)
                }
            }
        }

        val mimeType = contentResolver.getType(uri)
        val inputStream = contentResolver.openInputStream(uri)
            ?: throw IllegalArgumentException("Cannot open input stream for URI: $uri")

        return FileData(fileName, mimeType, size, inputStream)
    }

    fun saveFileToPrivateDirectory(fileData: FileData): File {
        val filesDir = File(context.filesDir, "archived_files")
        if (!filesDir.exists()) {
            filesDir.mkdirs()
        }

        val uniqueFileName = FileUtils.generateUniqueFileName(fileData.fileName, fileData.mimeType)
        val file = File(filesDir, uniqueFileName)

        FileOutputStream(file).use { outputStream ->
            fileData.inputStream.use { inputStream ->
                inputStream.copyTo(outputStream)
            }
        }

        return file
    }
}