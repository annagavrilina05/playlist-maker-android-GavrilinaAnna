package com.practicum.myapplication.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.practicum.myapplication.creator.Creator
import com.practicum.myapplication.domain.TracksRepository
import com.practicum.myapplication.ui.search.SearchState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException

class SearchViewModel(
    private val tracksRepository: TracksRepository
) : ViewModel() {
    private val _searchScreenState = MutableStateFlow<SearchState>(SearchState.Initial)
    val searchScreenState  = _searchScreenState.asStateFlow()
    private var lastQuery = ""
    private var failedQuery = ""

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


    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return SearchViewModel(Creator.getTracksRepository()) as T
                }
            }
    }

    private val _searchHistory = MutableStateFlow<List<String>>(emptyList())
    val searchHistory = _searchHistory.asStateFlow()

    fun addToHistory(query: String) {
        if (query.isNotBlank()) {
            val currentHistory = _searchHistory.value.toMutableList()
            // Удаляем дубликаты и добавляем в начало
            currentHistory.remove(query)
            currentHistory.add(0, query)
            // Ограничиваем историю 25 элементами
            _searchHistory.value = currentHistory.take(25)
        }
    }

    fun getHistoryList(): List<String> {
        return _searchHistory.value
    }

    fun clearHistory() {
        _searchHistory.value = emptyList()
    }
}