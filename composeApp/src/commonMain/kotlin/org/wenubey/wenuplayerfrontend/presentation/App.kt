package org.wenubey.wenuplayerfrontend.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import co.touchlab.kermit.Logger
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import org.wenubey.wenuplayerfrontend.presentation.components.VideoPlayer
import org.wenubey.wenuplayerfrontend.presentation.viewmodel.MainViewModel

@OptIn(KoinExperimentalAPI::class)
@Composable
@Preview
fun App() {
    // TODO: delete later
    val mainViewModel = koinViewModel<MainViewModel>()

    var filePath by remember { mutableStateOf("") }
    val videoState by mainViewModel.videoState.collectAsState()
    val videoSummaries = videoState.summaries
    val currentTime = videoState.currentTime
    val logger = Logger.withTag("App")




    MaterialTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            backgroundColor = MaterialTheme.colors.background
        ) { paddingValues ->

            Column(
                Modifier.fillMaxWidth().padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                VideoPlayer(modifier = Modifier.weight(0.5f), mainViewModel = mainViewModel)
            }
        }
    }
}