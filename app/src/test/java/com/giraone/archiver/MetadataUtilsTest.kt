package com.giraone.archiver

import com.giraone.archiver.utils.MetadataUtils
import org.junit.Test
import org.junit.Assert.*
import java.io.File
import java.io.IOException

class MetadataUtilsTest {

    @Test
    fun createDefaultMetadata() {
        val metadata = MetadataUtils.createDefaultMetadata("test.txt", "text/plain")
        assertEquals("test.txt", metadata["name"])
        assertEquals("text/plain", metadata["contentType"])
        assertEquals(2, metadata.size)
    }

    @Test
    fun getContentFileName() {
        val result = MetadataUtils.getContentFileName("ABC123DEF456")
        assertEquals("ABC123DEF456.content", result)
    }

    @Test
    fun getMetadataFileName() {
        val result = MetadataUtils.getMetadataFileName("ABC123DEF456")
        assertEquals("ABC123DEF456.metadata", result)
    }

    @Test
    fun extractTsidFromContentFile() {
        val contentFile = File("ABC123DEF456.content")
        val result = MetadataUtils.extractTsidFromContentFile(contentFile)
        assertEquals("ABC123DEF456", result)
    }

    @Test
    fun extractTsidFromContentFile_invalid() {
        val invalidFile = File("notContentFile.txt")
        val result = MetadataUtils.extractTsidFromContentFile(invalidFile)
        assertNull(result)
    }

    @Test
    fun extractTsidFromMetadataFile() {
        val metadataFile = File("ABC123DEF456.metadata")
        val result = MetadataUtils.extractTsidFromMetadataFile(metadataFile)
        assertEquals("ABC123DEF456", result)
    }

    @Test
    fun extractTsidFromMetadataFile_invalid() {
        val invalidFile = File("notMetadataFile.txt")
        val result = MetadataUtils.extractTsidFromMetadataFile(invalidFile)
        assertNull(result)
    }

    @Test
    fun readMetadataFromFile_nonExistent() {
        val nonExistentFile = File("nonexistent.metadata")
        val result = MetadataUtils.readMetadataFromFile(nonExistentFile)
        assertTrue(result.isEmpty())
    }
}