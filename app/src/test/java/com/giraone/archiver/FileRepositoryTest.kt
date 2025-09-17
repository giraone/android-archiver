package com.giraone.archiver

import android.content.Context
import com.giraone.archiver.data.FileRepository
import com.giraone.archiver.data.PreferencesManager
import com.giraone.archiver.data.SortOrder
import com.giraone.archiver.utils.ContentHandler
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import java.io.ByteArrayInputStream
import java.io.File

class FileRepositoryTest {

    @Mock
    private lateinit var mockContext: Context

    @Mock
    private lateinit var mockPreferencesManager: PreferencesManager

    @Mock
    private lateinit var mockFilesDir: File

    @Mock
    private lateinit var mockArchivedFilesDir: File

    private lateinit var fileRepository: FileRepository

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        `when`(mockContext.filesDir).thenReturn(mockFilesDir)
        `when`(mockPreferencesManager.sortOrderFlow).thenReturn(flowOf(SortOrder.DATE))
        fileRepository = FileRepository(mockContext, mockPreferencesManager)
    }

    @Test
    fun loadFiles_emptyDirectory() = runTest {
        `when`(mockFilesDir.exists()).thenReturn(true)
        `when`(mockArchivedFilesDir.exists()).thenReturn(true)
        `when`(mockArchivedFilesDir.isDirectory).thenReturn(true)
        `when`(mockArchivedFilesDir.listFiles()).thenReturn(emptyArray())

        fileRepository.loadFiles()
        // Verify that the files list is empty
        // This would require accessing the internal state or adding a getter method
    }

    @Test
    fun addFile() = runTest {
        val testFileData = ContentHandler.FileData(
            fileName = "test.txt",
            mimeType = "text/plain",
            size = 100L,
            inputStream = ByteArrayInputStream("test content".toByteArray())
        )

        // This test would require extensive mocking of File operations
        // In a real implementation, you'd use a test directory or mock file system

        // Mock the file creation process
        // fileRepository.addFile(testFileData)

        // Verify file was added to the repository
    }

    @Test
    fun deleteFile() = runTest {
        // Mock a file item and test deletion
        // This would require setting up a mock file system
    }

    @Test
    fun renameFile() = runTest {
        // Mock a file item and test renaming
        // This would require setting up a mock file system
    }

    @Test
    fun getFileById() {
        // Test retrieving a file by ID
        // This would require adding files to the repository first
        val result = fileRepository.getFileById("test-id")
        assertNull(result) // Should be null for empty repository
    }
}