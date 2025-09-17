package com.giraone.archiver.viewmodel

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.giraone.archiver.data.FileItem
import com.giraone.archiver.data.FileRepository
import com.giraone.archiver.data.FontSize
import com.giraone.archiver.data.PreferencesManager
import com.giraone.archiver.data.SortOrder
import com.giraone.archiver.utils.ContentHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val fileRepository: FileRepository,
    private val preferencesManager: PreferencesManager,
    private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    val fileItems = fileRepository.fileItems
    val sortOrder = preferencesManager.sortOrderFlow
    val fontSize = preferencesManager.fontSizeFlow

    init {
        loadFiles()
    }

    private fun loadFiles() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                fileRepository.loadFiles()
                _uiState.value = _uiState.value.copy(isLoading = false, error = null)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Unknown error occurred"
                )
            }
        }
    }

    fun handleSharedIntent(intent: Intent) {
        viewModelScope.launch {
            try {
                val contentHandler = ContentHandler(context)
                val fileDataList = contentHandler.extractFileDataFromIntent(intent)

                fileDataList.forEach { fileData ->
                    fileRepository.addFile(fileData)
                }

                _uiState.value = _uiState.value.copy(error = null)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Error processing shared content"
                )
            }
        }
    }

    fun deleteFile(fileItem: FileItem) {
        viewModelScope.launch {
            try {
                val deleted = fileRepository.deleteFile(fileItem)
                if (!deleted) {
                    _uiState.value = _uiState.value.copy(error = "Failed to delete file")
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Error deleting file"
                )
            }
        }
    }

    fun renameFile(fileItem: FileItem, newFileName: String) {
        viewModelScope.launch {
            try {
                val renamedFile = fileRepository.renameFile(fileItem, newFileName)
                if (renamedFile == null) {
                    _uiState.value = _uiState.value.copy(error = "Failed to rename file")
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Error renaming file"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun setSortOrder(sortOrder: SortOrder) {
        viewModelScope.launch {
            preferencesManager.setSortOrder(sortOrder)
        }
    }

    fun setFontSize(fontSize: FontSize) {
        viewModelScope.launch {
            preferencesManager.setFontSize(fontSize)
        }
    }

    class Factory(
        private val fileRepository: FileRepository,
        private val preferencesManager: PreferencesManager,
        private val context: Context
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                return MainViewModel(fileRepository, preferencesManager, context) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

data class MainUiState(
    val isLoading: Boolean = false,
    val error: String? = null
)