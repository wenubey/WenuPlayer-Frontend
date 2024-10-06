package org.wenubey.wenuplayerfrontend.data

import co.touchlab.kermit.Logger
import kotlinx.coroutines.withContext
import org.wenubey.wenuplayerfrontend.data.dto.VideoMetadata
import org.wenubey.wenuplayerfrontend.data.dto.VideoSummary
import org.wenubey.wenuplayerfrontend.domain.model.VideoModel
import org.wenubey.wenuplayerfrontend.domain.repository.ApiService
import org.wenubey.wenuplayerfrontend.domain.repository.DispatcherProvider
import org.wenubey.wenuplayerfrontend.domain.repository.CommonRepository
import org.wenubey.wenuplayerfrontend.domain.repository.LocalFileRepository
import org.wenubey.wenuplayerfrontend.domain.repository.VideoRepository
import java.io.File

class VideoRepositoryImpl(
    private val apiService: ApiService,
    private val dispatcherProvider: DispatcherProvider,
    private val commonRepository: CommonRepository,
    private val localFileRepository: LocalFileRepository,
): VideoRepository {

    private val logger = Logger.withTag("VideoRepositoryImpl")
    private val ioDispatcher = dispatcherProvider.io()
    private val hasInternetConnection = commonRepository.hasInternetConnection()

    override suspend fun uploadVideo(videoMetadata: VideoMetadata, videoFile: File): Result<Unit> =
        withContext(ioDispatcher) {
            apiService.uploadVideo(videoMetadata, videoFile)
        }

    override suspend fun getVideoSummaries(): Result<List<VideoSummary>> =
        withContext(ioDispatcher) {
            if(hasInternetConnection) {
                apiService.getVideoSummaries()
            } else {
                localFileRepository.fetchLocalVideoSummaries()
            }
        }



    override suspend fun getVideoById(id: String, name: String): Result<VideoModel> =
        withContext(ioDispatcher) {
            logger.i { "getVideoById: id: $id, name: $name"}
            if (hasInternetConnection) {
                apiService.getVideoById(id)
            } else {
                localFileRepository.getVideoByName(name)
            }
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

