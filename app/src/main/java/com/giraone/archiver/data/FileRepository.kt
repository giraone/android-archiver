package com.giraone.archiver.data

import android.content.Context
import android.util.Log
import com.giraone.archiver.utils.FileUtils
import com.giraone.archiver.utils.MetadataUtils
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
    companion object {
        private const val TAG = "FileRepository"
    }

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
            try {
                Log.d(TAG, "Loading files from archived_files directory")
                val filesDir = File(context.filesDir, "archived_files")
                if (!filesDir.exists()) {
                    Log.d(TAG, "Archived files directory doesn't exist, creating it")
                    filesDir.mkdirs()
                    _fileItems.value = emptyList()
                    return@withContext
                }

            val contentFiles = filesDir.listFiles()?.filter { it.name.endsWith(".content") } ?: emptyList()
            val fileItems = contentFiles.mapNotNull { contentFile ->
                try {
                    parseFileItem(contentFile)
                } catch (e: Exception) {
                    Log.w(TAG, "Failed to parse file item: ${contentFile.name}", e)
                    null
                }
            }

                Log.d(TAG, "Successfully loaded ${fileItems.size} files")
                _fileItems.value = fileItems
            } catch (e: Exception) {
                Log.e(TAG, "Failed to load files", e)
                _fileItems.value = emptyList()
            }
        }
    }

    private fun parseFileItem(contentFile: File): FileItem? {
        if (!contentFile.isFile) {
            Log.w(TAG, "Skipping non-file: ${contentFile.name}")
            return null
        }

        val tsid = MetadataUtils.extractTsidFromContentFile(contentFile)
        if (tsid == null) {
            Log.w(TAG, "Invalid content file name format: ${contentFile.name} (expected TSID.content format)")
            return null
        }

        val metadataFile = File(contentFile.parent, MetadataUtils.getMetadataFileName(tsid))
        val metadata = MetadataUtils.readMetadataFromFile(metadataFile)

        val fileName = metadata["name"] ?: "unknown"
        val contentType = metadata["contentType"] ?: "application/octet-stream"
        val fileType = FileUtils.getFileTypeFromFileName(fileName, contentType)

        return FileItem(
            id = tsid,
            fileName = fileName,
            filePath = contentFile.absolutePath,
            contentType = contentType,
            sizeInBytes = contentFile.length(),
            storageDateTime = LocalDateTime.ofEpochSecond(
                contentFile.lastModified() / 1000, 0, java.time.ZoneOffset.systemDefault().rules.getOffset(java.time.Instant.ofEpochMilli(contentFile.lastModified()))
            ),
            fileType = fileType,
            metadata = metadata
        )
    }


    suspend fun addFile(fileData: com.giraone.archiver.utils.ContentHandler.FileData): FileItem {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Adding file: ${fileData.fileName}")
                val contentHandler = com.giraone.archiver.utils.ContentHandler(context)
                val savedFile = contentHandler.saveFileToPrivateDirectory(fileData)

                val tsid = MetadataUtils.extractTsidFromContentFile(savedFile)
                    ?: throw IllegalStateException("Invalid content file generated: ${savedFile.name}")

                val metadata = MetadataUtils.createDefaultMetadata(
                    fileData.fileName,
                    fileData.mimeType ?: "application/octet-stream"
                )

                val metadataFile = File(savedFile.parent, MetadataUtils.getMetadataFileName(tsid))
                MetadataUtils.writeMetadataToFile(metadataFile, metadata)

                val fileItem = FileItem(
                    id = tsid,
                    fileName = fileData.fileName,
                    filePath = savedFile.absolutePath,
                    contentType = fileData.mimeType ?: "application/octet-stream",
                    sizeInBytes = fileData.size,
                    storageDateTime = LocalDateTime.now(),
                    fileType = FileUtils.getFileTypeFromFileName(fileData.fileName, fileData.mimeType),
                    metadata = metadata
                )

                val currentFiles = _fileItems.value.toMutableList()
                currentFiles.add(fileItem)
                _fileItems.value = currentFiles

                Log.d(TAG, "Successfully added file: ${fileData.fileName} with ID: ${fileItem.id}")
                fileItem
            } catch (e: Exception) {
                Log.e(TAG, "Failed to add file: ${fileData.fileName}", e)
                throw e
            }
        }
    }

    suspend fun deleteFile(fileItem: FileItem): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Deleting file: ${fileItem.fileName}")
                val contentFile = File(fileItem.filePath)
                val metadataFile = File(contentFile.parent, MetadataUtils.getMetadataFileName(fileItem.id))

                val contentDeleted = contentFile.delete()
                val metadataDeleted = metadataFile.delete() || !metadataFile.exists()

                if (contentDeleted && metadataDeleted) {
                    val currentFiles = _fileItems.value.toMutableList()
                    currentFiles.removeIf { it.id == fileItem.id }
                    _fileItems.value = currentFiles
                    Log.d(TAG, "Successfully deleted file: ${fileItem.fileName}")
                } else {
                    Log.w(TAG, "File delete operation failed for: ${fileItem.fileName} (content: $contentDeleted, metadata: $metadataDeleted)")
                }

                contentDeleted && metadataDeleted
            } catch (e: Exception) {
                Log.e(TAG, "Failed to delete file: ${fileItem.fileName}", e)
                false
            }
        }
    }

    suspend fun renameFile(fileItem: FileItem, newFileName: String): FileItem? {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Renaming file: ${fileItem.fileName} to $newFileName")
                val metadataFile = File(File(fileItem.filePath).parent, MetadataUtils.getMetadataFileName(fileItem.id))

                val updatedMetadata = fileItem.metadata.toMutableMap()
                updatedMetadata["name"] = newFileName

                MetadataUtils.writeMetadataToFile(metadataFile, updatedMetadata)

                val updatedFileItem = fileItem.copy(
                    fileName = newFileName,
                    metadata = updatedMetadata
                )

                val currentFiles = _fileItems.value.toMutableList()
                val index = currentFiles.indexOfFirst { it.id == fileItem.id }
                if (index != -1) {
                    currentFiles[index] = updatedFileItem
                    _fileItems.value = currentFiles
                }

                Log.d(TAG, "Successfully renamed file: ${fileItem.fileName} to $newFileName")
                updatedFileItem
            } catch (e: Exception) {
                Log.e(TAG, "Failed to rename file: ${fileItem.fileName} to $newFileName", e)
                null
            }
        }
    }

    fun getFileById(id: String): FileItem? {
        return _fileItems.value.find { it.id == id }
    }
}