package com.giraone.archiver.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class PreferencesManager(private val context: Context) {

    companion object {
        private val SORT_ORDER_KEY = stringPreferencesKey("sort_order")
        private val FONT_SIZE_KEY = stringPreferencesKey("font_size")
    }

    val sortOrderFlow: Flow<SortOrder> = context.dataStore.data.map { preferences ->
        val sortOrderString = preferences[SORT_ORDER_KEY] ?: SortOrder.DATE.name
        try {
            SortOrder.valueOf(sortOrderString)
        } catch (e: IllegalArgumentException) {
            SortOrder.DATE
        }
    }

    val fontSizeFlow: Flow<FontSize> = context.dataStore.data.map { preferences ->
        val fontSizeString = preferences[FONT_SIZE_KEY] ?: FontSize.NORMAL.name
        try {
            FontSize.valueOf(fontSizeString)
        } catch (e: IllegalArgumentException) {
            FontSize.NORMAL
        }
    }

    suspend fun setSortOrder(sortOrder: SortOrder) {
        context.dataStore.edit { preferences ->
            preferences[SORT_ORDER_KEY] = sortOrder.name
        }
    }

    suspend fun setFontSize(fontSize: FontSize) {
        context.dataStore.edit { preferences ->
            preferences[FONT_SIZE_KEY] = fontSize.name
        }
    }
}