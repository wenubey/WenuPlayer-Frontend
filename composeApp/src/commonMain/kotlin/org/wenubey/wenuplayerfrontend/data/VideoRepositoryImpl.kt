package org.wenubey.wenuplayerfrontend.data

import kotlinx.coroutines.withContext
import org.wenubey.wenuplayerfrontend.data.dto.VideoMetadata
import org.wenubey.wenuplayerfrontend.data.dto.VideoSummary
import org.wenubey.wenuplayerfrontend.domain.model.VideoModel
import org.wenubey.wenuplayerfrontend.domain.repository.ApiService
import org.wenubey.wenuplayerfrontend.domain.repository.DispatcherProvider
import org.wenubey.wenuplayerfrontend.domain.repository.CommonRepository
import org.wenubey.wenuplayerfrontend.domain.repository.VideoRepository
import java.io.File

class VideoRepositoryImpl(
    private val apiService: ApiService,
    private val dispatcherProvider: DispatcherProvider,
    private val commonRepository: CommonRepository,
): VideoRepository {

    private val ioDispatcher = dispatcherProvider.io()

    override suspend fun uploadVideo(videoMetadata: VideoMetadata, videoFile: File): Result<Unit> =
        withContext(ioDispatcher) {
            apiService.uploadVideo(videoMetadata, videoFile)
        }

    override suspend fun getVideoSummaries(): Result<List<VideoSummary>> =
        withContext(ioDispatcher) {
            if(commonRepository.hasInternetConnection()) {
                apiService.getVideoSummaries()
            } else {
                commonRepository.fetchLocalVideoSummaries()
            }
        }



    override suspend fun getVideoById(id: String): Result<VideoModel> =
        withContext(ioDispatcher) {
            apiService.getVideoById(id)
        }

    override suspend fun updateLastWatched(id: String, lastMillis: Long): Result<String> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteVideoById(id: String): Result<String> {
        TODO("Not yet implemented")
    }

    override suspend fun restoreVideoById(id: String): Result<String> {
        TODO("Not yet implemented")
    }
}

