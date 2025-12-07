package com.practicum.myapplication.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import com.practicum.myapplication.R
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.practicum.myapplication.domain.models.Track
import androidx.compose.foundation.Image
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.style.TextAlign
import kotlinx.coroutines.delay
import androidx.compose.ui.unit.times
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import coil.compose.AsyncImage


@Composable
fun SearchScreen(
    onBackClick: () -> Unit,
    onTrackClick: (Long) -> Unit = {},
    viewModel: SearchViewModel = viewModel(factory = SearchViewModel.getViewModelFactory())
) {
    var searchQuery by remember { mutableStateOf("") }
    val screenState by viewModel.searchScreenState.collectAsState()
    val searchHistory by viewModel.searchHistory.collectAsState(emptyList())
    var isFocused by remember { mutableStateOf(false) }

    // Дебаунс для поиска при вводе
    LaunchedEffect(searchQuery) {
        if (searchQuery.isNotBlank()) {
            delay(500)
            // Защита от повторного поиска
            if (searchQuery != viewModel.getLastQuery()) {
                viewModel.search(searchQuery)
            }
        } else {
            viewModel.clearSearch()
        }
    }

    // Определяем, нужно ли показывать историю поиска
    val shouldShowHistory = searchHistory.isNotEmpty() &&
            screenState is SearchState.Initial &&
            searchQuery.isEmpty()

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
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size))
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = stringResource(R.string.back),
                    tint = colorResource(id = R.color.black)
                )
            }

            Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.padding_medium)))

            Text(
                text = stringResource(R.string.search_title),
                style = TextStyle(
                    fontSize = dimensionResource(R.dimen.text_size_large).value.sp,
                    fontWeight = FontWeight.Medium
                ),
                color = colorResource(id = R.color.black)
            )
        }

        // Поисковая строка с историей
        SearchFieldWithHistory(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            onClearClick = {
                searchQuery = ""
                viewModel.clearSearch()
            },
            onSearchClick = {
                if (searchQuery.isNotBlank()) {
                    viewModel.search(searchQuery)
                }
            },
            searchHistory = searchHistory,
            shouldShowHistory = isFocused && searchQuery.isEmpty() && searchHistory.isNotEmpty(),
            isFocused = isFocused,
            onFocusChange = { focused -> isFocused = focused },
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = dimensionResource(id = R.dimen.padding_large),
                    vertical = dimensionResource(id = R.dimen.search_top_padding)
                )
        )

        // Отображение состояния поиска
        when (screenState) {
            is SearchState.Searching -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(dimensionResource(id = R.dimen.padding_large)),
                    contentAlignment = Alignment.TopCenter
                ) {
                    CircularProgressIndicator(
                        color = colorResource(id = R.color.blue),
                        modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_large3))
                    )
                }
            }
            is SearchState.Success -> {
                val tracks = (screenState as SearchState.Success).list
                if (tracks.isNotEmpty()) {
                    SearchResultsList(tracks = tracks, onTrackClick = onTrackClick)
                } else {
                    // Если треков не найдено
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.8f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.light_mode_nothing_found),
                                contentDescription = stringResource(R.string.not_found),
                                modifier = Modifier.size(dimensionResource(R.dimen.error_image_size))
                            )

                            Text(
                                text = stringResource(R.string.not_found),
                                color = colorResource(id = R.color.black),
                                fontSize = dimensionResource(id = R.dimen.text_size_medium).value.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
            // Ошибка соединения
            is SearchState.Fail -> {
                val errorState = screenState as SearchState.Fail
                val failedQuery = errorState.lastQuery
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.8f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.light_mode_connection_issue),
                            contentDescription = stringResource(R.string.search_error1),
                            modifier = Modifier.size(dimensionResource(R.dimen.error_image_size))
                        )

                        Text(
                            text = stringResource(R.string.search_error1),
                            color = colorResource(id = R.color.black),
                            fontSize = dimensionResource(id = R.dimen.text_size_medium).value.sp,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = stringResource(R.string.search_error2),
                            color = colorResource(id = R.color.black),
                            fontSize = dimensionResource(id = R.dimen.text_size_medium).value.sp,
                            textAlign = TextAlign.Center
                        )

                        if (failedQuery.isNotEmpty()) {
                            Button(
                                onClick = {
                                    viewModel.search(failedQuery)
                                },
                                modifier = Modifier
                                    .padding(top = dimensionResource(id = R.dimen.padding_large)),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = colorResource(id = R.color.blue),
                                    contentColor = colorResource(id = R.color.white)
                                )

                            ) {
                                Text(
                                    text = stringResource(R.string.refresh_button),
                                    fontSize = dimensionResource(id = R.dimen.text_size_medium).value.sp
                                )
                            }
                        }
                    }
                }
            }
            else -> {
                // Initial state
            }
        }
    }
}

