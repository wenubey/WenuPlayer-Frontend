package org.wenubey.wenuplayerfrontend.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import co.touchlab.kermit.Logger
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(KoinExperimentalAPI::class)
@Composable
@Preview
fun App() {
    // TODO: delete later
    val mainViewModel = koinViewModel<MainViewModel>()
    val uploadState by mainViewModel.uploadState
    val videoSummaries by mainViewModel.summariesState
    var filePath by remember { mutableStateOf("") }
    var currentVideo by mainViewModel.currentVideo
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
                OutlinedTextField(
                    value = filePath,
                    onValueChange = { filePath = it },
                    label = { Text("Enter video file path") },
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                )

                // Button to trigger the upload
                Button(onClick = { mainViewModel.uploadVideo(filePath) }) {
                    Text("Upload Video")
                }

                // Display the upload status
                Text(
                    text = uploadState,
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier.padding(top = 16.dp)
                )
                Button(onClick = {
                    mainViewModel.getVideoSummaries()
                }) {
                    Text("Get All Video Summaries")
                }
                LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                        .padding(16.dp)
                ) {
                    items(videoSummaries) { videoSummary ->
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Column {
                                Text(videoSummary.id)
                                Text(videoSummary.title)
                            }
                        }
                    }
                }

                Button(onClick = {
                    mainViewModel.getVideoById("1dd94d84-2b50-4264-bcfb-3b0ca8ae814c")
                }) {
                    Text("Get Video By ID")
                }

                Text(currentVideo.metadata.title)
                Text("Size: ${currentVideo.videoFile.length()}")
            }
        }

    }
}