package com.giraone.archiver.data

import android.content.Context
import com.giraone.archiver.utils.FileUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext
import java.io.File
import java.time.LocalDateTime

class FileRepository(
    private val context: Context,
    private val preferencesManager: PreferencesManager
) {

    private val _fileItems = MutableStateFlow<List<FileItem>>(emptyList())

    val fileItems: Flow<List<FileItem>> = combine(
        _fileItems.asStateFlow(),
        preferencesManager.sortOrderFlow
    ) { files, sortOrder ->
        when (sortOrder) {
            SortOrder.DATE -> files.sortedByDescending { it.storageDateTime }
            SortOrder.FILENAME -> files.sortedBy { it.fileName }
        }
    }

    suspend fun loadFiles() {
        withContext(Dispatchers.IO) {
            val filesDir = File(context.filesDir, "archived_files")
            if (!filesDir.exists()) {
                filesDir.mkdirs()
                _fileItems.value = emptyList()
                return@withContext
            }

            val fileItems = filesDir.listFiles()?.mapNotNull { file ->
                try {
                    parseFileItem(file)
                } catch (e: Exception) {
                    null
                }
            } ?: emptyList()

            _fileItems.value = fileItems
        }
    }

    private fun parseFileItem(file: File): FileItem? {
        if (!file.isFile) return null

        val fileName = file.name
        val parts = fileName.split("-", limit = 2)

        if (parts.size < 2) return null

        val uuid = parts[0]
        val originalFileName = parts[1]

        val contentType = guessContentTypeFromFileName(originalFileName)
        val fileType = FileUtils.getFileTypeFromFileName(originalFileName, contentType)

        return FileItem(
            id = uuid,
            fileName = originalFileName,
            filePath = file.absolutePath,
            contentType = contentType ?: "application/octet-stream",
            sizeInBytes = file.length(),
            storageDateTime = LocalDateTime.ofEpochSecond(
                file.lastModified() / 1000, 0, java.time.ZoneOffset.systemDefault().rules.getOffset(java.time.Instant.ofEpochMilli(file.lastModified()))
            ),
            fileType = fileType
        )
    }

    private fun guessContentTypeFromFileName(fileName: String): String? {
        val extension = fileName.substringAfterLast(".", "").lowercase()
        return when (extension) {
            "txt" -> "text/plain"
            "pdf" -> "application/pdf"
            "md", "markdown" -> "text/markdown"
            "jpg", "jpeg" -> "image/jpeg"
            "png" -> "image/png"
            "gif" -> "image/gif"
            "bmp" -> "image/bmp"
            "webp" -> "image/webp"
            else -> "application/octet-stream"
        }
    }

    suspend fun addFile(fileData: com.giraone.archiver.utils.ContentHandler.FileData): FileItem {
        return withContext(Dispatchers.IO) {
            val contentHandler = com.giraone.archiver.utils.ContentHandler(context)
            val savedFile = contentHandler.saveFileToPrivateDirectory(fileData)

            val fileItem = FileItem(
                id = savedFile.name.split("-").first(),
                fileName = fileData.fileName,
                filePath = savedFile.absolutePath,
                contentType = fileData.mimeType ?: "application/octet-stream",
                sizeInBytes = fileData.size,
                storageDateTime = LocalDateTime.now(),
                fileType = FileUtils.getFileTypeFromFileName(fileData.fileName, fileData.mimeType)
            )

            val currentFiles = _fileItems.value.toMutableList()
            currentFiles.add(fileItem)
            _fileItems.value = currentFiles

            fileItem
        }
    }

    suspend fun deleteFile(fileItem: FileItem): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val file = File(fileItem.filePath)
                val deleted = file.delete()

                if (deleted) {
                    val currentFiles = _fileItems.value.toMutableList()
                    currentFiles.removeIf { it.id == fileItem.id }
                    _fileItems.value = currentFiles
                }

                deleted
            } catch (e: Exception) {
                false
            }
        }
    }

    suspend fun renameFile(fileItem: FileItem, newFileName: String): FileItem? {
        return withContext(Dispatchers.IO) {
            try {
                val oldFile = File(fileItem.filePath)
                val newFile = File(oldFile.parent, "${fileItem.id}-$newFileName")

                val renamed = oldFile.renameTo(newFile)

                if (renamed) {
                    val updatedFileItem = fileItem.copy(
                        fileName = newFileName,
                        filePath = newFile.absolutePath
                    )

                    val currentFiles = _fileItems.value.toMutableList()
                    val index = currentFiles.indexOfFirst { it.id == fileItem.id }
                    if (index != -1) {
                        currentFiles[index] = updatedFileItem
                        _fileItems.value = currentFiles
                    }

                    updatedFileItem
                } else {
                    null
                }
            } catch (e: Exception) {
                null
            }
        }
    }

    fun getFileById(id: String): FileItem? {
        return _fileItems.value.find { it.id == id }
    }
}