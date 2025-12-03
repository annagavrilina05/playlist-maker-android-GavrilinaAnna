package com.practicum.myapplication.domain

import com.practicum.myapplication.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface TracksRepository {
    suspend fun searchTracks(expression: String): List<Track>
    fun getTrackByNameAndArtist(track: Track): Flow<Track?>
    fun getFavoriteTracks(): Flow<List<Track>>
    suspend fun insertSongToPlaylist(track: Track, playlistId: Long)
    suspend fun deleteSongFromPlaylist(track: Track, playlistId: Long)
    suspend fun updateTrackFavoriteStatus(track: Track, isFavorite: Boolean)
    suspend fun deleteTracksByPlaylistId(playlistId: Long)
    fun getTrackById(id: Long): Flow<Track?>
}