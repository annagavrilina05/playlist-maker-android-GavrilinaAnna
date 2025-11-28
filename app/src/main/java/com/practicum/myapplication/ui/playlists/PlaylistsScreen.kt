package com.practicum.myapplication.ui.playlists

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.practicum.myapplication.R

@Composable
fun PlaylistsScreen(
    onBackClick: () -> Unit,
    onCreatePlaylistClick: () -> Unit,
    onPlaylistClick: (Long) -> Unit,
    playlistViewModel: PlaylistViewModel = viewModel(factory = PlaylistViewModel.getViewModelFactory())
) {
    val playlists by playlistViewModel.playlists.collectAsState(emptyList())

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(id = R.color.white))
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
                        tint = colorResource(id = R.color.black)
                    )
                }
                Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.padding_medium)))
                Text(
                    text = stringResource(R.string.playlists),
                    fontSize = dimensionResource(R.dimen.text_size_large).value.sp,
                    fontWeight = FontWeight.Medium,
                    color = colorResource(id = R.color.black)
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
                        color = colorResource(id = R.color.gray),
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
                            onClick = { onPlaylistClick(playlist.id) }
                        )
                        Divider(
                            color = colorResource(id = R.color.light_gray),
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
            containerColor = colorResource(id = R.color.blue),
            contentColor = colorResource(id = R.color.white),
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
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(dimensionResource(id = R.dimen.padding_medium)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(dimensionResource(id = R.dimen.icon_size_large))
                .background(colorResource(id = R.color.light_gray), RoundedCornerShape(dimensionResource(id = R.dimen.corner_radius))),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_music),
                contentDescription = stringResource(R.string.playlist),
                tint = colorResource(id = R.color.gray),
                modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size))
            )
        }

        Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.padding_medium)))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = playlist.name,
                fontSize = dimensionResource(id = R.dimen.text_size_medium).value.sp,
                fontWeight = FontWeight.Medium,
                color = colorResource(id = R.color.black)
            )
            Text(
                text = "${playlist.tracks.size} ${stringResource(R.string.trekov)} • ${playlist.description}",
                fontSize = dimensionResource(id = R.dimen.text_size_small).value.sp,
                color = colorResource(id = R.color.gray)
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