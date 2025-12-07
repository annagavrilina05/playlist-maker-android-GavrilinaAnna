package com.practicum.myapplication.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.practicum.myapplication.data.database.entity.TrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TracksDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: TrackEntity)

    @Query("SELECT * FROM tracks WHERE trackName = :trackName AND artistName = :artistName")
    fun getTrackByNameAndArtist(trackName: String, artistName: String): Flow<TrackEntity?>

    @Query("SELECT * FROM tracks WHERE favorite = 1")
    fun getFavoriteTracks(): Flow<List<TrackEntity>>

    @Query("SELECT * FROM tracks WHERE id = :id")
    fun getTrackById(id: Long): Flow<TrackEntity?>

    @Update
    suspend fun updateTrack(track: TrackEntity)

    @Query("SELECT * FROM tracks")
    suspend fun getAllTracks(): List<TrackEntity>

    @Query("SELECT * FROM tracks WHERE id = :id")
    suspend fun getTrackByIdSync(id: Long): TrackEntity?
}