package com.practicum.myapplication.data.converter

import com.practicum.myapplication.data.database.entity.PlaylistEntity
import com.practicum.myapplication.domain.models.Playlist
import com.practicum.myapplication.domain.models.Track

fun PlaylistEntity.toDomain(tracks: List<Track> = emptyList()): Playlist {
    return Playlist(
        id = this.id,
        name = this.name,
        description = this.description,
        coverImageUri = this.coverImageUri,
        tracks = tracks
    )
}

fun Playlist.toEntity(): PlaylistEntity {
    return PlaylistEntity(
        id = this.id,
        name = this.name,
        description = this.description,
        coverImageUri = this.coverImageUri
    )
}