package com.practicum.myapplication.ui.trackdetails

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.PlaylistAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.practicum.myapplication.R
import com.practicum.myapplication.domain.models.Track
import com.practicum.myapplication.ui.playlists.PlaylistViewModel

@Composable
fun TrackDetailsScreen(
    track: Track,
    onBackClick: () -> Unit,
    playlistViewModel: PlaylistViewModel = viewModel(factory = PlaylistViewModel.getViewModelFactory())
) {
    var showPlaylistSheet by remember { mutableStateOf(false) }
    var isFavorite by remember { mutableStateOf(track.favorite) }



    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.white))
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
                    tint = colorResource(id = R.color.black)
                )
            }
            Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.padding_medium)))
            Text(
                text = stringResource(R.string.track_details),
                fontSize = dimensionResource(R.dimen.text_size_large).value.sp,
                fontWeight = FontWeight.Medium,
                color = colorResource(id = R.color.black)
            )
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
                    .size(dimensionResource(id = R.dimen.track_image_size))
                    .background(colorResource(id = R.color.light_gray), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_music),
                    contentDescription = stringResource(R.string.track_image),
                    tint = colorResource(id = R.color.gray),
                    modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size_large))
                )
            }

            // Информация о треке
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = track.trackName,
                    fontSize = dimensionResource(R.dimen.text_size_large).value.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(id = R.color.black)
                )
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_small)))
                Text(
                    text = track.artistName,
                    fontSize = dimensionResource(R.dimen.text_size_medium).value.sp,
                    color = colorResource(id = R.color.gray)
                )
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_small)))
                Text(
                    text = track.trackTime,
                    fontSize = dimensionResource(R.dimen.text_size_small).value.sp,
                    color = colorResource(id = R.color.gray)
                )
            }

            // Кнопки действий
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Кнопка избранного
                IconButton(
                    onClick = {
                        isFavorite = !isFavorite
                        playlistViewModel.toggleFavorite(track, isFavorite)
                    },
                    modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size_large))
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = stringResource(R.string.favorites),
                        tint = if (isFavorite) colorResource(id = R.color.red) else colorResource(id = R.color.gray)
                    )
                }

                // Кнопка добавления в плейлист
                IconButton(
                    onClick = { showPlaylistSheet = true },
                    modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size_large))
                ) {
                    Icon(
                        imageVector = Icons.Outlined.PlaylistAdd,
                        contentDescription = stringResource(R.string.add_in_playlist),
                        tint = colorResource(id = R.color.gray)
                    )
                }
            }
        }
    }

    // Bottom Sheet для выбора плейлиста (всплывает снизу)
    if (showPlaylistSheet) {
        PlaylistSelectionBottomSheet(
            onDismiss = { showPlaylistSheet = false },
            onPlaylistSelected = { playlist ->
                playlistViewModel.addTrackToPlaylist(track, playlist.id)
                showPlaylistSheet = false
            }
        )
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

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(id = R.dimen.padding_large))
        ) {
            Text(
                text = stringResource(R.string.choose_playlist),
                fontSize = dimensionResource(R.dimen.text_size_large).value.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(id = R.color.black)
            )

            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_medium)))

            if (playlists.isEmpty()) {
                Text(
                    text = stringResource(R.string.playlists_not_found),
                    color = colorResource(id = R.color.gray),
                    modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium))
                )
            } else {
                playlists.forEach { playlist ->
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
            .padding(dimensionResource(id = R.dimen.padding_medium)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_music),
            contentDescription = stringResource(R.string.playlist),
            tint = colorResource(id = R.color.gray),
            modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size))
        )
        Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.padding_medium)))
        Column {
            Text(
                text = playlist.name,
                color = colorResource(id = R.color.black),
                fontSize = dimensionResource(id = R.dimen.text_size_medium).value.sp
            )
            Text(
                text = "${playlist.tracks.size} ${stringResource(R.string.trekov)}",
                color = colorResource(id = R.color.gray),
                fontSize = dimensionResource(id = R.dimen.text_size_small).value.sp
            )
        }
    }
}