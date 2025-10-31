package com.practicum.myapplication

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

@Composable
fun SearchScreen(
    onBackClick: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    // Пустая история поиска
    /*val searchHistory = remember {
        listOf<SearchHistoryItem>()
    }*/

    // Заглушка для истории поиска
    val searchHistory = remember {
        listOf(
            SearchHistoryItem(1, "Rock music"),
            SearchHistoryItem(2, "Jazz classics"),
            SearchHistoryItem(3, "Pop hits")
        )
    }

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

        // Поисковая строка с возможностью расширения
        SearchFieldWithHistory(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            onClearClick = { searchQuery = "" },
            searchHistory = searchHistory,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = dimensionResource(id = R.dimen.padding_large),
                    vertical = dimensionResource(id = R.dimen.search_top_padding)
                )
        )
    }
}

@Composable
fun SearchFieldWithHistory(
    query: String,
    onQueryChange: (String) -> Unit,
    onClearClick: () -> Unit,
    searchHistory: List<SearchHistoryItem>,
    modifier: Modifier = Modifier
) {
    val hasHistory = searchHistory.isNotEmpty()
    val fieldHeight = if (hasHistory) {
        dimensionResource(id = R.dimen.search_field_expanded_height)
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
            // Верхняя часть - поле ввода с TextField
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dimensionResource(id = R.dimen.padding_medium))
                    .height(dimensionResource(id = R.dimen.search_field_height)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Строка поиска
                TextField(
                    value = query,
                    onValueChange = onQueryChange,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
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
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = null,
                            tint = colorResource(id = R.color.gray),
                            modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size))
                        )
                    },
                    trailingIcon = {
                        if (query.isNotEmpty()) {
                            IconButton(
                                onClick = onClearClick,
                                modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size))
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

            // История поиска (показывается только если есть история)
            if (hasHistory) {
                // Разделительная линия
                HorizontalDivider(
                    color = colorResource(id = R.color.gray),
                    thickness = dimensionResource(id = R.dimen.divider_height),
                    modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.padding_medium))
                )

                // Список истории
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = dimensionResource(id = R.dimen.padding_medium))
                ) {
                    items(searchHistory) { historyItem ->
                        SearchHistoryItem(
                            item = historyItem,
                            onClick = { /* Позже добавим логику */ }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SearchHistoryItem(
    item: SearchHistoryItem,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(dimensionResource(id = R.dimen.history_item_height))
            .clickable(onClick = onClick),
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
            text = item.query,
            color = colorResource(id = R.color.black),
            fontSize = dimensionResource(id = R.dimen.text_size_medium).value.sp
        )
    }
}

data class SearchHistoryItem(
    val id: Int,
    val query: String
)

@Preview(showBackground = true)
@Composable
fun SearchScreenPreview() {
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            SearchScreen(onBackClick = {})
        }
    }
}