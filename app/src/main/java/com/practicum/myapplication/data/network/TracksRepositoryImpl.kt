package com.practicum.myapplication.data.network

import com.practicum.myapplication.data.converter.toTrack
import com.practicum.myapplication.data.converter.toEntity
import com.practicum.myapplication.data.database.dao.PlaylistsDao
import com.practicum.myapplication.data.database.dao.TracksDao
import com.practicum.myapplication.data.database.entity.PlaylistTrackCrossRef
import com.practicum.myapplication.data.dto.TracksSearchRequest
import com.practicum.myapplication.data.dto.TracksSearchResponse
import com.practicum.myapplication.domain.NetworkClient
import com.practicum.myapplication.domain.TracksRepository
import com.practicum.myapplication.domain.models.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Locale

class TracksRepositoryImpl(
    private val networkClient: NetworkClient,
    private val tracksDao: TracksDao,
    private val playlistsDao: PlaylistsDao,
    private val scope: CoroutineScope
) : TracksRepository {

    private val timeFormatter = SimpleDateFormat("mm:ss", Locale.getDefault())

    override suspend fun searchTracks(expression: String): List<Track> {
        val response = networkClient.doRequest(TracksSearchRequest(expression))
        delay(1000)

        return if (response.resultCode == 200) {
            (response as TracksSearchResponse).results.map { it ->
                val trackTime = try {
                    timeFormatter.format(it.trackTimeMillis)
                } catch (e: Exception) {
                    "00:00"
                }

                // Проверяем, есть ли трек уже в базе
                val existingTrack = tracksDao.getTrackByIdSync(it.id)
                val playlistIds = if (existingTrack != null) {
                    playlistsDao.getPlaylistIdsForTrack(it.id)
                } else {
                    emptyList()
                }

                val track = Track(
                    id = it.id,
                    trackName = it.trackName,
                    artistName = it.artistName,
                    trackTime = trackTime,
                    image = it.image ?: "",
                    favorite = existingTrack?.favorite ?: false,
                    playlistId = playlistIds
                )

                // Сохранение трека в базу
                tracksDao.insertTrack(track.toEntity())

                track
            }
        } else {
            emptyList()
        }
    }

    override fun getTrackByNameAndArtist(track: Track): Flow<Track?> {
        return tracksDao.getTrackByNameAndArtist(track.trackName, track.artistName)
            .map { entity ->
                entity?.let {
                    // Получаем плейлисты для этого трека
                    val playlistIds = playlistsDao.getPlaylistIdsForTrack(it.id)
                    it.toTrack(playlistIds)
                }
            }
    }

    override fun getFavoriteTracks(): Flow<List<Track>> {
        return tracksDao.getFavoriteTracks()
            .map { entities ->
                entities.map { entity ->
                    val playlistIds = playlistsDao.getPlaylistIdsForTrack(entity.id)
                    entity.toTrack(playlistIds)
                }
            }
    }

    override suspend fun insertSongToPlaylist(track: Track, playlistId: Long) {
        // Сохраняем трек в базу
        tracksDao.insertTrack(track.toEntity())

        // Создаем связь трек-плейлист
        playlistsDao.insertCrossRef(
            PlaylistTrackCrossRef(
                playlistId = playlistId,
                trackId = track.id
            )
        )
    }

    override suspend fun deleteSongFromPlaylist(track: Track, playlistId: Long) {
        playlistsDao.deleteCrossRef(playlistId, track.id)
    }

    override suspend fun updateTrackFavoriteStatus(track: Track, isFavorite: Boolean) {
        val entity = track.toEntity().copy(favorite = isFavorite)
        tracksDao.insertTrack(entity)
    }

    override suspend fun deleteTracksByPlaylistId(playlistId: Long) {
        playlistsDao.deleteAllCrossRefsForPlaylist(playlistId)
    }

    override fun getTrackById(id: Long): Flow<Track?> {
        return tracksDao.getTrackById(id)
            .map { entity ->
                entity?.let {
                    val playlistIds = playlistsDao.getPlaylistIdsForTrack(it.id)
                    it.toTrack(playlistIds)
                }
            }
    }
}