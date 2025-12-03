package com.practicum.myapplication.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.practicum.myapplication.ui.main.MainScreen
import com.practicum.myapplication.ui.search.SearchScreen
import com.practicum.myapplication.ui.settings.SettingsScreen
import com.practicum.myapplication.ui.favorites.FavoritesScreen
import com.practicum.myapplication.ui.playlists.PlaylistsScreen
import com.practicum.myapplication.ui.trackdetails.TrackDetailsScreen
import com.practicum.myapplication.ui.playlists.CreatePlaylistScreen
import com.practicum.myapplication.domain.models.Track
import com.practicum.myapplication.ui.playlists.PlaylistDetailViewModel
import com.practicum.myapplication.ui.playlists.PlaylistScreen

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
                    navController.navigate(Screen.PLAYLISTS.name)
                },
                onFavoritesClick = {
                    navController.navigate(Screen.FAVORITES.name)
                }
            )
        }

        // Экран поиска
        composable(Screen.SEARCH.name) {
            SearchScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onTrackClick = { trackId ->
                    navController.navigate("${Screen.TRACK_DETAILS.name}/${trackId}")
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

        // Экран плейлистов
        composable(Screen.PLAYLISTS.name) {
            PlaylistsScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onCreatePlaylistClick = {
                    navController.navigate(Screen.CREATE_PLAYLIST.name)
                },
                onPlaylistClick = { playlistId ->
                    navController.navigate("${Screen.PLAYLIST_SCREEN.name}/$playlistId")
                }
            )
        }

        // Экран плейлиста
        composable(
            route = "${Screen.PLAYLIST_SCREEN.name}/{playlistId}",
            arguments = listOf(
                navArgument("playlistId") {
                    type = androidx.navigation.NavType.LongType
                }
            )
        ) { backStackEntry ->
            val playlistId = backStackEntry.arguments?.getLong("playlistId") ?: 0L

            PlaylistScreen(
                playlistId = playlistId,
                onBackClick = {
                    navController.popBackStack()
                },
                onTrackClick = { trackId ->
                    navController.navigate("${Screen.TRACK_DETAILS.name}/${trackId}")
                },
                viewModelFactory = PlaylistDetailViewModel.getViewModelFactory(playlistId)
            )
        }

        // Экран избранного
        composable(Screen.FAVORITES.name) {
            FavoritesScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onTrackClick = { trackId  ->
                    navController.navigate("${Screen.TRACK_DETAILS.name}/$trackId")
                }
            )
        }

        // Экран деталей трека
        composable(
            route = "${Screen.TRACK_DETAILS.name}/{trackId}",
            arguments = listOf(
                navArgument("trackId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val trackId = backStackEntry.arguments?.getLong("trackId") ?: 0L

            // Получаем трек из репозитория
            TrackDetailsScreen(
                trackId = trackId,
                onBackClick = { navController.popBackStack() }
            )
        }

        // Экран создания плейлиста
        composable(Screen.CREATE_PLAYLIST.name) {
            CreatePlaylistScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}