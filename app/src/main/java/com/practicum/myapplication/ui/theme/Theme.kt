package com.practicum.myapplication.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = blue,                // Основной цвет (синий)
    onPrimary = white,             // Текст/иконки на основном цвете

    background = dark_background,  // Фон приложения
    onBackground = white,          // Текст на фоне

    surfaceVariant = dark_gray,    // Вариант поверхности
    onSurfaceVariant = white,      // Текст на варианте поверхности

    primaryContainer = blue.copy(alpha = 0.2f)
)

private val LightColorScheme = lightColorScheme(
    primary = blue,                // Основной цвет (синий)
    onPrimary = white,             // Текст/иконки на основном цвете

    background = white,            // Фон приложения
    onBackground = black,          // Текст на фоне

    surfaceVariant = light_gray,   // Вариант поверхности
    onSurfaceVariant = black,      // Текст на варианте поверхности

    primaryContainer = blue.copy(alpha = 0.1f)
)

@Composable
fun PlaylistMakerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}