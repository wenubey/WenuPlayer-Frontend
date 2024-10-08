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
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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

    var filePath by remember { mutableStateOf("") }
    val videoState by mainViewModel.videoState.collectAsState()
    val videoSummaries = videoState.summaries
    val currentTime = videoState.currentTimeMillis
    val screenInfo = videoState.screenInfo
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
                Button(onClick = { mainViewModel.onEvent(VideoEvent.UploadVideo(filePath)) }) {
                    Text("Upload Video")
                }

                Button(onClick = {
                    mainViewModel.onEvent(VideoEvent.GetVideoSummaries)
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
                                Text(videoSummary.id ?: "Unknown")
                                Text(videoSummary.title)
                            }
                        }
                    }
                }

                Button(onClick = {
                    mainViewModel.onEvent(VideoEvent.GetVideoById("ec0268e7-4923-475a-a559-40267eb7722g", "BigBuckBunny.mp4"))
                }) {
                    Text("Get Video By ID")
                }

                Text(videoState.videoModel.metadata.title)
                Text("Size: ${videoState.videoModel.videoFile.length()}")

                Row {
                    Button(
                        onClick =  {
                            mainViewModel.onPlayEvent(VideoPlayEvent.Play)
                        }
                    ) {
                        Icon(imageVector = Icons.Filled.PlayCircle, contentDescription = null)
                    }
                    Button(
                        onClick =  {
                            mainViewModel.onPlayEvent(VideoPlayEvent.Pause)
                        }
                    ) {
                        Icon(imageVector = Icons.Filled.PauseCircle, contentDescription = null)
                    }

                    Button(
                        onClick =  {
                            mainViewModel.onPlayEvent(VideoPlayEvent.Forward)
                        }
                    ) {
                        Icon(imageVector = Icons.Filled.FastForward, contentDescription = null)
                    }

                    Button(
                        onClick =  {
                            mainViewModel.onPlayEvent(VideoPlayEvent.Backward)
                        }
                    ) {
                        Icon(imageVector = Icons.Filled.FastRewind, contentDescription = null)
                    }
                }

                Text("Current Time: $currentTime")


                Button(
                    onClick = {
                        mainViewModel.onEvent(VideoEvent.DeleteVideoById(videoState.videoModel.metadata.id))
                    },
                ) {
                    Text("Delete Current Video")
                }

                Button(
                    onClick = {
                        mainViewModel.onEvent(VideoEvent.RestoreVideoById(videoState.videoModel.metadata.id))
                    }
                ) {
                    Text("Restore Current Video")
                }

                Text(screenInfo)
            }
        }

    }
}