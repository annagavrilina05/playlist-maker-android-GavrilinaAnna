package com.practicum.myapplication.data.network

import com.practicum.myapplication.data.dto.TracksSearchRequest
import com.practicum.myapplication.domain.NetworkClient
import com.practicum.myapplication.domain.ITunesApiService
import com.practicum.myapplication.data.dto.BaseResponse
import java.io.IOException

class RetrofitNetworkClient(private val api: ITunesApiService) : NetworkClient {

    override suspend fun doRequest(dto: Any): BaseResponse {
        return when (dto) {
            is TracksSearchRequest -> {
                try {
                    val response = api.searchTracks(
                        query = dto.expression,
                        media = "music",
                        entity = "song",
                        limit = 10
                    )
                    response.resultCode = 200
                    response
                } catch (e: IOException) {
                    throw IOException("Network error: ${e.message ?: "No internet connection"}")
                } catch (e: Exception) {
                    throw Exception("Unexpected error: ${e.message}")
                }
            }
            else -> throw IllegalArgumentException("Invalid request type")
        }
    }
}