package org.wenubey.wenuplayerfrontend.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.wenubey.wenuplayerfrontend.data.dto.VideoMetadata
import org.wenubey.wenuplayerfrontend.data.dto.VideoSummary
import org.wenubey.wenuplayerfrontend.domain.model.VideoModel
import org.wenubey.wenuplayerfrontend.domain.repository.ApiService
import org.wenubey.wenuplayerfrontend.domain.repository.DispatcherProvider
import org.wenubey.wenuplayerfrontend.domain.repository.VideoRepository
import java.io.File
import java.util.UUID

// TODO: Change this viewmodel with the necessary functionalities
// TODO: Change this viewmodel with necessary states and events.
class MainViewModel(
    private val apiService: ApiService,
    private val dispatcherProvider: DispatcherProvider,
    private val videoRepository: VideoRepository,
) : ViewModel() {
    private val logger = Logger.withTag("MainViewModel")
    private val ioDispatcher = dispatcherProvider.io()
    private val mainDispatcher = dispatcherProvider.main()

    private val _videoState = MutableStateFlow(VideoState())
    val videoState: StateFlow<VideoState> = _videoState.asStateFlow()

    private var videoJob: Job? = null


    fun onPlayEvent(playEvent: VideoPlayEvent) {
        when (playEvent) {
            VideoPlayEvent.Backward -> {
                viewModelScope.launch(mainDispatcher) {
                    _videoState.update { oldState ->
                        val newTime = oldState.currentTimeMillis - 5 * 1000L
                        oldState.copy(
                            currentTimeMillis = newTime
                        )
                    }
                }
            }

            VideoPlayEvent.Forward -> {
                viewModelScope.launch(mainDispatcher) {
                    _videoState.update { oldState ->
                        val newTime = oldState.currentTimeMillis + 5 * 1000L
                        oldState.copy(
                            currentTimeMillis = newTime
                        )
                    }
                }
            }

            VideoPlayEvent.Pause -> {
                viewModelScope.launch(mainDispatcher) {
                    videoJob?.cancel()
                    val videoId = _videoState.value.videoModel.metadata.id
                    updateLastWatch(videoId)
                }
            }

            VideoPlayEvent.Play -> {
                videoJob = viewModelScope.launch(ioDispatcher) {
                    while (isActive) {
                        viewModelScope.launch(mainDispatcher) {
                            _videoState.update { oldState ->
                                val newTime = oldState.currentTimeMillis + 1 * 1000L
                                oldState.copy(
                                    currentTimeMillis = newTime
                                )
                            }
                        }
                        delay(1000L)
                    }
                }
            }
        }
    }

    fun onEvent(event: VideoEvent) {
        when (event) {

            is VideoEvent.UploadVideo -> {
                uploadVideo(event.path)
            }

            is VideoEvent.GetVideoSummaries -> {
                getVideoSummaries()
            }

            is VideoEvent.GetVideoById -> {
                getVideoById(event.id)
            }

            is VideoEvent.DeleteVideoById -> {
                deleteVideoById(event.id)
            }

            is VideoEvent.RestoreVideoById -> {
                restoreVideoById(event.id)
            }
        }
    }

    // TODO change example video metadata to find video for given path and upload that metadata into backend
    // TODO File exist should check on api service(in future repo) and move there.
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
                updateScreenInfo(Result.success("File not found"))
                return@launch
            }
            val result = apiService.uploadVideo(video, videoFile)
            if (result.isSuccess) {
                updateScreenInfo(Result.success("Video uploaded successfully."))
            } else {
                updateScreenInfo(Result.success("Video upload failed: ${result.exceptionOrNull()?.message}"))
                logger.e { "Upload failed with exception: ${result.exceptionOrNull()}" }
            }

        }
    }

    private fun getVideoSummaries() {
        viewModelScope.launch(ioDispatcher) {
            val result = videoRepository.getVideoSummaries()
            if (result.isSuccess) {
                _videoState.update { oldState ->
                    oldState.copy(
                        summaries = result.getOrNull()!!,
                        screenInfo = ""
                    )
                }
            } else {
                _videoState.update { oldState ->
                    oldState.copy(
                        screenInfo = result.exceptionOrNull()!!.message.toString()
                    )
                }
                logger.e { "Get Summaries failed with exception: ${result.exceptionOrNull()}" }
            }
        }
    }

    private fun getVideoById(id: String) {
        viewModelScope.launch(ioDispatcher) {
            val result = apiService.getVideoById(id)
            viewModelScope.launch(mainDispatcher) {
                if (result.isSuccess) {
                    _videoState.update { oldState ->
                        oldState.copy(
                            videoModel = result.getOrNull()!!,
                            currentTimeMillis = result.getOrNull()!!.metadata.lastWatched,
                        )
                    }
                    logger.i { "Get video success: ${result.getOrNull()!!}" }
                } else {
                    _videoState.update { oldState ->
                        oldState.copy(
                            screenInfo = result.exceptionOrNull()!!.message.toString()
                        )
                    }
                    logger.e { "Get video by id failed with exception: ${result.exceptionOrNull()}" }
                }
            }
        }
    }

    private fun updateLastWatch(id: String) {
        viewModelScope.launch(ioDispatcher) {
            val lastMillis = _videoState.value.currentTimeMillis
            val result = apiService.updateLastWatched(id = id, lastMillis = lastMillis)
            updateScreenInfo(result)
        }
    }

    private fun deleteVideoById(id: String) {
        viewModelScope.launch(ioDispatcher) {
            val response = apiService.deleteVideoById(id)
            updateScreenInfo(response)
        }
    }

    private fun restoreVideoById(id: String) {
        viewModelScope.launch(ioDispatcher) {
            val response = apiService.restoreVideoById(id)
            updateScreenInfo(response)
        }
    }

    private fun updateScreenInfo(result: Result<String>) {
        viewModelScope.launch(mainDispatcher) {
            _videoState.update { oldState ->
                oldState.copy(
                    screenInfo = result.getOrNull()!!
                )
            }
        }
    }
}

sealed interface VideoEvent {
    // TODO add nextVideo PreviousVideo Events
    data class UploadVideo(val path: String) : VideoEvent
    data object GetVideoSummaries : VideoEvent
    data class GetVideoById(val id: String) : VideoEvent
    data class DeleteVideoById(val id: String) : VideoEvent
    data class RestoreVideoById(val id: String) : VideoEvent
}

sealed interface VideoPlayEvent {
    data object Play : VideoPlayEvent
    data object Pause : VideoPlayEvent
    data object Forward : VideoPlayEvent
    data object Backward : VideoPlayEvent
}

// TODO create new states like current video queue change this video State
data class VideoState(
    val videoModel: VideoModel = VideoModel.default(),
    var currentTimeMillis: Long = 0L,
    val currentQueue: List<VideoModel> = listOf(),
    val screenInfo: String = "",
    val summaries: List<VideoSummary> = listOf(),
)


