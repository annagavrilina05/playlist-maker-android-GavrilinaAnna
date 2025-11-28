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
import kotlinx.coroutines.delay
import androidx.compose.ui.unit.times

@Composable
fun SearchScreen(
    onBackClick: () -> Unit,
    onTrackClick: (Track) -> Unit = {},
    viewModel: SearchViewModel = viewModel(factory = SearchViewModel.getViewModelFactory())
) {
    var searchQuery by remember { mutableStateOf("") }
    val screenState by viewModel.searchScreenState.collectAsState()
    val searchHistory by viewModel.searchHistory.collectAsState()
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
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = colorResource(id = R.color.blue)
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
                            .fillMaxSize()
                            .padding(dimensionResource(id = R.dimen.padding_large)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.not_found),
                            color = colorResource(id = R.color.gray),
                            fontSize = dimensionResource(id = R.dimen.text_size_medium).value.sp
                        )
                    }
                }
            }
            is SearchState.Fail -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(dimensionResource(id = R.dimen.padding_large)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.search_error),
                        color = colorResource(id = R.color.red),
                        fontSize = dimensionResource(id = R.dimen.text_size_medium).value.sp
                    )
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
                    onClick = {},
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
                                onClick = onClearClick
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
    onTrackClick: (Track) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {onTrackClick(track)}
            .padding(dimensionResource(id = R.dimen.padding_medium)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_music),
            contentDescription = "Трек ${track.trackName}",
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

@Composable
fun SearchResultsList(
    tracks: List<Track>,
    onTrackClick: (Track) -> Unit
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