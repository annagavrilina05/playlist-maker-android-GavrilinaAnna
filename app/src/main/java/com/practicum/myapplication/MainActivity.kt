package com.practicum.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.TextStyle

import androidx.navigation.compose.rememberNavController


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    PlaylistHost(navController = navController)
                }
            }
        }
    }
}

// Текстовые стили
object TextStyles {
    val Medium22 = TextStyle(
        fontSize = 22.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 22.sp,
        letterSpacing = 0.sp
    )
}

@Composable
fun MainScreen(
    onSearchClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onPlaylistsClick: () -> Unit,
    onFavoritesClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.blue))
    ) {
        val contentCardModifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.915f)
            .align(Alignment.BottomCenter)
            .clip(RoundedCornerShape(dimensionResource(id = R.dimen.corner_radius)))
            .background(colorResource(id = R.color.white))
            .padding(top = dimensionResource(id = R.dimen.padding_small), start = dimensionResource(id = R.dimen.padding_medium), end = dimensionResource(id = R.dimen.padding_medium))

        Column(
            modifier = contentCardModifier,
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small))
        ) {
            // Кнопка Поиск
            ListItem(
                icon = {
                    Icon(
                        Icons.Outlined.Search,
                        contentDescription = stringResource(R.string.search),
                        tint = colorResource(id = R.color.black),
                        modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size))
                    )
                },
                text = stringResource(R.string.search),
                onClick = onSearchClick
            )

            // Кнопка Плейлисты
            ListItem(
                icon = {
                    Icon(
                        Icons.Default.LibraryMusic,
                        contentDescription = stringResource(R.string.playlists),
                        tint = colorResource(id = R.color.black),
                        modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size))
                    )
                },
                text = stringResource(R.string.playlists),
                onClick = onPlaylistsClick
            )

            // Кнопка Избранное
            ListItem(
                icon = {
                    Icon(
                        Icons.Outlined.FavoriteBorder,
                        contentDescription = stringResource(R.string.favorites),
                        tint = colorResource(id = R.color.black),
                        modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size))
                    )
                },
                text = stringResource(R.string.favorites),
                onClick = onFavoritesClick
            )

            // Кнопка Настройки
            ListItem(
                icon = {
                    Icon(
                        Icons.Default.Settings,
                        contentDescription = stringResource(R.string.settings),
                        tint = colorResource(id = R.color.black),
                        modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size))
                    )
                },
                text = stringResource(R.string.settings),
                onClick = onSettingsClick
            )
        }

        // Заголовок
        val titleModifier = Modifier.padding(dimensionResource(id = R.dimen.padding_large))
        Box(
            modifier = titleModifier,
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = stringResource(R.string.playlist_maker),
                style = TextStyles.Medium22,
                color = colorResource(id = R.color.white)
            )
        }
    }
}

@Composable
fun ListItem(
    icon: @Composable () -> Unit,
    text: String,
    onClick: () -> Unit
) {
    val listItemModifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(dimensionResource(id = R.dimen.corner_radius)))
        .clickable(onClick = onClick)
        .padding(dimensionResource(id = R.dimen.padding_medium))

    Row(
        modifier = listItemModifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small))
        ) {
            icon()

            Text(
                text = text,
                style = TextStyles.Medium22,
                color = colorResource(id = R.color.black)
            )
        }

        // Иконка стрелочки - серая
        Icon(
            Icons.AutoMirrored.Outlined.KeyboardArrowRight,
            contentDescription = null,
            tint = colorResource(id = R.color.gray),
            modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size))
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            MainScreen(
                onSearchClick = {},
                onSettingsClick = {},
                onPlaylistsClick = {},
                onFavoritesClick = {}
            )
        }
    }
}