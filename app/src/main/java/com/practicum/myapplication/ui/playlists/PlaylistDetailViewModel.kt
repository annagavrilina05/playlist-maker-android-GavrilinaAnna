package com.practicum.myapplication.ui.playlists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.practicum.myapplication.creator.Creator
import com.practicum.myapplication.domain.PlaylistsRepository

class PlaylistDetailViewModel(
    private val playlistsRepository: PlaylistsRepository,
    private val playlistId: Long
) : ViewModel() {

    val playlist = playlistsRepository.getPlaylist(playlistId)

    companion object {
        fun getViewModelFactory(playlistId: Long): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return PlaylistDetailViewModel(
                        Creator.getPlaylistsRepository(),
                        playlistId
                    ) as T
                }
            }
    }
}