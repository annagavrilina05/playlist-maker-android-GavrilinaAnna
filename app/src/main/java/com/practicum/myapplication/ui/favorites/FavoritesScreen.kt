package com.practicum.myapplication.ui.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
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
import com.practicum.myapplication.domain.models.Track
import com.practicum.myapplication.ui.playlists.PlaylistViewModel
import com.practicum.myapplication.ui.search.TrackListItem

@Composable
fun FavoritesScreen(
    onBackClick: () -> Unit,
    onTrackClick: (Track) -> Unit = {},
    playlistViewModel: PlaylistViewModel = viewModel(factory = PlaylistViewModel.getViewModelFactory())
) {
    val favoriteTracks by playlistViewModel.favoriteTracks.collectAsState()

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
                text = stringResource(R.string.favorites),
                fontSize = dimensionResource(R.dimen.text_size_large).value.sp,
                fontWeight = FontWeight.Medium,
                color = colorResource(id = R.color.black)
            )
        }

        // Список избранных треков
        if (favoriteTracks.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(dimensionResource(id = R.dimen.padding_large)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.no_favorites),
                    color = colorResource(id = R.color.gray),
                    fontSize = dimensionResource(id = R.dimen.text_size_medium).value.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = dimensionResource(id = R.dimen.padding_medium)),
                verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small))
            ) {
                items(favoriteTracks) { track ->
                    TrackListItem(
                        track = track,
                        onTrackClick = onTrackClick
                    )
                    Divider(
                        color = colorResource(id = R.color.light_gray),
                        thickness = dimensionResource(id = R.dimen.divider_height)
                    )
                }
            }
        }
    }
}

@Composable
fun TrackListItem(
    track: Track,
    onTrackClick: (Track) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onTrackClick(track) }
            .padding(dimensionResource(id = R.dimen.padding_medium)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        androidx.compose.foundation.Image(
            painter = painterResource(id = R.drawable.ic_music),
            contentDescription = stringResource(R.string.track) + {track.trackName},
            modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size))
        )

        Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.padding_medium)))

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

        Text(
            text = track.trackTime,
            fontSize = dimensionResource(id = R.dimen.text_size_small).value.sp,
            color = colorResource(id = R.color.gray)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FavoritesScreenPreview() {
    FavoritesScreen(
        onBackClick = {},
        onTrackClick = {}
    )
}