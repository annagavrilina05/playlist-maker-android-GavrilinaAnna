package com.practicum.myapplication.ui.playlists

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.practicum.myapplication.R

@Composable
fun CreatePlaylistScreen(
    onBackClick: () -> Unit,
    playlistViewModel: PlaylistViewModel = viewModel(factory = PlaylistViewModel.getViewModelFactory())
) {
    var name by remember { mutableStateOf(TextFieldValue()) }
    var description by remember { mutableStateOf(TextFieldValue()) }

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
                text = stringResource(R.string.create_playlist),
                fontSize = dimensionResource(R.dimen.text_size_large).value.sp,
                fontWeight = FontWeight.Medium,
                color = colorResource(id = R.color.black)
            )
        }

        // Форма создания
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(dimensionResource(id = R.dimen.padding_large)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
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

            // Поле названия
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = colorResource(id = R.color.white),
                    unfocusedContainerColor = colorResource(id = R.color.white),
                    focusedIndicatorColor = colorResource(id = R.color.blue),
                    unfocusedIndicatorColor = colorResource(id = R.color.gray),
                    focusedLabelColor = colorResource(id = R.color.blue),
                    unfocusedLabelColor = colorResource(id = R.color.gray),
                    cursorColor = colorResource(id = R.color.blue)
                ),
                label = { Text(stringResource(R.string.playlist_name)) },
                placeholder = { Text(stringResource(R.string.input_name)) },
                shape = RoundedCornerShape(dimensionResource(id = R.dimen.corner_radius))
            )

            // Поле описания
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = colorResource(id = R.color.white),
                    unfocusedContainerColor = colorResource(id = R.color.white),
                    focusedIndicatorColor = colorResource(id = R.color.blue),
                    unfocusedIndicatorColor = colorResource(id = R.color.gray),
                    focusedLabelColor = colorResource(id = R.color.blue),
                    unfocusedLabelColor = colorResource(id = R.color.gray),
                    cursorColor = colorResource(id = R.color.blue)
                ),
                label = { Text(stringResource(R.string.description)) },
                placeholder = { Text(stringResource(R.string.input_description)) },
                shape = RoundedCornerShape(dimensionResource(id = R.dimen.corner_radius))
            )

            Spacer(modifier = Modifier.weight(1f))

            // Кнопка сохранения
            Button(
                onClick = {
                    if (name.text.isNotBlank()) {
                        playlistViewModel.createNewPlaylist(name.text, description.text)
                        onBackClick()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimensionResource(id = R.dimen.icon_size_large)),
                shape = RoundedCornerShape(dimensionResource(id = R.dimen.corner_radius)),
                enabled = name.text.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.blue),
                    contentColor = colorResource(id = R.color.white)
                )

            ) {
                Text(
                    text = stringResource(R.string.save),
                    fontSize = dimensionResource(id = R.dimen.text_size_medium).value.sp
                )
            }
        }
    }
}
