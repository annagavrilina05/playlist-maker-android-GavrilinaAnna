package com.practicum.myapplication.ui.trackdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.practicum.myapplication.creator.Creator
import com.practicum.myapplication.domain.TracksRepository
import com.practicum.myapplication.domain.models.Track
import kotlinx.coroutines.flow.Flow

class TrackDetailsViewModel(
    private val tracksRepository: TracksRepository,
    private val trackId: Long
) : ViewModel() {

    val track: Flow<Track?> = tracksRepository.getTrackById(trackId)

    companion object {
        fun getViewModelFactory(trackId: Long): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val repository = Creator.getTracksRepository()
                    return TrackDetailsViewModel(repository, trackId) as T
                }
            }
    }
}