package com.practicum.myapplication.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.practicum.myapplication.ui.main.MainScreen
import com.practicum.myapplication.ui.search.SearchScreen
import com.practicum.myapplication.ui.settings.SettingsScreen

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
                onPlaylistsClick = {},
                onFavoritesClick = {}
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