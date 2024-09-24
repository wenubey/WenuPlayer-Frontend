package org.wenubey.wenuplayerfrontend.data

import co.touchlab.kermit.Logger
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsChannel
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
import java.io.File
import java.util.UUID
import kotlin.math.log

// TODO: Change this ApiServiceImpl with necessary functionalities
class ApiServiceImpl(
    private val client: HttpClient,
    private val dispatcherProvider: DispatcherProvider,
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
            client.get(BASE_URL + VIDEOS_PATH + GET_VIDEO_SUMMARIES_ENDPOINT)
                .body<List<VideoSummary>>()
        }

    override suspend fun getVideoById(id: String): Result<VideoModel> =
        safeApiCall(logger = logger, dispatcher = ioDispatcher) {
            val metadata = fetchVideoMetadata(id)
            val contentType = determineContentType(metadata.title)
            val videoFile = getVideoFile(metadata)

            if (videoFile.exists()) {
                VideoModel(metadata, videoFile)
            } else {
                val fileResponse = downloadVideoStream(id, contentType)
                writeVideoToFile(videoFile, fileResponse)
                VideoModel(metadata, videoFile)
            }
        }

    private suspend fun fetchVideoMetadata(id: String): VideoMetadata {
        val url = BASE_URL + VIDEOS_PATH + GET_VIDEO_METADATA_ENDPOINT.replace("{id}", id)
        return client.get(url).body<VideoMetadata>()
    }


    private fun determineContentType(title: String): ContentType {
        return when {
            title.endsWith(".mp4", ignoreCase = true) -> ContentType.Video.MP4
            title.equals(".mkv", ignoreCase = true) -> ContentType("video", "x-matroska")
            else -> ContentType.Video.Any
        }
    }

    private fun getVideoFile(metadata: VideoMetadata): File {
        val downloadsDir = getDownloadsDirectory()
        val wenuPlayerDir = File(downloadsDir, WENU_PLAYER_DIR)

        if (!wenuPlayerDir.exists()) {
            wenuPlayerDir.mkdirs()
        }

        return File(wenuPlayerDir, metadata.title)
    }

    private suspend fun downloadVideoStream(id: String, contentType: ContentType): ByteArray {
        val url = BASE_URL + VIDEOS_PATH + GET_VIDEO_STREAM_ENDPOINT.replace("{id}", id)

        return client.get(url) {
            header(HttpHeaders.Accept, contentType)
        }.readBytes()
    }

    private fun writeVideoToFile(file: File, videoBytes: ByteArray) {
        file.writeBytes(videoBytes)
    }

    override suspend fun updateLastWatched(id: String, lastMillis: Long): Result<Unit> =
        safeApiCall(logger = logger, dispatcher = ioDispatcher) {
            val url = BASE_URL + VIDEOS_PATH + UPDATE_LAST_WATCHED_ENDPOINT.replace("{id}", id)

           client.put(url) {
                contentType(ContentType.Application.Json)
                setBody(lastMillis)
            }
        }


    private companion object {
        const val TAG = "ApiService"
        const val BASE_URL = "http://192.168.0.20:8080/"
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
        const val UPDATE_LAST_WATCH_ERROR = "Failed to update last watched time: "
    }
}