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
import java.io.IOException
import java.util.UUID

class VideoRepositoryImpl(
    private val apiService: ApiService,
    dispatcherProvider: DispatcherProvider,
    commonRepository: CommonRepository,
    private val localFileRepository: LocalFileRepository,
) : VideoRepository {

    private val logger = Logger.withTag("VideoRepositoryImpl")
    private val ioDispatcher = dispatcherProvider.io()
    private val hasInternetConnection = commonRepository.hasInternetConnection()

    override suspend fun uploadVideo(videoPath: String): Result<Unit> =
        withContext(ioDispatcher) {
            val videoFile = fetchValidateFile(videoPath)
            videoFile?.also {
                val videoMetadata = VideoMetadata(
                    id = UUID.randomUUID().toString(),
                    title = videoFile.name,
                    url = videoFile.absolutePath,
                    lastWatched = 0L,
                )
                apiService.uploadVideo(videoMetadata, videoFile)
                Result.success(Unit)
            } ?: run {
                logger.e { "Video file not found" }
                Result.failure<Unit>(Exception("Video file not found"))
            }
            Result.failure(Exception("Unknown error occurred."))
        }

    override suspend fun getVideoSummaries(): Result<List<VideoSummary>> =
        withContext(ioDispatcher) {
            if (hasInternetConnection) {
                apiService.getVideoSummaries()
            } else {
                localFileRepository.fetchLocalVideoSummaries()
            }

        }

    override suspend fun getVideoById(id: String, name: String): Result<VideoModel> =
        withContext(ioDispatcher) {
            val result = apiService.getVideoById(id)
            if (hasInternetConnection && result.isSuccess) {
                result
            } else {
                localFileRepository.getVideoByName(name)
            }
        }

    override suspend fun updateLastWatched(id: String, lastMillis: Long): Result<String> =
        withContext(ioDispatcher) {
            if (hasInternetConnection) {
                apiService.updateLastWatched(id, lastMillis)

            } else {
                Result.failure(IOException("There is no internet connection"))
            }

        }

    override suspend fun deleteVideoById(id: String): Result<String> = withContext(ioDispatcher) {
        if (hasInternetConnection) {
            apiService.deleteVideoById(id)
        } else {
            // TODO set to Room and get from Room db in future
            Result.failure(IOException("There is no internet connection"))
        }
    }

    override suspend fun restoreVideoById(id: String): Result<String> = withContext(ioDispatcher) {
        if (hasInternetConnection) {
            apiService.restoreVideoById(id)
        } else {
            // TODO set to Room and get from Room db in future
            Result.failure(IOException("There is no internet connection"))
        }
    }

    private suspend fun fetchValidateFile(path: String): File? = withContext(ioDispatcher) {
        val file = File(path)
        if (file.isFile && file.exists()) {
            file
        } else {
            null
        }
    }
}

