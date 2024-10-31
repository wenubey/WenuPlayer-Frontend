package org.wenubey.wenuplayerfrontend.presentation.viewmodel

import org.wenubey.wenuplayerfrontend.data.dto.VideoSummary
import org.wenubey.wenuplayerfrontend.domain.model.VideoModel

// TODO create new states like current video queue change this video State
data class VideoState(
    val videoModel: VideoModel = VideoModel.default(),
    val currentQueue: List<VideoModel> = listOf(),
    val summaries: List<VideoSummary> = listOf(),
    val currentTime: String = "00:00:00",
    val totalTime: String = "00:00:00",
    val currentVolume: Int = 50,
    val isPlaying: Boolean = false,
)