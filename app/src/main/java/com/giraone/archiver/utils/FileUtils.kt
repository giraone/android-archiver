package com.giraone.archiver.utils

import android.content.Context
import android.webkit.MimeTypeMap
import com.giraone.archiver.R
import com.giraone.archiver.data.FileType
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

object FileUtils {

    fun formatFileSize(context: Context, sizeInBytes: Long): String {
        return when {
            sizeInBytes < 1024 -> context.getString(R.string.bytes, sizeInBytes)
            sizeInBytes < 1024 * 1024 -> context.getString(R.string.kb, sizeInBytes / 1024.0)
            sizeInBytes < 1024 * 1024 * 1024 -> context.getString(R.string.mb, sizeInBytes / (1024.0 * 1024.0))
            else -> context.getString(R.string.gb, sizeInBytes / (1024.0 * 1024.0 * 1024.0))
        }
    }

    fun formatDateTime(dateTime: LocalDateTime): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        return dateTime.format(formatter)
    }

    fun getFileTypeFromMimeType(mimeType: String?): FileType {
        return when {
            mimeType?.startsWith("image/") == true -> FileType.IMAGE
            mimeType == "text/markdown" -> FileType.MARKDOWN
            mimeType?.startsWith("text/") == true -> FileType.TEXT
            mimeType == "application/pdf" -> FileType.TEXT
            else -> FileType.OTHER
        }
    }

    fun getFileTypeFromFileName(fileName: String, mimeType: String?): FileType {
        // Check file extension for markdown files since MIME type might not be reliable
        val extension = fileName.substringAfterLast('.', "").lowercase()
        return when {
            extension in listOf("md", "markdown") -> FileType.MARKDOWN
            else -> getFileTypeFromMimeType(mimeType)
        }
    }

    fun getFileExtensionFromMimeType(mimeType: String?): String {
        if (mimeType == null) return ""
        val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
        return if (extension != null) ".$extension" else ""
    }

    fun generateUniqueFileName(originalFileName: String, mimeType: String?): String {
        val uuid = UUID.randomUUID().toString()
        val extension = getFileExtensionFromMimeType(mimeType)
        return if (extension.isNotEmpty()) {
            "$uuid-$originalFileName$extension"
        } else {
            "$uuid-$originalFileName"
        }
    }

    fun isTextFile(mimeType: String?): Boolean {
        return mimeType?.startsWith("text/") == true || mimeType == "application/pdf"
    }

    fun isImageFile(mimeType: String?): Boolean {
        return mimeType?.startsWith("image/") == true
    }
}