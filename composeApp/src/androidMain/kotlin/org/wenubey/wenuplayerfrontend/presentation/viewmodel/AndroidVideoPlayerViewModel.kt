package org.wenubey.wenuplayerfrontend.presentation.viewmodel

import androidx.lifecycle.ViewModel

class AndroidVideoPlayerViewModel: ViewModel(), VideoPlayerInterface {

    override fun onEvent(event: VideoPlayerEvent) {
        TODO("Not yet implemented")
    }

    override fun setCurrentTime(currentTime: Long) {
        TODO("Not yet implemented")
    }

    override fun setDuration(duration: Long) {
        TODO("Not yet implemented")
    }
}

//actual fun getVideoState(): VideoPlayerInterface = AndroidVideoPlayerViewModel()
actual fun onEvent(event: VideoPlayerEvent): VideoPlayerInterface = AndroidVideoPlayerViewModel()
actual fun setCurrentTime(currentTime: Long): VideoPlayerInterface = AndroidVideoPlayerViewModel()
actual fun setDuration(duration: Long): VideoPlayerInterface = AndroidVideoPlayerViewModel()