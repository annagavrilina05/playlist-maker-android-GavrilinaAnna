package com.practicum.myapplication.domain

import kotlinx.coroutines.flow.Flow

interface SearchHistoryRepository {
    fun addToHistory(query: String)
    suspend fun getHistory(): List<String>
    fun getHistoryFlow(): Flow<List<String>>
    suspend fun clearHistory()
}