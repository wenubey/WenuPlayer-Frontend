package org.wenubey.wenuplayerfrontend.presentation.viewmodel

interface VideoPlayerInterface {
    //fun getVideoState(): StateFlow<VideoState>
    fun onEvent(event: VideoPlayerEvent)
    fun setCurrentTime(currentTime: Long)
    fun setDuration(duration: Long)
}

//expect fun getVideoState(): VideoPlayerInterface
expect fun onEvent(event: VideoPlayerEvent): VideoPlayerInterface
expect fun setCurrentTime(currentTime: Long): VideoPlayerInterface
expect fun setDuration(duration: Long): VideoPlayerInterface