@Composable
fun SearchFieldWithHistory(
    query: String,
    onQueryChange: (String) -> Unit,
    onClearClick: () -> Unit,
    onSearchClick: () -> Unit,
    searchHistory: List<String>,
    shouldShowHistory: Boolean,
    isFocused: Boolean,
    onFocusChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val fieldHeight = if (shouldShowHistory) {
        // Высота поля поиска + (количество элементов * высота одного элемента)
        dimensionResource(id = R.dimen.search_field_height) +
                (searchHistory.take(25).size * dimensionResource(id = R.dimen.history_item_height))
    } else {
        dimensionResource(id = R.dimen.search_field_height)
    }

    val keyboardController = LocalSoftwareKeyboardController.current

    Box(
        modifier = modifier
            .height(fieldHeight)
            .clip(RoundedCornerShape(dimensionResource(id = R.dimen.search_field_corner_radius)))
            .background(colorResource(id = R.color.light_gray))
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Верхняя часть
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dimensionResource(id = R.dimen.padding_medium))
                    .height(dimensionResource(id = R.dimen.search_field_height)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Иконка поиска слева
                IconButton(
                    onClick = {
                        onSearchClick()
                        keyboardController?.hide()
                    },
                    modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size))
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = stringResource(R.string.search),
                        tint = colorResource(id = R.color.gray)
                    )
                }

                Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.padding_small)))

                // Строка поиска
                TextField(
                    value = query,
                    onValueChange = onQueryChange,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .onFocusChanged { focusState -> onFocusChange(focusState.isFocused)
                        },
                    textStyle = TextStyle(
                        fontSize = dimensionResource(R.dimen.text_size_medium).value.sp,
                        color = colorResource(id = R.color.black)
                    ),
                    placeholder = {
                        if (query.isEmpty()) {
                            Text(
                                text = stringResource(R.string.search_hint),
                                color = colorResource(id = R.color.gray),
                                fontSize = dimensionResource(id = R.dimen.text_size_medium).value.sp
                            )
                        }
                    },
                    trailingIcon = {
                        if (query.isNotEmpty()) {
                            IconButton(
                                onClick = {
                                    onClearClick()
                                    keyboardController?.hide()
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Clear,
                                    contentDescription = stringResource(R.string.clear),
                                    tint = colorResource(id = R.color.gray)
                                )
                            }
                        }
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = colorResource(id = R.color.light_gray),
                        unfocusedContainerColor = colorResource(id = R.color.light_gray),
                        disabledContainerColor = colorResource(id = R.color.light_gray),
                        focusedIndicatorColor = colorResource(id = R.color.light_gray),
                        unfocusedIndicatorColor = colorResource(id = R.color.light_gray),
                        disabledIndicatorColor = colorResource(id = R.color.light_gray),
                        cursorColor = colorResource(id = R.color.gray)
                    ),
                    singleLine = true
                )
            }

            // История поиска
            if (shouldShowHistory) {
                HistoryRequests(
                    historyList = searchHistory,
                    onClick = { historyQuery ->
                        onQueryChange(historyQuery)
                    }
                )
            }
        }
    }
}


@Composable
fun HistoryRequests(
    historyList: List<String>,
    onClick: (String) -> Unit
) {
    val limitedHistory = historyList.take(25)

    LazyColumn(
        modifier = Modifier.fillMaxWidth()
    ) {
        items(limitedHistory) { historyItem ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimensionResource(id = R.dimen.history_item_height))
                    .clickable { onClick(historyItem) }
                    .padding(horizontal = dimensionResource(id = R.dimen.padding_small)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Schedule,
                    contentDescription = null,
                    tint = colorResource(id = R.color.gray),
                    modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size))
                )
                Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.padding_small)))
                Text(
                    text = historyItem,
                    color = colorResource(id = R.color.black),
                    fontSize = dimensionResource(id = R.dimen.text_size_medium).value.sp
                )
            }
        }
    }
}

@Composable
fun TrackListItem(
    track: Track,
    onTrackClick: (Long) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onTrackClick(track.id) }
            .padding(dimensionResource(id = R.dimen.padding_medium)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Блок для изображения трека
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
                // Если нет URL - показываем заглушку
                Image(
                    painter = painterResource(id = R.drawable.ic_music),
                    contentDescription = "Трек ${track.trackName}",
                    modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size))
                )
            }
        }

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

@Composable
fun SearchResultsList(
    tracks: List<Track>,
    onTrackClick: (Long) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = dimensionResource(id = R.dimen.padding_medium)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small))
    ) {
        items(tracks) { track ->
            TrackListItem(track = track, onTrackClick = onTrackClick)
            HorizontalDivider(
                color = colorResource(id = R.color.light_gray),
                thickness = dimensionResource(id = R.dimen.divider_height)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SearchScreenPreview() {
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            SearchScreen(
                onBackClick = {},
                onTrackClick = {}
            )
        }
    }
}