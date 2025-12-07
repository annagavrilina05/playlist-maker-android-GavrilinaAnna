package com.practicum.myapplication.data

import com.practicum.myapplication.data.preferences.SearchHistoryPreferences
import com.practicum.myapplication.domain.SearchHistoryRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class SearchHistoryRepositoryImpl(
    private val preferences: SearchHistoryPreferences,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) : SearchHistoryRepository {

    override fun addToHistory(query: String) {
        preferences.addEntry(query)
    }

    override suspend fun getHistory(): List<String> {
        return preferences.getEntries()
    }

    override fun getHistoryFlow(): Flow<List<String>> {
        return preferences.getEntriesFlow()
    }

    override suspend fun clearHistory() {
        preferences.clearHistory()
    }
}