package org.wenubey.wenuplayerfrontend.domain.model

import org.wenubey.wenuplayerfrontend.data.dto.VideoMetadata
import java.io.File

data class VideoModel(
    val metadata: VideoMetadata,
    val videoFile: File
) {
    companion object {
        fun default(): VideoModel =
            VideoModel(
                metadata = VideoMetadata.default(),
                videoFile = File("")
            )
    }
}
