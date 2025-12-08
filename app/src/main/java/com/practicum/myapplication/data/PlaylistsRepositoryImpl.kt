package com.practicum.myapplication.data

import com.practicum.myapplication.data.converter.toDomain
import com.practicum.myapplication.data.converter.toTrack
import com.practicum.myapplication.data.database.dao.PlaylistsDao
import com.practicum.myapplication.data.database.dao.TracksDao
import com.practicum.myapplication.domain.PlaylistsRepository
import com.practicum.myapplication.domain.models.Playlist
import com.practicum.myapplication.domain.models.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class PlaylistsRepositoryImpl(
    private val playlistsDao: PlaylistsDao,
    private val tracksDao: TracksDao,
    private val scope: CoroutineScope
) : PlaylistsRepository {

    override fun getPlaylist(playlistId: Long): Flow<Playlist?> {
        return playlistsDao.getPlaylistById(playlistId)
            .combine(tracksDao.getAllTracksFlow()) { playlistEntity, allTrackEntities  ->
                playlistEntity?.let { entity ->
                    // Получаем треки для этого плейлиста
                    val trackIds = playlistsDao.getTrackIdsForPlaylist(playlistId)
                    val tracks = allTrackEntities
                        .filter { trackIds.contains(it.id) }
                        .map { trackEntity ->
                            val playlistIds = playlistsDao.getPlaylistIdsForTrack(trackEntity.id)
                            trackEntity.toTrack(playlistIds)
                        }

                    entity.toDomain(tracks)
                }
            }
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> {
        return playlistsDao.getAllPlaylists()
            .combine(tracksDao.getAllTracksFlow()) { playlistEntities, allTrackEntities  ->
                playlistEntities.map { playlistEntity ->
                    // Получаем треки для каждого плейлиста
                    val trackIds = playlistsDao.getTrackIdsForPlaylist(playlistEntity.id)
                    val tracks = allTrackEntities
                        .filter { trackIds.contains(it.id) }
                        .map { trackEntity ->
                            val playlistIds = playlistsDao.getPlaylistIdsForTrack(trackEntity.id)
                            trackEntity.toTrack(playlistIds)
                        }

                    playlistEntity.toDomain(tracks)
                }
            }
    }

    override suspend fun addNewPlaylist(name: String, description: String, coverImageUri: String?) {
        val entity = com.practicum.myapplication.data.database.entity.PlaylistEntity(
            name = name,
            description = description,
            coverImageUri = coverImageUri
        )
        playlistsDao.insertPlaylist(entity)
    }

    override suspend fun deletePlaylistById(id: Long) {
        // Удаляем связи трек-плейлист
        playlistsDao.deleteAllCrossRefsForPlaylist(id)
        // Удаляем сам плейлист
        playlistsDao.deletePlaylistById(id)
    }
}