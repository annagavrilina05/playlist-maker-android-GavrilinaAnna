package com.practicum.myapplication.creator

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.practicum.myapplication.data.PlaylistsRepositoryImpl
import com.practicum.myapplication.data.SearchHistoryRepositoryImpl
import com.practicum.myapplication.data.db.AppDatabase
import com.practicum.myapplication.data.db.ImageFileManager
import com.practicum.myapplication.data.network.RetrofitNetworkClient
import com.practicum.myapplication.data.network.TracksRepositoryImpl
import com.practicum.myapplication.data.preferences.SearchHistoryPreferences
import com.practicum.myapplication.data.preferences.ThemePreferences
import com.practicum.myapplication.domain.ITunesApiService
import com.practicum.myapplication.domain.PlaylistsRepository
import com.practicum.myapplication.domain.SearchHistoryRepository
import com.practicum.myapplication.domain.TracksRepository
import com.practicum.myapplication.ui.playlists.CreatePlaylistViewModel
import com.practicum.myapplication.ui.theme.ThemeViewModel
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
    private var appContext: Context? = null
    fun init(context: Context) {
        appContext = context.applicationContext
    }

    fun getAppContext(): Context {
        return appContext ?: throw IllegalStateException("AppContext not initialized")
    }

    // Метод для репозитория треков
    fun getTracksRepository(): TracksRepository {
        val context = appContext ?: throw IllegalStateException("AppContext not initialized. Call Creator.init() first!")
        val database = AppDatabase.getInstance(context)
        return TracksRepositoryImpl(
            networkClient = RetrofitNetworkClient(iTunesApiService),
            tracksDao = database.tracksDao(),
            playlistsDao = database.playlistsDao(),
            scope = scope
        )
    }

    // Метод для репозитория плейлистов
    fun getPlaylistsRepository(): PlaylistsRepository {
        val context = appContext ?: throw IllegalStateException("AppContext not initialized. Call Creator.init() first!")
        val database = AppDatabase.getInstance(context)
        return PlaylistsRepositoryImpl(
            playlistsDao = database.playlistsDao(),
            tracksDao = database.tracksDao(),
            scope = scope
        )
    }

    fun getTracksRepository(context: Context): TracksRepository {
        val database = AppDatabase.getInstance(context)
        return TracksRepositoryImpl(
            networkClient = RetrofitNetworkClient(iTunesApiService),
            tracksDao = database.tracksDao(),
            playlistsDao = database.playlistsDao(),
            scope = scope
        )
    }

    fun getPlaylistsRepository(context: Context): PlaylistsRepository {
        val database = AppDatabase.getInstance(context)
        return PlaylistsRepositoryImpl(
            playlistsDao = database.playlistsDao(),
            tracksDao = database.tracksDao(),
            scope = scope
        )
    }

    fun getSearchHistoryRepository(context: Context): SearchHistoryRepository {
        val preferences = SearchHistoryPreferences(context)
        return SearchHistoryRepositoryImpl(preferences, scope)
    }

    fun getSearchHistoryRepository(): SearchHistoryRepository {
        val context = appContext ?: throw IllegalStateException("Call Creator.init() first!")
        val preferences = SearchHistoryPreferences(context)
        return SearchHistoryRepositoryImpl(preferences, scope)
    }

    fun getNewPlaylistViewModelFactory(): ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return CreatePlaylistViewModel(
                    getPlaylistsRepository(),
                    getImageFileManager()
                ) as T
            }
        }

    fun getThemeViewModelFactory(): ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val themePreferences = ThemePreferences(getAppContext())
                return ThemeViewModel(themePreferences) as T
            }
        }

    fun getImageFileManager(context: Context): ImageFileManager {
        return ImageFileManager(context)
    }

    fun getImageFileManager(): ImageFileManager {
        val context = appContext ?: throw IllegalStateException("AppContext not initialized")
        return ImageFileManager(context)
    }
}