package com.practicum.myapplication.data

import com.practicum.myapplication.domain.models.Playlist
import com.practicum.myapplication.domain.models.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class DatabaseMock(
    private val scope: CoroutineScope,
) {
    // Объявление синглтона
    companion object {
        private var instance: DatabaseMock? = null

        fun getInstance(scope: CoroutineScope): DatabaseMock {
            return instance ?: synchronized(this) {
                instance ?: DatabaseMock(scope).also { instance = it }
            }
        }
    }

    private val historyList = mutableListOf<String>()
    private val playlists = mutableListOf<Playlist>()
    private val tracks = mutableListOf<Track>()
    private val _historyUpdates = MutableSharedFlow<Unit>()

    // История поиска
    fun getHistory(): List<String> = historyList.toList()

    // Обновление истории поиска
    fun addToHistory(word: String) {
        historyList.remove(word)
        historyList.add(0, word)
        if (historyList.size > 25) historyList.removeAt(historyList.lastIndex)
        notifyHistoryChanged()
    }

    // Оповещение об изменениях в истории
    private fun notifyHistoryChanged() {
        scope.launch(Dispatchers.IO) { _historyUpdates.emit(Unit) }
    }

    // Плейлисты
    fun getAllPlaylists(): Flow<List<Playlist>> = flow {
        delay(300)
        val playlistsWithTracks = playlists.map { playlist ->
            val playlistTracks = tracks.filter { track ->
                playlist.id in track.playlistId
            }
            playlist.copy(tracks = playlistTracks)
        }
        emit(playlistsWithTracks)
    }

    // Получение плейлиста по айди
    fun getPlaylist(playlistId: Long): Playlist? {
        val playlist = playlists.find { it.id == playlistId }
        return playlist?.copy(
            tracks = tracks.filter { playlistId in it.playlistId }
        )
    }

    // Добавление нового плейлиста
    fun addNewPlaylist(name: String, description: String) {
        val newPlaylist = Playlist(
            id = (playlists.size + 1).toLong(),
            name = name,
            description = description,
            tracks = emptyList()
        )
        playlists.add(newPlaylist)
    }

    // Удаление плейлиста
    fun deletePlaylistById(id: Long) {
        playlists.removeIf { it.id == id }
        // Удаляем треки этого плейлиста из всех треков
        tracks.forEach { track ->
            track.playlistId = track.playlistId.filter { it != id }
        }
    }

    // Получение трека по названию и имени исполнителяч
    fun getTrackByNameAndArtist(trackName: String, artistName: String): Track? {
        return tracks.find { it.trackName == trackName && it.artistName == artistName }
    }

    // Добавление трека
    fun insertTrack(track: Track) {
        tracks.removeIf {
            it.trackName == track.trackName && it.artistName == track.artistName
        }
        tracks.add(track)
    }

    // Удаление трека из плейлиста
    fun removeTrackFromPlaylist(trackName: String, artistName: String, playlistId: Long) {
        val track = getTrackByNameAndArtist(trackName, artistName)
        track?.let { existingTrack ->
            // Создаем новый трек с обновленным списком playlistId
            val updatedPlaylistIds = existingTrack.playlistId.filter { id -> id != playlistId }
            val updatedTrack = existingTrack.copy(playlistId = updatedPlaylistIds)

            // Удаляем старый и добавляем обновленный
            tracks.remove(existingTrack)
            tracks.add(updatedTrack)
        }
    }

    // Получение избранных треков
    fun getFavoriteTracks(): Flow<List<Track>> = flow {
        delay(300)
        emit(tracks.filter { it.favorite })
    }

    // Удаление плейлиста
    fun deleteTracksByPlaylistId(playlistId: Long) {
        // Удаляем playlistId из всех треков
        tracks.forEach { track ->
            track.playlistId = track.playlistId.filter { it != playlistId }
        }
    }

    fun clearAllData() {
        historyList.clear()
        playlists.clear()
        tracks.clear()
    }
    fun getTracksCount(): Int = tracks.size
    fun getPlaylistsCount(): Int = playlists.size
}