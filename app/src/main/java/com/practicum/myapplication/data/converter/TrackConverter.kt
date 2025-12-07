package com.practicum.myapplication.data.converter

import com.practicum.myapplication.data.database.entity.TrackEntity
import com.practicum.myapplication.domain.models.Track

fun TrackEntity.toTrack(playlistIds: List<Long> = emptyList()): Track {
    return Track(
        id = this.id,
        trackName = this.trackName,
        artistName = this.artistName,
        trackTime = this.trackTime,
        image = this.image,
        favorite = this.favorite,
        playlistId = playlistIds
    )
}

fun Track.toEntity(): TrackEntity {
    return TrackEntity(
        id = this.id,
        trackName = this.trackName,
        artistName = this.artistName,
        trackTime = this.trackTime,
        image = this.image,
        favorite = this.favorite
    )
}