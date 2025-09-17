package com.giraone.archiver.data

import java.time.LocalDateTime

data class FileItem(
    val id: String,
    val fileName: String,
    val filePath: String,
    val contentType: String,
    val sizeInBytes: Long,
    val storageDateTime: LocalDateTime,
    val fileType: FileType
)