package org.wenubey.wenuplayerfrontend.presentation.viewmodel

sealed class VideoPlayerEvent {
    data class SelectVideo(val id: String, val name: String): VideoPlayerEvent()
    data object Play: VideoPlayerEvent()
    data object Pause: VideoPlayerEvent()
    data object SeekForward: VideoPlayerEvent()
    data object SeekBackward: VideoPlayerEvent()
    data object ToggleMute: VideoPlayerEvent()
    data object ToggleFullScreen: VideoPlayerEvent()
    data object VolumeUp: VideoPlayerEvent()
    data object VolumeDown: VideoPlayerEvent()
    data object FetchSubtitles: VideoPlayerEvent()
    data class SelectSubtitle(val subtitleIndex: Int): VideoPlayerEvent()
}
