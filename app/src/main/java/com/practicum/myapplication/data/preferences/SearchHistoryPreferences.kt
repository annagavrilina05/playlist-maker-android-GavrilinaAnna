package com.practicum.myapplication.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "search_history")

private const val MAX_ENTRIES = 10
private const val SEPARATOR = ","
private val SEARCH_HISTORY_KEY = stringPreferencesKey("search_history")

class SearchHistoryPreferences(
    private val context: Context,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {

    fun addEntry(word: String) {
        if (word.isEmpty()) {
            return
        }

        coroutineScope.launch {
            context.dataStore.edit { preferences ->
                val historyString = preferences[SEARCH_HISTORY_KEY].orEmpty()
                val history = if (historyString.isNotEmpty()) {
                    historyString.split(SEPARATOR).toMutableList()
                } else {
                    mutableListOf()
                }

                history.remove(word)
                history.add(0, word)

                val subList = if (history.size > MAX_ENTRIES) {
                    history.subList(0, MAX_ENTRIES) // Храним не более 10 элементов
                } else {
                    history
                }

                val updatedString = subList.joinToString(SEPARATOR)
                preferences[SEARCH_HISTORY_KEY] = updatedString
            }
        }
    }

    suspend fun getEntries(): List<String> = withContext(Dispatchers.IO) {
        getEntriesFlow().first()
    }

    fun getEntriesFlow(): Flow<List<String>> {
        return context.dataStore.data.map { preferences ->
            val historyString = preferences[SEARCH_HISTORY_KEY].orEmpty()
            if (historyString.isNotEmpty()) {
                historyString.split(SEPARATOR)
            } else {
                emptyList()
            }
        }
    }

    suspend fun clearHistory() {
        context.dataStore.edit { preferences ->
            preferences.remove(SEARCH_HISTORY_KEY)
        }
    }
}