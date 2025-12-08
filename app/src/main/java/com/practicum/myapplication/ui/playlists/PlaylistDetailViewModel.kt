package com.practicum.myapplication.ui.playlists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.practicum.myapplication.creator.Creator
import com.practicum.myapplication.domain.PlaylistsRepository
import androidx.lifecycle.viewModelScope
import com.practicum.myapplication.data.db.ImageFileManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlaylistDetailViewModel(
    private val playlistsRepository: PlaylistsRepository,
    private val imageFileManager: ImageFileManager,
    private val playlistId: Long
) : ViewModel() {

    val playlist = playlistsRepository.getPlaylist(playlistId)

    fun deletePlaylist() {
        viewModelScope.launch(Dispatchers.IO) {
            playlistsRepository.deletePlaylistById(playlistId)
        }
    }

    fun getImageUriForPlaylist(coverImageUri: String?): String? {
        return imageFileManager.getUriFromPath(coverImageUri)?.toString()
    }

    companion object {
        fun getViewModelFactory(playlistId: Long): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return PlaylistDetailViewModel(
                        Creator.getPlaylistsRepository(),
                        Creator.getImageFileManager(),
                        playlistId
                    ) as T
                }
            }
    }
}