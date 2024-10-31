package org.wenubey.wenuplayerfrontend.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.wenubey.wenuplayerfrontend.domain.repository.CommonRepository
import org.wenubey.wenuplayerfrontend.domain.repository.DispatcherProvider
import org.wenubey.wenuplayerfrontend.domain.repository.VideoRepository
import org.wenubey.wenuplayerfrontend.util.formatDuration

// TODO: Change this viewmodel with the necessary functionalities
// TODO: Change this viewmodel with necessary states and events.
class MainViewModel(
    dispatcherProvider: DispatcherProvider,
    private val videoRepository: VideoRepository,
    private val commonRepository: CommonRepository,
) : ViewModel() {
    private val logger = Logger.withTag("MainViewModel")
    private val ioDispatcher = dispatcherProvider.io()
    private val mainDispatcher = dispatcherProvider.main()

    private val _videoState = MutableStateFlow(VideoState())
    val videoState: StateFlow<VideoState> = _videoState.asStateFlow()

    private var videoJob: Job? = null


    fun onEvent(event: VideoEvent) {
        when (event) {

            is VideoEvent.UploadVideo -> {
                uploadVideo(event.path)
            }

            is VideoEvent.GetVideoSummaries -> {
                getVideoSummaries()
            }

            is VideoEvent.GetVideoById -> {
                getVideoById(event.id, event.name)
            }

            is VideoEvent.DeleteVideoById -> {
                deleteVideoById(event.id)
            }

            is VideoEvent.RestoreVideoById -> {
                restoreVideoById(event.id)
            }
        }
    }

    private fun uploadVideo(path: String) {
        viewModelScope.launch(ioDispatcher) {
            val result = videoRepository.uploadVideo(path)
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
                    commonRepository.showToast("Video summaries fetched successfully.")
                    oldState.copy(
                        summaries = result.getOrNull()!!,
                    )
                }
            } else {
                commonRepository.showToast(result.exceptionOrNull()!!.message.toString())
                logger.e { "Get Summaries failed with exception: ${result.exceptionOrNull()}" }
            }
        }
    }

    private fun getVideoById(id: String, name: String) {
        viewModelScope.launch(ioDispatcher) {
            val result = videoRepository.getVideoById(id, name)
            viewModelScope.launch(mainDispatcher) {
                if (result.isSuccess) {
                    commonRepository.showToast("Video fetched successfully.")
                    _videoState.update { oldState ->
                        val lastWatchedTimeMillis = result.getOrNull()!!.metadata.lastWatched
                        oldState.copy(
                            videoModel = result.getOrNull()!!,
                            currentTime = formatDuration(lastWatchedTimeMillis),
                        )
                    }
                    logger.i { "Get video success: ${result.getOrNull()!!}" }
                } else {
                    commonRepository.showToast(result.exceptionOrNull()!!.message.toString())
                    logger.e { "Get video by id failed with exception: ${result.exceptionOrNull()}" }
                }
            }
        }
    }


    private fun deleteVideoById(id: String) {
        viewModelScope.launch(ioDispatcher) {
            val response = videoRepository.deleteVideoById(id)
            updateScreenInfo(response)
        }
    }

    private fun restoreVideoById(id: String) {
        viewModelScope.launch(ioDispatcher) {
            val response = videoRepository.restoreVideoById(id)
            updateScreenInfo(response)
        }
    }

    private fun updateScreenInfo(result: Result<String>) {
        viewModelScope.launch(mainDispatcher) {
            if (result.isSuccess) {
                commonRepository.showToast(result.getOrNull()!!)
                logger.i { "Update screen info success: ${result.getOrNull()}" }
            }
            if (result.isFailure) {
                commonRepository.showToast(result.exceptionOrNull()!!.message.toString())
                logger.e { "Update screen info failed with exception: ${result.exceptionOrNull()}" }
            }
        }
    }
}

sealed interface VideoEvent {
    data class UploadVideo(val path: String) : VideoEvent
    data object GetVideoSummaries : VideoEvent
    data class GetVideoById(val id: String, val name: String) : VideoEvent
    data class DeleteVideoById(val id: String) : VideoEvent
    data class RestoreVideoById(val id: String) : VideoEvent
}