package org.wenubey.wenuplayerfrontend.presentation

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import kotlinx.coroutines.launch
import org.wenubey.wenuplayerfrontend.data.dto.VideoMetadata
import org.wenubey.wenuplayerfrontend.domain.repository.ApiService
import org.wenubey.wenuplayerfrontend.domain.repository.DispatcherProvider
import java.io.File
import java.util.UUID

// TODO: Change this viewmodel with the necessary functionalities
class MainViewModel(
    private val apiService: ApiService,
    private val dispatcherProvider: DispatcherProvider,
) : ViewModel() {
    private val logger = Logger.withTag("MainViewModel")

    var uploadState = mutableStateOf("")

    fun uploadVideo(path: String) {
        viewModelScope.launch(dispatcherProvider.io()) {
            val video = VideoMetadata(
                id = UUID.randomUUID().toString(),
                title = "Video Title",
                url = "video url",
                lastWatched = 0L,
            )
            val videoFile = File(path)
            if (!videoFile.exists()) {
                logger.e { "Video file not found at path: $path" }
                viewModelScope.launch(dispatcherProvider.main()) {
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

}