package com.giraone.archiver.utils

import android.util.Log
import org.json.JSONObject
import java.io.File
import java.time.LocalDateTime

object MetadataUtils {
    private const val TAG = "MetadataUtils"

    fun createDefaultMetadata(fileName: String, contentType: String): Map<String, String> {
        return mapOf(
            "name" to fileName,
            "contentType" to contentType
        )
    }

    fun writeMetadataToFile(metadataFile: File, metadata: Map<String, String>) {
        try {
            Log.d(TAG, "Writing metadata to file: ${metadataFile.name}")
            val jsonObject = JSONObject()
            metadata.forEach { (key, value) ->
                jsonObject.put(key, value)
            }
            metadataFile.writeText(jsonObject.toString(2))
            Log.d(TAG, "Successfully wrote metadata to file: ${metadataFile.name}")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to write metadata to file: ${metadataFile.name}", e)
            throw e
        }
    }

    fun readMetadataFromFile(metadataFile: File): Map<String, String> {
        return try {
            Log.d(TAG, "Reading metadata from file: ${metadataFile.name}")
            if (!metadataFile.exists()) {
                Log.w(TAG, "Metadata file doesn't exist: ${metadataFile.name}")
                return emptyMap()
            }

            val jsonString = metadataFile.readText()
            val jsonObject = JSONObject(jsonString)
            val metadata = mutableMapOf<String, String>()

            jsonObject.keys().forEach { key ->
                metadata[key] = jsonObject.getString(key)
            }

            Log.d(TAG, "Successfully read metadata from file: ${metadataFile.name}")
            metadata
        } catch (e: Exception) {
            Log.e(TAG, "Failed to read metadata from file: ${metadataFile.name}", e)
            emptyMap()
        }
    }

    fun getContentFileName(tsid: String): String = "$tsid.content"

    fun getMetadataFileName(tsid: String): String = "$tsid.metadata"

    fun extractTsidFromContentFile(contentFile: File): String? {
        val fileName = contentFile.name
        return if (fileName.endsWith(".content")) {
            fileName.substringBeforeLast(".content")
        } else {
            null
        }
    }

    fun extractTsidFromMetadataFile(metadataFile: File): String? {
        val fileName = metadataFile.name
        return if (fileName.endsWith(".metadata")) {
            fileName.substringBeforeLast(".metadata")
        } else {
            null
        }
    }
}