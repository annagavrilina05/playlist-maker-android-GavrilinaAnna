package com.practicum.myapplication.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.practicum.myapplication.creator.Creator
import com.practicum.myapplication.domain.SearchHistoryRepository
import com.practicum.myapplication.domain.TracksRepository
import com.practicum.myapplication.ui.search.SearchState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException

class SearchViewModel(
    private val tracksRepository: TracksRepository,
    private val searchHistoryRepository: SearchHistoryRepository
) : ViewModel() {
    private val _searchScreenState = MutableStateFlow<SearchState>(SearchState.Initial)
    val searchScreenState  = _searchScreenState.asStateFlow()
    private var lastQuery = ""
    private var failedQuery = ""
    val searchHistory = searchHistoryRepository.getHistoryFlow()

    fun search(whatSearch: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _searchScreenState.update { SearchState.Searching }
                val list = tracksRepository.searchTracks(expression = whatSearch)
                _searchScreenState.update { SearchState.Success(list = list) }
                // Сохраняем только при успешном поиске
                if (list.isNotEmpty()) {
                    addToHistory(whatSearch)
                }
            } catch (e: IOException) {
                failedQuery = whatSearch // Сохраняем неудавшийся запрос
                _searchScreenState.update {
                    SearchState.Fail(
                        errorMessage = e.message.toString(),
                        lastQuery = whatSearch
                    )
                }
            }
        }
    }

    fun getLastQuery(): String = lastQuery

    fun getFailedQuery(): String = failedQuery

    fun retryLastFailedSearch() {
        if (failedQuery.isNotEmpty()) {
            search(failedQuery)
        }
    }

    fun clearSearch() {
        _searchScreenState.update { SearchState.Initial }
        lastQuery = ""
    }

    fun addToHistory(query: String) {
        if (query.isNotBlank()) {
            searchHistoryRepository.addToHistory(query)
        }
    }

    suspend fun getHistoryList(): List<String> {
        return searchHistoryRepository.getHistory()
    }

    suspend fun clearHistory() {
        searchHistoryRepository.clearHistory()
    }

    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return SearchViewModel(
                        Creator.getTracksRepository(),
                        Creator.getSearchHistoryRepository()
                    ) as T
                }
            }
    }
}