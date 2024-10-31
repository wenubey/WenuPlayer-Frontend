package org.wenubey.wenuplayerfrontend.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject
import org.wenubey.wenuplayerfrontend.domain.model.VideoModel
import org.wenubey.wenuplayerfrontend.domain.repository.CommonRepository
import org.wenubey.wenuplayerfrontend.domain.repository.DispatcherProvider
import org.wenubey.wenuplayerfrontend.domain.repository.VideoRepository
import org.wenubey.wenuplayerfrontend.util.formatDuration
import org.wenubey.wenuplayerfrontend.util.initializeMediaPlayerComponent
import uk.co.caprica.vlcj.player.component.CallbackMediaPlayerComponent
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer
import java.awt.Component


class DesktopVideoPlayerViewModel : ViewModel(), VideoPlayerInterface {

    private val logger = Logger.withTag("DesktopVideoPlayerViewModel")
    private val dispatcherProvider: DispatcherProvider by inject(DispatcherProvider::class.java)
    private val videoRepository: VideoRepository by inject(VideoRepository::class.java)
    private val commonRepository: CommonRepository by inject(CommonRepository::class.java)
    private val ioDispatcher = dispatcherProvider.io()
    private val mainDispatcher = dispatcherProvider.main()

    private var component: Component = initializeMediaPlayerComponent()
    val mediaPlayer: EmbeddedMediaPlayer = component.mediaPlayer()
    val factory by mutableStateOf({ component })

    private val _videoStateFlow: MutableStateFlow<VideoState> = MutableStateFlow(VideoState())
    val videoState: StateFlow<VideoState> = _videoStateFlow.asStateFlow()

//    override fun getVideoState(): StateFlow<VideoState> = MutableStateFlow(VideoState())

    private val _subtitleStateFlow: MutableStateFlow<SubtitleState> =
        MutableStateFlow(SubtitleState())
    val subtitleState: StateFlow<SubtitleState> = _subtitleStateFlow.asStateFlow()

    private val timerScope = CoroutineScope(mainDispatcher + SupervisorJob())
    private var timerJob: Job? = null

    init {
        viewModelScope.launch {
            val result = videoRepository.getVideoSummaries()
            if (result.isSuccess) {
                _videoStateFlow.update { oldState ->
                    oldState.copy(
                        summaries = result.getOrNull()!!
                    )
                }
            }
        }
    }


    override fun onEvent(event: VideoPlayerEvent) {
        when (event) {
            is VideoPlayerEvent.FetchSubtitles -> {
                viewModelScope.launch(mainDispatcher) {
                    _subtitleStateFlow.update { oldState ->
                        oldState.copy(
                            subtitles = mediaPlayer.subpictures().trackDescriptions()
                        )
                    }
                }
            }
            //TODO fix this
            is VideoPlayerEvent.SelectSubtitle -> {
                viewModelScope.launch(ioDispatcher) {
                    mediaPlayer.subpictures().setTrack(event.subtitleIndex)
                }
                viewModelScope.launch(mainDispatcher) {
                    _subtitleStateFlow.update { oldState ->
                        oldState.copy(
                            selectedSubtitle = _subtitleStateFlow.value.subtitles.find { it.id() == event.subtitleIndex }
                        )
                    }
                }
            }

            is VideoPlayerEvent.SelectVideo -> {
                viewModelScope.launch(ioDispatcher) {
                    getVideoById(event.id, event.name)
                }
            }

            is VideoPlayerEvent.Pause -> {
                viewModelScope.launch(ioDispatcher) {
                    mediaPlayer.controls().pause()
                    updateLastTime()
                }
                viewModelScope.launch(mainDispatcher) {
                    stopTimer()
                    _videoStateFlow.update { oldState ->
                        oldState.copy(
                            isPlaying = false
                        )
                    }
                }
            }

            is VideoPlayerEvent.Play -> {
                viewModelScope.launch(ioDispatcher) {
                    mediaPlayer.controls().play()
                }
                viewModelScope.launch(mainDispatcher) {
                    startTimer()
                    _videoStateFlow.update { oldState ->
                        oldState.copy(
                            isPlaying = true
                        )
                    }
                }
            }

            is VideoPlayerEvent.SeekBackward -> {
                viewModelScope.launch(ioDispatcher) {
                    mediaPlayer.controls().skipTime(-5 * 1_000)
                }
            }

            is VideoPlayerEvent.SeekForward -> {
                viewModelScope.launch(ioDispatcher) {
                    mediaPlayer.controls().skipTime(5 * 1_000)
                }
            }

            is VideoPlayerEvent.ToggleFullScreen -> TODO()
            is VideoPlayerEvent.ToggleMute -> TODO()
            is VideoPlayerEvent.VolumeDown -> TODO()
            is VideoPlayerEvent.VolumeUp -> TODO()
        }
    }

    override fun setCurrentTime(currentTime: Long) {
        _videoStateFlow.update { oldState ->
            oldState.copy(
                currentTime = formatDuration(currentTime)
            )
        }
    }

    override fun setDuration(duration: Long) {
        TODO("Not yet implemented")
    }

    private fun prepareVideo(video: VideoModel) {
        viewModelScope.launch(ioDispatcher) {
            mediaPlayer.media().prepare(video.videoFile.absolutePath)
            putLastWatchedOnMedia()
            setCurrentTime(video.metadata.lastWatched)
            mediaPlayer.audio().setVolume(_videoStateFlow.value.currentVolume)
            logger.i { "Video Prepared" }
            onEvent(VideoPlayerEvent.FetchSubtitles)
        }
    }

    private fun getVideoById(id: String, name: String) {
        viewModelScope.launch(ioDispatcher) {
            val result = videoRepository.getVideoById(id, name)
            viewModelScope.launch(mainDispatcher) {
                if (result.isSuccess) {
                    logger.i { "Get Video By ID Success" }
                    _videoStateFlow.update { oldState ->
                        oldState.copy(
                            videoModel = result.getOrNull()!!
                        )
                    }
                    prepareVideo(_videoStateFlow.value.videoModel)
                } else {

                }
            }
        }
    }

    private fun startTimer() {
        timerJob = timerScope.launch {
            while (isActive) {
                setCurrentTime(mediaPlayer.status().time())
                delay(1000)
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    private suspend fun updateLastTime() {
        val id = _videoStateFlow.value.videoModel.metadata.id
        val lastWatched = mediaPlayer.status().time()
        val result = videoRepository.updateLastWatched(id, lastWatched)
        updateScreenInfo(result)
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

    private fun putLastWatchedOnMedia() {
        val lastWatched = _videoStateFlow.value.videoModel.metadata.lastWatched
        mediaPlayer.controls().play()
        mediaPlayer.controls().skipTime(lastWatched)
        mediaPlayer.controls().setPause(true)
    }
}

private fun Component.mediaPlayer() = when (this) {
    is CallbackMediaPlayerComponent -> mediaPlayer()
    is EmbeddedMediaPlayerComponent -> mediaPlayer()
    else -> error("mediaPlayer() can only be called on vlcj player components")
}

//actual fun getVideoState(): VideoPlayerInterface = DesktopVideoPlayerViewModel()
actual fun onEvent(event: VideoPlayerEvent): VideoPlayerInterface = DesktopVideoPlayerViewModel()
actual fun setCurrentTime(currentTime: Long): VideoPlayerInterface = DesktopVideoPlayerViewModel()
actual fun setDuration(duration: Long): VideoPlayerInterface = DesktopVideoPlayerViewModel()