package com.practicum.myapplication.data

import com.practicum.myapplication.domain.PlaylistsRepository
import com.practicum.myapplication.domain.models.Playlist
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistsRepositoryImpl(
    private val scope: CoroutineScope
) : PlaylistsRepository {

    private val database = DatabaseMock.getInstance(scope = scope)

    override fun getPlaylist(playlistId: Long): Flow<Playlist?> {
        return database.getAllPlaylists().map { playlists ->
            playlists.find { it.id == playlistId }
        }
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> {
        return database.getAllPlaylists()
    }

    override suspend fun addNewPlaylist(name: String, description: String) {
        database.addNewPlaylist(name, description)
    }

    override suspend fun deletePlaylistById(id: Long) {
        database.deletePlaylistById(id)
    }
}