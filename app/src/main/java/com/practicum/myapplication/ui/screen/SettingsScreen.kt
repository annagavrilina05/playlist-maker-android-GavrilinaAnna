package com.practicum.myapplication.ui.screen

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.scale
import androidx.core.net.toUri
import com.practicum.myapplication.R


@Composable
fun SettingsScreen(
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    var isDarkTheme by remember { mutableStateOf(false) }

    // Получаем строки заранее, чтобы использовать в Intent
    val shareText = stringResource(R.string.share_app)
    val supportEmail = stringResource(R.string.support_email)
    val emailSubject = stringResource(R.string.email_subject)
    val emailBody = stringResource(R.string.email_body)
    val termsUrl = stringResource(R.string.terms_url)

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
                text = stringResource(R.string.settings_title),
                style = TextStyle(
                    fontSize = dimensionResource(R.dimen.text_size_large).value.sp,
                    fontWeight = FontWeight.Medium
                ),
                color = colorResource(id = R.color.black)
            )
        }

        // Список настроек на белом фоне
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = dimensionResource(id = R.dimen.padding_large),
                    start = dimensionResource(id = R.dimen.padding_large),
                    end = dimensionResource(id = R.dimen.padding_large)
                )
        ) {
            // Темная тема
            SettingsItem(
                text = stringResource(R.string.dark_theme),
                onClick = {},
                trailingContent = {
                    Switch(
                        checked = isDarkTheme,
                        onCheckedChange = { isDarkTheme = it },
                        modifier = Modifier
                            .size(48.dp) // Увеличиваем область клика
                            .scale(0.8f), // Увеличиваем визуальный размер
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = colorResource(id = R.color.blue),
                            checkedTrackColor = colorResource(id = R.color.blue).copy(alpha = 0.5f)
                        )
                    )
                }
            )

            // Поделиться приложением
            SettingsItem(
                text = stringResource(R.string.share_app),
                onClick = {
                    val shareIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, shareText)
                        type = "text/plain"
                    }
                    context.startActivity(Intent.createChooser(shareIntent, null))
                },
                trailingContent = {
                    Icon(
                        Icons.Outlined.Share,
                        contentDescription = null,
                        tint = colorResource(id = R.color.gray),
                        modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size))
                    )
                }
            )

            // Написать в поддержку
            SettingsItem(
                text = stringResource(R.string.contact_support),
                onClick = {
                    val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                        data = "mailto:$supportEmail".toUri()
                        putExtra(Intent.EXTRA_SUBJECT, emailSubject)
                        putExtra(Intent.EXTRA_TEXT, emailBody)
                    }
                    context.startActivity(Intent.createChooser(emailIntent, null))
                },
                trailingContent = {
                    Icon(
                        Icons.Outlined.SupportAgent,
                        contentDescription = null,
                        tint = colorResource(id = R.color.gray),
                        modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size))
                    )
                }
            )

            // Пользовательское соглашение
            SettingsItem(
                text = stringResource(R.string.user_agreement),
                onClick = {
                    val termsIntent = Intent(Intent.ACTION_VIEW).apply {
                        data = termsUrl.toUri()
                    }
                    context.startActivity(termsIntent)
                },
                trailingContent = {
                    Icon(
                        Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                        contentDescription = null,
                        tint = colorResource(id = R.color.gray),
                        modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size))
                    )
                }
            )
        }
    }
}

@Composable
fun SettingsItem(
    text: String,
    onClick: () -> Unit,
    trailingContent: @Composable () -> Unit
) {
    val listItemModifier = Modifier
        .fillMaxWidth()
        .height(64.dp)
        .clickable(onClick = onClick)
        .padding(vertical = dimensionResource(id = R.dimen.padding_medium))

    Row(
        modifier = listItemModifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontSize = dimensionResource(R.dimen.text_size_medium).value.sp,
                fontWeight = FontWeight.Medium
            ),
            color = colorResource(id = R.color.black)
        )

        // Иконка/элемент справа
        trailingContent()
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            SettingsScreen(onBackClick = {})
        }
    }
}