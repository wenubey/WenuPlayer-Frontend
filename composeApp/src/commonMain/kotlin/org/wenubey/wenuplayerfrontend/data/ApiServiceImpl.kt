package org.wenubey.wenuplayerfrontend.data

import co.touchlab.kermit.Logger
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.readBytes
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import org.wenubey.wenuplayerfrontend.data.dto.VideoMetadata
import org.wenubey.wenuplayerfrontend.data.dto.VideoSummary
import org.wenubey.wenuplayerfrontend.domain.model.VideoModel
import org.wenubey.wenuplayerfrontend.domain.repository.ApiService
import org.wenubey.wenuplayerfrontend.domain.repository.DispatcherProvider
import org.wenubey.wenuplayerfrontend.domain.repository.CommonRepository
import org.wenubey.wenuplayerfrontend.domain.repository.LocalFileRepository
import java.io.File
import java.util.UUID

// TODO: Change this ApiServiceImpl with necessary functionalities
class ApiServiceImpl(
    private val client: HttpClient,
    private val dispatcherProvider: DispatcherProvider,
    private val commonRepository: CommonRepository,
    private val localFileRepository: LocalFileRepository,
) : ApiService {
    private val logger = Logger.withTag(TAG)
    private val ioDispatcher = dispatcherProvider.io()

    override suspend fun uploadVideo(videoMetadata: VideoMetadata, videoFile: File): Result<Unit> =
        safeApiCall(logger = logger, dispatcher = ioDispatcher) {
            val contentType = getContentType(videoFile)
            val uuid = UUID.randomUUID().toString()
            if (!isValidContentType(contentType)) {
                Result.failure<Exception>(Exception("Unsupported content type: $contentType"))
            }

            val response: HttpResponse = client.submitFormWithBinaryData(
                url = BASE_URL + VIDEOS_PATH + UPLOAD_VIDEO_ENDPOINT,
                formData = formData {
                    append("uuid", uuid)
                    append("file", videoFile.readBytes(), Headers.build {
                        append(HttpHeaders.ContentType, contentType)
                        append(HttpHeaders.ContentDisposition, "filename=\"${videoFile.name}\"")
                    })
                }
            )
            if (response.status == HttpStatusCode.Created) {
                Result.success(Unit)
            } else {
                Result.failure<Exception>(Exception("Unknown error occurred try again."))
            }
        }

    override suspend fun getVideoSummaries(): Result<List<VideoSummary>> =
        safeApiCall(logger = logger, dispatcher = ioDispatcher) {
            val url = BASE_URL + VIDEOS_PATH + GET_VIDEO_SUMMARIES_ENDPOINT
            client.get(url)
                .body<List<VideoSummary>>()
        }

    override suspend fun getVideoById(id: String): Result<VideoModel> =
        safeApiCall(logger = logger, dispatcher = ioDispatcher) {
            val metadata = fetchVideoMetadata(id)
            val contentType = determineContentType(metadata.title)
            val videoFile = getVideoFile(metadata)

            if (videoFile.exists()) {
                logger.i { "From Device: $metadata" }
                VideoModel(metadata, videoFile)
            } else {
                logger.i { "From Backend: $metadata" }
                val fileResponse = downloadVideoStream(id, contentType)
                writeVideoToFile(videoFile, fileResponse)
                VideoModel(metadata, videoFile)
            }
        }

    private suspend fun fetchVideoMetadata(id: String): VideoMetadata {
        val url = BASE_URL + VIDEOS_PATH + GET_VIDEO_METADATA_ENDPOINT
        return client.get(url) {
            parameter("id", id)
        }.body<VideoMetadata>()
    }


    private fun determineContentType(title: String): ContentType {
        return when {
            title.endsWith(".mp4", ignoreCase = true) -> ContentType.Video.MP4
            title.equals(".mkv", ignoreCase = true) -> ContentType("video", "x-matroska")
            else -> ContentType.Video.Any
        }
    }

    private suspend fun getVideoFile(metadata: VideoMetadata): File  {
        val downloadsDir = localFileRepository.fetchDownloadsDirectory()
        val wenuPlayerDir = File(downloadsDir, WENU_PLAYER_DIR)

        if (!wenuPlayerDir.exists()) {
            wenuPlayerDir.mkdirs()
        }

        return File(wenuPlayerDir, metadata.title)
    }

    private suspend fun downloadVideoStream(id: String, contentType: ContentType): ByteArray {
        val url = BASE_URL + VIDEOS_PATH + GET_VIDEO_STREAM_ENDPOINT

        return client.get(url) {
            parameter("id", id)
            header(HttpHeaders.Accept, contentType)
        }.readBytes()
    }

    private fun writeVideoToFile(file: File, videoBytes: ByteArray) {
        file.writeBytes(videoBytes)
    }

    override suspend fun updateLastWatched(id: String, lastMillis: Long): Result<String> =
        safeApiCall(logger = logger, dispatcher = ioDispatcher) {
            val url = BASE_URL + VIDEOS_PATH + UPDATE_LAST_WATCHED_ENDPOINT

            val response = client.put(url) {
                parameter("id", id)
                contentType(ContentType.Application.Json)
                setBody(lastMillis)
            }

            if (response.status.isSuccess()) {
                UPDATE_LAST_WATCH_SUCCESS
            } else {
                val errorReason = response.bodyAsText()
                UPDATE_LAST_WATCH_ERROR + errorReason
            }
        }

    override suspend fun deleteVideoById(id: String): Result<String> =
        safeApiCall(logger = logger, dispatcher = ioDispatcher) {
            val url = BASE_URL + VIDEOS_PATH + SOFT_DELETE_VIDEO_ENDPOINT
            val response = client.delete(url) {
                parameter("id", id)
            }
            if (response.status.isSuccess()) {
                SOFT_DELETE_VIDEO_SUCCESS
            } else {
                val errorReason = response.bodyAsText()
                SOFT_DELETE_VIDEO_ERROR + errorReason
            }
        }

    override suspend fun restoreVideoById(id: String): Result<String> =
        safeApiCall(logger = logger, dispatcher = ioDispatcher) {
            val url = BASE_URL + VIDEOS_PATH + RESTORE_VIDEO_ENDPOINT
            val response = client.put(url) {
                parameter("id", id)
            }
            if (response.status.isSuccess()) {
                RESTORE_VIDEO_SUCCESS
            } else {
                val errorReason = response.bodyAsText()
                RESTORE_VIDEO_ERROR + errorReason
            }
        }

    private companion object {
        const val TAG = "ApiService"
        const val BASE_URL = "http://0.0.0.0:8080/"
        const val VIDEOS_PATH = "videos/"
        const val UPLOAD_VIDEO_ENDPOINT = "upload"
        const val GET_VIDEO_METADATA_ENDPOINT = "video/{id}"
        const val GET_VIDEO_STREAM_ENDPOINT = "video/{id}/stream"
        const val GET_VIDEO_SUMMARIES_ENDPOINT = "video-summaries"
        const val UPDATE_LAST_WATCHED_ENDPOINT = "/video/{id}/lastWatched"
        const val RESTORE_VIDEO_ENDPOINT = "/video/{id}/restore"
        const val SOFT_DELETE_VIDEO_ENDPOINT = "/video/{id}/trash"
        const val WENU_PLAYER_DIR = "wenuplayer"
        const val UPDATE_LAST_WATCH_SUCCESS = "Last watched time updated."
        const val RESTORE_VIDEO_SUCCESS = "Video restored successfully"
        const val SOFT_DELETE_VIDEO_SUCCESS =
            "Video successfully move to trash. It will automatically deleted after 6 hours."
        const val SOFT_DELETE_VIDEO_ERROR = "Failed to move to trash: "
        const val UPDATE_LAST_WATCH_ERROR = "Failed to update last watched time: "
        const val RESTORE_VIDEO_ERROR = "Failed to restore video: "
    }
}