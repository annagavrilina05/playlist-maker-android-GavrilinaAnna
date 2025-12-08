package com.practicum.myapplication.ui.playlists

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.practicum.myapplication.R
import androidx.compose.foundation.combinedClickable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri

@Composable
fun rememberImageFileManager(): com.practicum.myapplication.data.db.ImageFileManager {
    val context = LocalContext.current
    return remember { com.practicum.myapplication.data.db.ImageFileManager(context) }
}

@Composable
fun PlaylistsScreen(
    onBackClick: () -> Unit,
    onCreatePlaylistClick: () -> Unit,
    onPlaylistClick: (Long) -> Unit,
    playlistViewModel: PlaylistViewModel = viewModel(factory = PlaylistViewModel.getViewModelFactory())
) {
    val playlists by playlistViewModel.playlists.collectAsState(emptyList())
    val context = LocalContext.current
    val imageFileManager = rememberImageFileManager()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Заголовок
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
                Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.padding_medium)))
                Text(
                    text = stringResource(R.string.playlists),
                    fontSize = dimensionResource(R.dimen.text_size_large).value.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            // Список плейлистов
            if (playlists.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(dimensionResource(id = R.dimen.padding_large)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.playlists_not_found),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = dimensionResource(id = R.dimen.text_size_medium).value.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = dimensionResource(id = R.dimen.padding_medium)),
                    verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small))
                ) {
                    items(playlists) { playlist ->
                        PlaylistListItem(
                            playlist = playlist,
                            imageFileManager = imageFileManager,
                            onClick = { onPlaylistClick(playlist.id) },
                            onLongClick = {
                                // Удаление по долгому нажатию
                                playlistViewModel.deletePlaylist(playlist.id)
                                Toast.makeText(
                                    context,
                                    "Плейлист \"${playlist.name}\" удален",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            thickness = dimensionResource(id = R.dimen.divider_height)
                        )
                    }
                }
            }
        }

        // Кнопка создания плейлиста
        FloatingActionButton(
            onClick = onCreatePlaylistClick,
            modifier = Modifier
                .padding(dimensionResource(id = R.dimen.padding_large))
                .align(Alignment.BottomEnd),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.background,
            shape = CircleShape
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = stringResource(R.string.create_playlist)
            )
        }
    }
}

@Composable
fun PlaylistListItem(
    playlist: com.practicum.myapplication.domain.models.Playlist,
    imageFileManager: com.practicum.myapplication.data.db.ImageFileManager,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
            .padding(dimensionResource(id = R.dimen.padding_medium)),
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

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = playlist.name,
                fontSize = dimensionResource(id = R.dimen.text_size_medium).value.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground
            )

            // Склонение слова "треки"
            val trackCount = playlist.tracks.size
            val trackText = when {
                trackCount % 10 == 1 && trackCount % 100 != 11 ->
                    stringResource(R.string.track_single)
                trackCount % 10 in 2..4 && trackCount % 100 !in 12..14 ->
                    stringResource(R.string.track_few)
                else -> stringResource(R.string.track_many)
            }
            val trackCountText = "$trackCount $trackText"
            val descriptionText = if (playlist.description.isNotBlank()) {
                " • ${playlist.description}"
            } else {
                ""
            }

            Text(
                text = trackCountText + descriptionText,
                fontSize = dimensionResource(id = R.dimen.text_size_small).value.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PlaylistsScreenPreview() {
    PlaylistsScreen(
        onBackClick = {},
        onCreatePlaylistClick = {},
        onPlaylistClick = {}
    )
}