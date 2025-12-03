package com.practicum.myapplication.data.network

import com.practicum.myapplication.data.dto.TracksSearchRequest
import com.practicum.myapplication.data.dto.TracksSearchResponse
import com.practicum.myapplication.data.DatabaseMock
import com.practicum.myapplication.domain.NetworkClient
import com.practicum.myapplication.domain.TracksRepository
import com.practicum.myapplication.domain.models.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.text.SimpleDateFormat
import java.util.Locale

class TracksRepositoryImpl(
    private val networkClient: NetworkClient,
    private val scope: CoroutineScope
) : TracksRepository {
    private val database = DatabaseMock.getInstance(scope = scope)
    private val timeFormatter = SimpleDateFormat("mm:ss", Locale.getDefault())

    // Переопределение метода поиска треков
    override suspend fun searchTracks(expression: String): List<Track> {
        val response = networkClient.doRequest(TracksSearchRequest(expression))
        delay(1000)
        return if (response.resultCode == 200) {
            (response as TracksSearchResponse).results.mapIndexed { index, it ->
                val trackTime = try {
                    timeFormatter.format(it.trackTimeMillis)
                } catch (e: Exception) {
                    "00:00"
                }

                val track = Track(
                    id = System.currentTimeMillis() + index,
                    trackName = it.trackName,
                    artistName = it.artistName,
                    trackTime = trackTime,
                    image = it.image ?: "",
                    favorite = false,
                    playlistId = emptyList()
                )
                database.insertTrack(track)
                track
            }
        } else {
            emptyList()
        }
    }

    // Переопределние метода поиска трека по имени и исполнителю
    override fun getTrackByNameAndArtist(track: Track): Flow<Track?> = flow {
        val foundTrack = database.getTrackByNameAndArtist(track.trackName, track.artistName)
        emit(foundTrack)
    }

    // Переопределение метода получения избранных треков
    override fun getFavoriteTracks(): Flow<List<Track>> {
        return database.getFavoriteTracks()
    }

    // Переопределение метода добавления трека в плейлист
    override suspend fun insertSongToPlaylist(track: Track, playlistId: Long) {
        val existingTrack = database.getTrackByNameAndArtist(track.trackName, track.artistName)
        val updatedPlaylistIds = if (existingTrack != null) {
            existingTrack.playlistId.toMutableList().apply {
                if (!contains(playlistId)) {
                    add(playlistId)
                }
            }
        } else {
            listOf(playlistId)
        }

        database.insertTrack(track.copy(
            playlistId = updatedPlaylistIds,
            favorite = existingTrack?.favorite ?: track.favorite
        ))
    }

    // Переопределение метода удаления трека из плейлиста
    override suspend fun deleteSongFromPlaylist(track: Track, playlistId: Long) {
        database.removeTrackFromPlaylist(track.trackName, track.artistName, playlistId)
    }

    // Переопределение метода обновления статуса избранного трека
    override suspend fun updateTrackFavoriteStatus(track: Track, isFavorite: Boolean) {
        val existingTrack = database.getTrackByNameAndArtist(track.trackName, track.artistName)
        if (existingTrack != null) {
            database.insertTrack(existingTrack.copy(favorite = isFavorite))
        } else {
            database.insertTrack(track.copy(favorite = isFavorite))
        }
    }

    // Переопределение метода удаления треков по айди плейлиста
    override suspend fun deleteTracksByPlaylistId(playlistId: Long) {
        database.deleteTracksByPlaylistId(playlistId)
    }

    override fun getTrackById(id: Long): Flow<Track?> = flow {
        val track = database.getTrackById(id)
        emit(track)
    }
}