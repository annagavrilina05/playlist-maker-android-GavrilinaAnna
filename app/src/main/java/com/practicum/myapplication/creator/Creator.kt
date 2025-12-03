package com.practicum.myapplication.creator

import com.practicum.myapplication.data.PlaylistsRepositoryImpl
import com.practicum.myapplication.data.network.RetrofitNetworkClient
import com.practicum.myapplication.data.network.TracksRepositoryImpl
import com.practicum.myapplication.domain.ITunesApiService
import com.practicum.myapplication.domain.PlaylistsRepository
import com.practicum.myapplication.domain.TracksRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Creator {
    private val scope = CoroutineScope(Dispatchers.IO)
    private const val BASE_URL = "https://itunes.apple.com/"

    // Создаем экземпляр Retrofit
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // Создаем API сервис
    private val iTunesApiService: ITunesApiService = retrofit.create(ITunesApiService::class.java)

    // Метод для репозитория треков
    fun getTracksRepository(): TracksRepository {
        // Передаем правильный ITunesApiService, а не Storage
        return TracksRepositoryImpl(
            RetrofitNetworkClient(iTunesApiService),
            scope
        )
    }

    // Метод для репозитория плейлистов
    fun getPlaylistsRepository(): PlaylistsRepository {
        return PlaylistsRepositoryImpl(scope)
    }
}