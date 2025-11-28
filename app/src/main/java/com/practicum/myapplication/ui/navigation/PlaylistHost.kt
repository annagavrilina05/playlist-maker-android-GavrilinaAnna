package com.practicum.myapplication.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
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
                onTrackClick = { track ->
                    // Передаем основные параметры трека через аргументы
                    navController.navigate(
                        "${Screen.TRACK_DETAILS.name}/" +
                                "${track.id}/" +
                                "${track.trackName}/" +
                                "${track.artistName}/" +
                                "${track.trackTime}/" +
                                "${track.favorite}"
                    )
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
                onPlaylistClick = { playlistId ->}
            )
        }

        // Экран избранного
        composable(Screen.FAVORITES.name) {
            FavoritesScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onTrackClick = { track ->
                    navController.navigate(
                        "${Screen.TRACK_DETAILS.name}/" +
                                "${track.id}/" +
                                "${track.trackName}/" +
                                "${track.artistName}/" +
                                "${track.trackTime}/" +
                                "${track.favorite}"
                    )
                }
            )
        }

        // Экран деталей трека
        composable(
            route = "${Screen.TRACK_DETAILS.name}/{id}/{trackName}/{artistName}/{trackTime}/{favorite}",
            arguments = listOf(
                navArgument("id") { type = androidx.navigation.NavType.LongType },
                navArgument("trackName") { type = androidx.navigation.NavType.StringType },
                navArgument("artistName") { type = androidx.navigation.NavType.StringType },
                navArgument("trackTime") { type = androidx.navigation.NavType.StringType },
                navArgument("favorite") { type = androidx.navigation.NavType.BoolType }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("id") ?: 0L
            val trackName = backStackEntry.arguments?.getString("trackName") ?: ""
            val artistName = backStackEntry.arguments?.getString("artistName") ?: ""
            val trackTime = backStackEntry.arguments?.getString("trackTime") ?: ""
            val favorite = backStackEntry.arguments?.getBoolean("favorite") ?: false

            val track = Track(
                id = id,
                trackName = trackName,
                artistName = artistName,
                trackTime = trackTime,
                image = "", // временно пустая строка
                favorite = favorite,
                playlistId = emptyList()
            )

            TrackDetailsScreen(
                track = track,
                onBackClick = {
                    navController.popBackStack()
                }
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