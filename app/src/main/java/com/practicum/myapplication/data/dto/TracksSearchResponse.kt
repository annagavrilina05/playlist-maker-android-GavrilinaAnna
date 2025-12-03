package com.practicum.myapplication.data.dto

class TracksSearchResponse(
    val resultCount: Int,
    val results: List<TrackDto>
): BaseResponse()