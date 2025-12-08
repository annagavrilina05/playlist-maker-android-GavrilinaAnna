package com.practicum.myapplication.ui.trackdetails

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.PlaylistAdd
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.practicum.myapplication.R
import com.practicum.myapplication.ui.playlists.PlaylistViewModel
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import androidx.core.net.toUri

@Composable
fun TrackDetailsScreen(
    trackId: Long,
    onBackClick: () -> Unit
) {
    val viewModel: TrackDetailsViewModel = viewModel(
        factory = TrackDetailsViewModel.getViewModelFactory(trackId)
    )

    val track by viewModel.track.collectAsState(null)
    var showPlaylistSheet by remember { mutableStateOf(false) }

    if (track == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    val safeTrack = track!!

    var isFavorite by remember { mutableStateOf(safeTrack.favorite) }
    val playlistViewModel: PlaylistViewModel = viewModel(factory = PlaylistViewModel.getViewModelFactory())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Заголовок с кнопкой назад
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(id = R.dimen.padding_large)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = stringResource(R.string.back),
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        // Контент трека
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(dimensionResource(id = R.dimen.padding_large)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_large))
        ) {
            // Обложка трека
            Box(
                modifier = Modifier
                    .size(dimensionResource(id = R.dimen.track_image_size)),
                contentAlignment = Alignment.Center
            ) {
                if (safeTrack.image.isNotEmpty()) {
                    val highQualityUrl = safeTrack.image.replace("100x100bb.jpg", "512x512bb.jpg")

                    AsyncImage(
                        model = highQualityUrl,
                        contentDescription = "Обложка трека ${safeTrack.trackName}",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                        placeholder = painterResource(id = R.drawable.ic_music),
                        error = painterResource(id = R.drawable.ic_music)
                    )
                } else {
                    // Если нет URL - показываем заглушку
                    Image(
                        painter = painterResource(id = R.drawable.ic_music),
                        contentDescription = "Трек ${safeTrack.trackName}",
                        modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size))
                    )
                }
            }


            // Название трека
            Text(
                text = safeTrack.trackName,
                fontSize = dimensionResource(R.dimen.text_size_large).value.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.fillMaxWidth()
            )

            // Исполнитель
            Text(
                text = safeTrack.artistName,
                fontSize = dimensionResource(R.dimen.text_size_small).value.sp,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.fillMaxWidth()
            )

            // Кнопки действий
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Кнопка добавления в плейлист (слева)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    IconButton(
                        onClick = { showPlaylistSheet = true },
                        modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size))
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.PlaylistAdd,
                            contentDescription = stringResource(R.string.add_in_playlist),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size))
                        )
                    }
                }

                // Кнопка избранного (справа)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    IconButton(
                        onClick = {
                            isFavorite = !isFavorite
                            playlistViewModel.toggleFavorite(safeTrack, isFavorite)
                        },
                        modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size))
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = stringResource(R.string.favorites),
                            tint = if (isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size))
                        )
                    }
                }
            }

            // Длительность трека
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.duration),
                    fontSize = dimensionResource(R.dimen.text_size_small).value.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = safeTrack.trackTime,
                    fontSize = dimensionResource(R.dimen.text_size_small).value.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        // Bottom Sheet для добавление трека в плейлист
        if (showPlaylistSheet) {
            PlaylistSelectionBottomSheet(
                onDismiss = { showPlaylistSheet = false },
                onPlaylistSelected = { playlist ->
                    playlistViewModel.addTrackToPlaylist(safeTrack, playlist.id)
                    showPlaylistSheet = false
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistSelectionBottomSheet(
    onDismiss: () -> Unit,
    onPlaylistSelected: (com.practicum.myapplication.domain.models.Playlist) -> Unit,
    playlistViewModel: PlaylistViewModel = viewModel(factory = PlaylistViewModel.getViewModelFactory())
) {
    val playlists by playlistViewModel.playlists.collectAsState(emptyList())
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        // LazyColumn для прокрутки
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f),
            contentPadding = PaddingValues(
                horizontal = dimensionResource(id = R.dimen.padding_large),
                vertical = dimensionResource(id = R.dimen.padding_medium)
            )
        ) {
            // Заголовок
            item {
                Text(
                    text = stringResource(R.string.choose_playlist),
                    fontSize = dimensionResource(R.dimen.text_size_large).value.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_large)))
            }

            // Список плейлистов или сообщение о пустоте
            if (playlists.isEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.playlists_not_found),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = dimensionResource(id = R.dimen.text_size_medium).value.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = dimensionResource(id = R.dimen.padding_large)),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                items(items = playlists, key = { it.id }) { playlist ->
                    PlaylistSelectionItem(
                        playlist = playlist,
                        onClick = { onPlaylistSelected(playlist) }
                    )
                }
            }
        }
    }
}

@Composable
fun PlaylistSelectionItem(
    playlist: com.practicum.myapplication.domain.models.Playlist,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = dimensionResource(id = R.dimen.padding_medium)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(dimensionResource(id = R.dimen.icon_size_large))
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(dimensionResource(id = R.dimen.corner_radius))),
            contentAlignment = Alignment.Center
        ) {
            if (playlist.coverImageUri != null) {
                // Показываем обложку плейлиста
                AsyncImage(
                    model = playlist.coverImageUri.toUri(),
                    contentDescription = playlist.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                // Показываем заглушку
                Icon(
                    painter = painterResource(id = R.drawable.ic_music),
                    contentDescription = stringResource(R.string.playlist),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size))
                )
            }
        }

        Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.padding_medium)))

        Text(
            text = playlist.name,
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = dimensionResource(id = R.dimen.text_size_medium).value.sp,
            modifier = Modifier.weight(1f)
        )
    }
}