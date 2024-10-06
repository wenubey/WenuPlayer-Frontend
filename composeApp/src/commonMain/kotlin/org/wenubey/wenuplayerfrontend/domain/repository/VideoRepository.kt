package org.wenubey.wenuplayerfrontend.domain.repository

import org.wenubey.wenuplayerfrontend.data.dto.VideoMetadata
import org.wenubey.wenuplayerfrontend.data.dto.VideoSummary
import org.wenubey.wenuplayerfrontend.domain.model.VideoModel
import java.io.File

interface VideoRepository {
    suspend fun uploadVideo(videoMetadata: VideoMetadata, videoFile: File): Result<Unit>
    suspend fun getVideoSummaries(): Result<List<VideoSummary>>
    suspend fun getVideoById(id: String, name: String): Result<VideoModel>
    suspend fun updateLastWatched(id: String, lastMillis: Long): Result<String>
    suspend fun deleteVideoById(id: String): Result<String>
    suspend fun restoreVideoById(id: String): Result<String>
}