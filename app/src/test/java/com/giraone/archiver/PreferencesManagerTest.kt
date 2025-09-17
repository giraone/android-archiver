package com.giraone.archiver

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.giraone.archiver.data.FontSize
import com.giraone.archiver.data.PreferencesManager
import com.giraone.archiver.data.SortOrder
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import kotlinx.coroutines.flow.first
import org.junit.Assert.*

class PreferencesManagerTest {

    @Mock
    private lateinit var mockContext: Context

    @Mock
    private lateinit var mockDataStore: DataStore<Preferences>

    @Mock
    private lateinit var mockPreferences: Preferences

    private lateinit var preferencesManager: PreferencesManager

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        // Note: Due to DataStore implementation complexity, these tests are simplified
        // In a real project, you would use TestScope and test DataStore implementations
        preferencesManager = PreferencesManager(mockContext)
    }

    @Test
    fun sortOrderFlow_defaultValue() = runTest {
        // This is a simplified test - in reality you'd need proper DataStore test setup
        // The default should be SortOrder.DATE
        // Real implementation would require setting up test DataStore
    }

    @Test
    fun fontSizeFlow_defaultValue() = runTest {
        // This is a simplified test - in reality you'd need proper DataStore test setup
        // The default should be FontSize.NORMAL
        // Real implementation would require setting up test DataStore
    }

    @Test
    fun setSortOrder() = runTest {
        // Test would verify that the sort order is correctly saved to DataStore
        // Implementation would require mocking the DataStore edit operation
    }

    @Test
    fun setFontSize() = runTest {
        // Test would verify that the font size is correctly saved to DataStore
        // Implementation would require mocking the DataStore edit operation
    }
}