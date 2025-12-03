package com.practicum.myapplication.ui.playlists

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.practicum.myapplication.R
import com.practicum.myapplication.domain.models.Track

@Composable
fun PlaylistScreen(
    modifier: Modifier = Modifier,
    playlistId: Long,
    onBackClick: () -> Unit,
    onTrackClick: (Long) -> Unit,
    viewModelFactory: ViewModelProvider.Factory
) {
    val viewModel: PlaylistDetailViewModel = viewModel(
        factory = viewModelFactory
    )

    val playlist by viewModel.playlist.collectAsState(null)

    if (playlist == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                color = colorResource(id = R.color.blue)
            )
        }
        return
    }

    val safePlaylist = playlist!!

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.white))
    ) {
        // Кнопка назад
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .padding(
                    start = dimensionResource(id = R.dimen.padding_medium),
                    top = dimensionResource(id = R.dimen.padding_medium)
                )
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = stringResource(R.string.back),
                tint = colorResource(id = R.color.black)
            )
        }

        // Контент плейлиста
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small))
        ) {
            // Информация о плейлисте
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = dimensionResource(id = R.dimen.padding_large)),
                    horizontalAlignment = Alignment.Start
                ) {
                    // Картинка плейлиста
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(dimensionResource(R.dimen.track_image_size))
                            .clickable {
                                // Запустить выбор изображения
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            // Заглушка - маленькая (позже сделать выбор размера изображения через if-else и проверку наличия соответствующего параметра)
                            Image(
                                painter = painterResource(id = R.drawable.add_image),
                                contentDescription = stringResource(R.string.playlist_image),
                                modifier = Modifier.size(dimensionResource(R.dimen.add_image_size))
                            )
                        }
                    }

                    // Название плейлиста
                    Text(
                        text = safePlaylist.name,
                        fontSize = dimensionResource(R.dimen.text_size_large).value.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(id = R.color.black),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = dimensionResource(id = R.dimen.padding_small))
                    )

                    // Описание плейлиста
                    if (safePlaylist.description.isNotBlank()) {
                        Text(
                            text = safePlaylist.description,
                            fontSize = dimensionResource(R.dimen.text_size_small).value.sp,
                            color = colorResource(id = R.color.black),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = dimensionResource(id = R.dimen.padding_small))
                        )
                    }

                    // Время + количество треков
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = dimensionResource(id = R.dimen.padding_large)),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        val totalDuration = calculateTotalDuration(safePlaylist.tracks)
                        val trackCount = safePlaylist.tracks.size

                        Text(
                            text = "$totalDuration мин",
                            fontSize = dimensionResource(R.dimen.text_size_small).value.sp,
                            color = colorResource(id = R.color.gray)
                        )

                        Text(
                            text = " • ",
                            fontSize = dimensionResource(R.dimen.text_size_small).value.sp,
                            color = colorResource(id = R.color.gray)
                        )

                        // Склонение слова "треки"
                        val trackText = when {
                            trackCount % 10 == 1 && trackCount % 100 != 11 ->
                                stringResource(R.string.track_single)
                            trackCount % 10 in 2..4 && trackCount % 100 !in 12..14 ->
                                stringResource(R.string.track_few)
                            else -> stringResource(R.string.track_many)
                        }

                        Text(
                            text = "$trackCount $trackText",
                            fontSize = dimensionResource(R.dimen.text_size_small).value.sp,
                            color = colorResource(id = R.color.gray)
                        )
                    }

                    // Кнопка троеточия
                    IconButton(
                        onClick = {
                            // Показать меню действий
                        },
                        modifier = Modifier
                            .padding(bottom = dimensionResource(id = R.dimen.padding_large))
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.MoreVert,
                            contentDescription = stringResource(R.string.more_actions),
                            tint = colorResource(id = R.color.black),
                            modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size))
                        )
                    }
                }
            }

            // Список треков
            if (safePlaylist.tracks.isNotEmpty()) {
                items(safePlaylist.tracks) { track ->
                    TrackListItem(
                        track = track,
                        onClick = { onTrackClick(track.id) }
                    )
                    Divider(
                        color = colorResource(id = R.color.light_gray),
                        thickness = dimensionResource(id = R.dimen.divider_height),
                        modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.padding_large))
                    )
                }
            } else {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = dimensionResource(id = R.dimen.padding_large)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.no_tracks_in_playlist),
                            color = colorResource(id = R.color.gray),
                            fontSize = dimensionResource(id = R.dimen.text_size_medium).value.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TrackListItem(
    track: Track,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(
                horizontal = dimensionResource(id = R.dimen.padding_large),
                vertical = dimensionResource(id = R.dimen.padding_small)
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Изображение трека
        Box(
            modifier = Modifier
                .size(dimensionResource(id = R.dimen.icon_size_large))
                .clip(RoundedCornerShape(dimensionResource(R.dimen.divider_height)))
                .background(colorResource(id = R.color.light_gray)),
            contentAlignment = Alignment.Center
        ) {
            if (track.image.isNotEmpty()) {
                val highQualityUrl = track.image.replace("100x100bb.jpg", "512x512bb.jpg")

                AsyncImage(
                    model = highQualityUrl,
                    contentDescription = "Обложка трека ${track.trackName}",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    placeholder = painterResource(id = R.drawable.ic_music),
                    error = painterResource(id = R.drawable.ic_music)
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.ic_music),
                    contentDescription = "Трек ${track.trackName}",
                    modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size))
                )
            }
        }

        Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.padding_medium)))

        // Информация о треке
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = track.trackName,
                fontWeight = FontWeight.Bold,
                fontSize = dimensionResource(id = R.dimen.text_size_medium).value.sp,
                color = colorResource(id = R.color.black)
            )
            Text(
                text = track.artistName,
                fontSize = dimensionResource(id = R.dimen.text_size_small).value.sp,
                color = colorResource(id = R.color.gray)
            )
        }

        Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.padding_medium)))

        // Время трека
        Text(
            text = track.trackTime,
            fontSize = dimensionResource(id = R.dimen.text_size_small).value.sp,
            color = colorResource(id = R.color.gray)
        )
    }
}

private fun calculateTotalDuration(tracks: List<Track>): Int {
    return tracks.sumOf { parseDurationToMinutes(it.trackTime) }
}

private fun parseDurationToMinutes(duration: String): Int {
    return try {
        val parts = duration.split(":")
        if (parts.size >= 2) {
            parts[0].toInt()
        } else {
            0
        }
    } catch (e: Exception) {
        0
    }
}