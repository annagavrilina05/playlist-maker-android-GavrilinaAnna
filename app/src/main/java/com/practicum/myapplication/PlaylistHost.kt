package com.practicum.myapplication

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun PlaylistHost(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Screen.MAIN.name
    ) {
        // Главный экран
        composable(Screen.MAIN.name) {
            MainScreen(
                onSearchClick = {
                    navController.navigate(Screen.SEARCH.name)
                },
                onSettingsClick = {
                    navController.navigate(Screen.SETTINGS.name)
                },
                onPlaylistsClick = {
                    // Оставляем Toast как было
                    // В будущем можно добавить навигацию на экран плейлистов
                },
                onFavoritesClick = {
                    // Оставляем Toast как было
                    // В будущем можно добавить навигацию на экран избранного
                }
            )
        }

        // Экран поиска
        composable(Screen.SEARCH.name) {
            SearchScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        // Экран настроек
        composable(Screen.SETTINGS.name) {
            SettingsScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}