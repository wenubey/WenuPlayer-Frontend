package org.wenubey.wenuplayerfrontend.domain.repository

import org.wenubey.wenuplayerfrontend.data.dto.VideoSummary
import org.wenubey.wenuplayerfrontend.domain.model.VideoModel
import java.io.File


interface LocalFileRepository  {
    suspend fun fetchLocalVideoSummaries(): Result<List<VideoSummary>>
    suspend fun fetchDownloadsDirectory(): File
    suspend fun getVideoByName(name: String): Result<VideoModel>

}
expect fun fetchLocalVideoSummaries(): LocalFileRepository

expect fun fetchDownloadsDirectory(): LocalFileRepository