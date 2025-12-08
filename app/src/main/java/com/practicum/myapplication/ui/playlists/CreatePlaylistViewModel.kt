package com.practicum.myapplication.ui.playlists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.practicum.myapplication.data.db.ImageFileManager
import com.practicum.myapplication.domain.PlaylistsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CreatePlaylistViewModel(
    private val playlistsRepository: PlaylistsRepository,
    private val imageFileManager: ImageFileManager
) : ViewModel() {

    // Храним временный Uri (из галереи)
    private var tempImageUri: String? = null

    // Flow для превью в UI
    private val _coverImageUri = MutableStateFlow<String?>(null)
    val coverImageUri = _coverImageUri.asStateFlow()

    // Устанавливаем временный Uri при выборе картинки
    fun setTempImageUri(uri: String) {
        tempImageUri = uri
        _coverImageUri.value = uri  // Показываем превью
    }

    // Создаем плейлист с копированием картинки
    fun createNewPlaylist(name: String, description: String) {
        viewModelScope.launch {
            var finalImageUri: String? = null

            // Если есть выбранная картинка - копируем её
            tempImageUri?.let { uriString ->
                // Преобразуем строку в Uri
                val uri = android.net.Uri.parse(uriString)
                val copiedUri = imageFileManager.copyImageToAppStorage(uri)
                finalImageUri = copiedUri?.toString()
            }

            // Создаем плейлист с сохраненным Uri
            playlistsRepository.addNewPlaylist(
                name = name,
                description = description,
                coverImageUri = finalImageUri  // Используем сохраненный Uri
            )

            // Сбрасываем временные данные
            tempImageUri = null
            _coverImageUri.value = null
        }
    }

    // Метод для получения Uri из сохраненного пути (для отображения)
    fun getImageUriFromSavedPath(path: String?): String? {
        if (path.isNullOrEmpty()) return null
        val uri = imageFileManager.getUriFromPath(path)
        return uri?.toString()
    }

    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val repository = com.practicum.myapplication.creator.Creator.getPlaylistsRepository()
                    val imageFileManager = com.practicum.myapplication.creator.Creator.getImageFileManager()
                    return CreatePlaylistViewModel(repository, imageFileManager) as T
                }
            }
    }
}