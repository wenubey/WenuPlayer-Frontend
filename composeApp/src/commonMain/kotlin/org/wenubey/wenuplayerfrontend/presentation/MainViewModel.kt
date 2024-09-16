package org.wenubey.wenuplayerfrontend.presentation

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import kotlinx.coroutines.launch
import org.wenubey.wenuplayerfrontend.data.dto.VideoMetadata
import org.wenubey.wenuplayerfrontend.data.dto.VideoSummary
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

    var uploadState = mutableStateOf("")
    var summariesState = mutableStateOf<List<VideoSummary>>(emptyList())
    var currentVideo = mutableStateOf<Pair<VideoMetadata?, File?>>(Pair(null, null))

    fun uploadVideo(path: String) {
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

    fun getVideoSummaries() {
        viewModelScope.launch(ioDispatcher) {
            val result = apiService.getVideoSummaries()
            if (result.isSuccess) {
                summariesState.value = result.getOrNull()!!
            } else {
                logger.e { "Get Summaries failed with exception: ${result.exceptionOrNull()}" }
            }
        }
    }

    fun getVideoById(id: String) {
        viewModelScope.launch(ioDispatcher) {
            val result = apiService.getVideoById(id)
            if (result.isSuccess) {
                currentVideo.value = result.getOrNull()!!
            } else {
                logger.e { "Get video by id failed with exception: ${result.exceptionOrNull()}" }
            }
        }
    }

}