package org.wenubey.wenuplayerfrontend.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.touchlab.kermit.Logger
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import org.wenubey.wenuplayerfrontend.presentation.viewmodel.DesktopVideoPlayerViewModel
import org.wenubey.wenuplayerfrontend.presentation.viewmodel.MainViewModel
import org.wenubey.wenuplayerfrontend.presentation.viewmodel.VideoPlayerEvent

@OptIn(KoinExperimentalAPI::class)
@Composable
actual fun VideoPlayer(modifier: Modifier, mainViewModel: MainViewModel) {
    val videoViewModel = koinViewModel<DesktopVideoPlayerViewModel>()
    val state = mainViewModel.videoState.collectAsStateWithLifecycle()
    val factory = videoViewModel.factory
    val videoState = videoViewModel.videoState.collectAsStateWithLifecycle().value
    val subtitleState = videoViewModel.subtitleState.collectAsStateWithLifecycle().value
    val mediaPlayer = videoViewModel.mediaPlayer
    val isPlaying = videoState.isPlaying
    val currentTime = videoState.currentTime

    val logger = Logger.withTag("TAG")
    DisposableEffect(Unit) { onDispose(mediaPlayer::release) }

    Column(
        modifier = modifier.fillMaxSize(),
    ) {
        SwingPanel(
            modifier = Modifier.fillMaxSize().weight(0.8f),
            background = Color.Transparent,
            factory = factory,
            update = {

            }
        )
        Row(
            modifier = Modifier.fillMaxWidth().weight(0.1f),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(modifier = Modifier.weight(0.1f), text = currentTime)
            LazyColumn(modifier = Modifier.weight(0.3f)) {
                items(videoState.summaries) { summary ->
                    Text(modifier = Modifier.clickable {
                        videoViewModel.onEvent(
                            VideoPlayerEvent.SelectVideo(
                                id = summary.id ?: "",
                                name = summary.title
                            )
                        )
                    }, text = summary.title)
                }
            }
            Button(
                modifier = Modifier.weight(0.1f),
                onClick = { videoViewModel.onEvent(VideoPlayerEvent.FetchSubtitles) },
                content = { Text("Fetch Subtitles") })
            LazyColumn(modifier = Modifier.weight(0.3f)) {
                items(subtitleState.subtitles) { subtitle ->
                    Text(modifier = Modifier.clickable {
                        videoViewModel.onEvent(VideoPlayerEvent.SelectSubtitle(subtitleIndex = subtitle.id()))
                    }, text = subtitle.description())
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth().weight(0.1f),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Button(
                onClick = {
                    videoViewModel.onEvent(VideoPlayerEvent.SeekBackward)
                },
                content = {
                    Icon(imageVector = Icons.Filled.FastRewind, contentDescription = null)
                }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    // TODO add new event
                    if (isPlaying) {
                        videoViewModel.onEvent(VideoPlayerEvent.Pause)
                    } else {
                        videoViewModel.onEvent(VideoPlayerEvent.Play)
                    }
                },
                content = {
                    Icon(
                        imageVector = if (isPlaying) Icons.Outlined.Pause else Icons.Outlined.PlayArrow,
                        contentDescription = null,
                    )
                }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    // TODO add new event
                    videoViewModel.onEvent(VideoPlayerEvent.SeekForward)
                },
                content = {
                    Icon(imageVector = Icons.Filled.FastForward, contentDescription = null)
                }
            )
        }
    }
}


