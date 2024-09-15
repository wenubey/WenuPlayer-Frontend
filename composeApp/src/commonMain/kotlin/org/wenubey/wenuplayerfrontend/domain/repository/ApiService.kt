package org.wenubey.wenuplayerfrontend.domain.repository

import org.wenubey.wenuplayerfrontend.data.dto.VideoMetadata
import java.io.File

// TODO change this service with the necessary functionality
interface ApiService {
    suspend fun uploadVideo(videoMetadata: VideoMetadata, videoFile: File): Result<Unit>
    suspend fun getVideoSummaries()
}