package org.wenubey.wenuplayerfrontend.domain.repository

import org.wenubey.wenuplayerfrontend.data.dto.VideoSummary
import java.io.File

interface CommonRepository {
    fun fetchLocalVideoSummaries(): Result<List<VideoSummary>>
    fun fetchDownloadsDirectory(): File
    fun hasInternetConnection(): Boolean
}

expect fun fetchLocalVideoSummaries(): CommonRepository

expect fun fetchDownloadsDirectory(): CommonRepository

expect fun hasInternetConnection(): CommonRepository
