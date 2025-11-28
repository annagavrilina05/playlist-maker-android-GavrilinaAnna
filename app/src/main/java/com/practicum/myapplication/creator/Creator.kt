package com.practicum.myapplication.creator

import com.practicum.myapplication.data.PlaylistsRepositoryImpl
import com.practicum.myapplication.data.network.RetrofitNetworkClient
import com.practicum.myapplication.data.network.TracksRepositoryImpl
import com.practicum.myapplication.domain.PlaylistsRepository
import com.practicum.myapplication.domain.TracksRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

object Creator {
    private val scope = CoroutineScope(Dispatchers.IO)

    //Метод для репозитория треков
    fun getTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient(Storage()), scope)
    }

    // Метод для репозитория плейлистов
    fun getPlaylistsRepository(): PlaylistsRepository {
        return PlaylistsRepositoryImpl(scope)
    }
}