// data/database/dao/PlaylistsDao.kt
package com.practicum.myapplication.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.practicum.myapplication.data.database.entity.PlaylistEntity
import com.practicum.myapplication.data.database.entity.PlaylistTrackCrossRef
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistEntity): Long

    @Query("SELECT * FROM playlists")
    fun getAllPlaylists(): Flow<List<PlaylistEntity>>

    @Query("SELECT * FROM playlists WHERE id = :playlistId")
    fun getPlaylistById(playlistId: Long): Flow<PlaylistEntity?>

    @Query("DELETE FROM playlists WHERE id = :playlistId")
    suspend fun deletePlaylistById(playlistId: Long)

    // Методы для связи треков и плейлистов
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCrossRef(crossRef: PlaylistTrackCrossRef)

    @Query("DELETE FROM playlist_track_cross_ref WHERE playlistId = :playlistId AND trackId = :trackId")
    suspend fun deleteCrossRef(playlistId: Long, trackId: Long)

    @Query("DELETE FROM playlist_track_cross_ref WHERE playlistId = :playlistId")
    suspend fun deleteAllCrossRefsForPlaylist(playlistId: Long)

    @Query("SELECT trackId FROM playlist_track_cross_ref WHERE playlistId = :playlistId")
    suspend fun getTrackIdsForPlaylist(playlistId: Long): List<Long>

    @Query("SELECT playlistId FROM playlist_track_cross_ref WHERE trackId = :trackId")
    suspend fun getPlaylistIdsForTrack(trackId: Long): List<Long>
}