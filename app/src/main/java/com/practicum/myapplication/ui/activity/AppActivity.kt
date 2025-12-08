package com.practicum.myapplication.ui.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.practicum.myapplication.ui.navigation.PlaylistHost
import com.practicum.myapplication.creator.Creator
import com.practicum.myapplication.ui.theme.PlaylistMakerTheme
import com.practicum.myapplication.ui.theme.ThemeViewModel

class AppActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Creator.init(this)
        setContent {
            val themeViewModel: ThemeViewModel = viewModel(
                factory = Creator.getThemeViewModelFactory()
            )
            val isDarkTheme = themeViewModel.isDarkTheme.collectAsState().value

            PlaylistMakerTheme(darkTheme = isDarkTheme) {
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