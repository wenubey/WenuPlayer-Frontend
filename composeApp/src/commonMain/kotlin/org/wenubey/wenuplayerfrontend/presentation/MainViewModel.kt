package org.wenubey.wenuplayerfrontend.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.asLongState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.wenubey.wenuplayerfrontend.data.dto.VideoMetadata
import org.wenubey.wenuplayerfrontend.data.dto.VideoSummary
import org.wenubey.wenuplayerfrontend.domain.model.VideoModel
import org.wenubey.wenuplayerfrontend.domain.repository.ApiService
import org.wenubey.wenuplayerfrontend.domain.repository.DispatcherProvider
import java.io.File
import java.util.UUID

// TODO: Change this viewmodel with the necessary functionalities
// TODO: Change this viewmodel with necessary states and events.
class MainViewModel(
    private val apiService: ApiService,
    private val dispatcherProvider: DispatcherProvider,
) : ViewModel() {
    private val logger = Logger.withTag("MainViewModel")
    private val ioDispatcher = dispatcherProvider.io()
    private val mainDispatcher = dispatcherProvider.main()

    // TODO change this states with Stateflow and create state data class to handle more clean
    var uploadState = mutableStateOf("")
    var summariesState = mutableStateOf<List<VideoSummary>>(emptyList())
    var currentVideo = mutableStateOf(VideoModel.default())

    private var _currentTimeMillis = mutableStateOf(currentVideo.value.metadata.lastWatched)
    val currentTimeMillis: State<Long> = _currentTimeMillis.asLongState()



    private var videoJob: Job? = null


    fun onPlayEvent(playEvent: VideoPlayEvent) {
        when (playEvent) {
            VideoPlayEvent.Backward -> {
                _currentTimeMillis.value -= 5 * 1000L
            }
            VideoPlayEvent.Forward -> {
                _currentTimeMillis.value += 5 * 1000L
            }
            VideoPlayEvent.Pause -> {
                videoJob?.cancel()
                val videoId = currentVideo.value.metadata.id
                updateLastWatch(videoId)
            }
            VideoPlayEvent.Play -> {
                videoJob = viewModelScope.launch(ioDispatcher) {
                    while (isActive) {
                        _currentTimeMillis.value += 1000L
                        delay(1000L)
                    }
                }
            }
        }
    }

    fun onEvent(event: VideoEvent) {
        when(event) {
            is VideoEvent.VideoChanged -> {
                videoJob?.cancel()
                _currentTimeMillis.value = 0L
            }
            is VideoEvent.UploadVideo -> {
                uploadVideo(event.path)
            }
            is VideoEvent.GetVideoSummaries -> {
                getVideoSummaries()
            }
            is VideoEvent.GetVideoById -> {
                getVideoById(event.id)
            }
        }
    }

    // TODO change example video metadata to find video for given path and upload that metadata into backend
    private fun uploadVideo(path: String) {
        viewModelScope.launch(ioDispatcher) {
            val video = VideoMetadata(
                id = UUID.randomUUID().toString(),
                title = "Video Title",
                url = "video url",
                lastWatched = 0L,
            )
            val videoFile = File(path)
            if (!videoFile.exists()) {
                logger.e { "Video file not found at path: $path" }
                viewModelScope.launch(mainDispatcher) {
                    uploadState.value = "File not found"
                }
                return@launch
            }
            val result = apiService.uploadVideo(video, videoFile)
            if (result.isSuccess) {
                uploadState.value = "Video uploaded successfully."
            } else {
                uploadState.value = "Video upload failed: ${result.exceptionOrNull()?.message}"
                logger.e { "Upload failed with exception: ${result.exceptionOrNull()}" }
            }

        }
    }

    private fun getVideoSummaries() {
        viewModelScope.launch(ioDispatcher) {
            val result = apiService.getVideoSummaries()
            if (result.isSuccess) {
                summariesState.value = result.getOrNull()!!
            } else {
                logger.e { "Get Summaries failed with exception: ${result.exceptionOrNull()}" }
            }
        }
    }

    private fun getVideoById(id: String) {
        viewModelScope.launch(ioDispatcher) {
            val result = apiService.getVideoById(id)
            if (result.isSuccess) {
                currentVideo.value = result.getOrNull()!!
            } else {
                logger.e { "Get video by id failed with exception: ${result.exceptionOrNull()}" }
            }
        }
    }

    private fun updateLastWatch(id: String){
        viewModelScope.launch(ioDispatcher) {
            val result = apiService.updateLastWatched(id = id, lastMillis = currentTimeMillis.value)
            if(result.isSuccess) {
                logger.i { "Video last watched updated." }
            } else {
                logger.e { "Video last watched updated failed: ${result.exceptionOrNull()}" }
            }
        }
    }
}

sealed interface VideoEvent {
    data object VideoChanged: VideoEvent
    data class UploadVideo(val path: String): VideoEvent
    data object GetVideoSummaries: VideoEvent
    data class GetVideoById(val id: String): VideoEvent
}

sealed interface VideoPlayEvent {
    data object Play: VideoPlayEvent
    data object Pause: VideoPlayEvent
    data object Forward: VideoPlayEvent
    data object Backward: VideoPlayEvent
}