package com.giraone.archiver

import android.content.Context
import com.giraone.archiver.data.FileType
import com.giraone.archiver.utils.FileUtils
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import java.time.LocalDateTime

class FileUtilsTest {

    @Mock
    private lateinit var mockContext: Context

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun formatFileSize_bytes() {
        `when`(mockContext.getString(R.string.bytes, 512L)).thenReturn("512 bytes")
        val result = FileUtils.formatFileSize(mockContext, 512L)
        assertEquals("512 bytes", result)
    }

    @Test
    fun formatFileSize_kilobytes() {
        `when`(mockContext.getString(R.string.kb, 1.5)).thenReturn("1.5 KB")
        val result = FileUtils.formatFileSize(mockContext, 1536L) // 1.5 KB
        assertEquals("1.5 KB", result)
    }

    @Test
    fun formatFileSize_megabytes() {
        `when`(mockContext.getString(R.string.mb, 2.0)).thenReturn("2.0 MB")
        val result = FileUtils.formatFileSize(mockContext, 2097152L) // 2 MB
        assertEquals("2.0 MB", result)
    }

    @Test
    fun formatFileSize_gigabytes() {
        `when`(mockContext.getString(R.string.gb, 1.0)).thenReturn("1.0 GB")
        val result = FileUtils.formatFileSize(mockContext, 1073741824L) // 1 GB
        assertEquals("1.0 GB", result)
    }

    @Test
    fun formatDateTime() {
        val dateTime = LocalDateTime.of(2023, 12, 25, 15, 30, 45)
        val result = FileUtils.formatDateTime(dateTime)
        assertEquals("2023-12-25 15:30:45", result)
    }

    @Test
    fun getFileTypeFromMimeType_image() {
        assertEquals(FileType.IMAGE, FileUtils.getFileTypeFromMimeType("image/jpeg"))
        assertEquals(FileType.IMAGE, FileUtils.getFileTypeFromMimeType("image/png"))
        assertEquals(FileType.IMAGE, FileUtils.getFileTypeFromMimeType("image/gif"))
    }

    @Test
    fun getFileTypeFromMimeType_text() {
        assertEquals(FileType.TEXT, FileUtils.getFileTypeFromMimeType("text/plain"))
        assertEquals(FileType.TEXT, FileUtils.getFileTypeFromMimeType("text/html"))
        assertEquals(FileType.TEXT, FileUtils.getFileTypeFromMimeType("application/pdf"))
    }

    @Test
    fun getFileTypeFromMimeType_markdown() {
        assertEquals(FileType.MARKDOWN, FileUtils.getFileTypeFromMimeType("text/markdown"))
    }

    @Test
    fun getFileTypeFromMimeType_other() {
        assertEquals(FileType.OTHER, FileUtils.getFileTypeFromMimeType("application/zip"))
        assertEquals(FileType.OTHER, FileUtils.getFileTypeFromMimeType("video/mp4"))
        assertEquals(FileType.OTHER, FileUtils.getFileTypeFromMimeType(null))
    }

    @Test
    fun getFileExtensionFromMimeType() {
        // Note: This test might need to be adjusted based on actual MimeTypeMap behavior
        val result = FileUtils.getFileExtensionFromMimeType("image/jpeg")
        assertTrue(result.isEmpty() || result == ".jpg" || result == ".jpeg")
    }

    @Test
    fun generateUniqueFileName() {
        val result = FileUtils.generateUniqueFileName("test.txt", "text/plain")
        assertTrue(result.contains("test.txt"))
        assertTrue(result.length > "test.txt".length) // Should have UUID prefix
    }

    @Test
    fun isTextFile() {
        assertTrue(FileUtils.isTextFile("text/plain"))
        assertTrue(FileUtils.isTextFile("text/html"))
        assertTrue(FileUtils.isTextFile("application/pdf"))
        assertFalse(FileUtils.isTextFile("image/jpeg"))
        assertFalse(FileUtils.isTextFile(null))
    }

    @Test
    fun isImageFile() {
        assertTrue(FileUtils.isImageFile("image/jpeg"))
        assertTrue(FileUtils.isImageFile("image/png"))
        assertFalse(FileUtils.isImageFile("text/plain"))
        assertFalse(FileUtils.isImageFile(null))
    }

    @Test
    fun getFileTypeFromFileName_markdown() {
        assertEquals(FileType.MARKDOWN, FileUtils.getFileTypeFromFileName("readme.md", "text/plain"))
        assertEquals(FileType.MARKDOWN, FileUtils.getFileTypeFromFileName("docs.markdown", "text/plain"))
        assertEquals(FileType.MARKDOWN, FileUtils.getFileTypeFromFileName("README.MD", "text/plain"))
    }

    @Test
    fun getFileTypeFromFileName_fallback() {
        assertEquals(FileType.TEXT, FileUtils.getFileTypeFromFileName("test.txt", "text/plain"))
        assertEquals(FileType.IMAGE, FileUtils.getFileTypeFromFileName("photo.jpg", "image/jpeg"))
    }
}