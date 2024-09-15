package org.wenubey.wenuplayerfrontend.data

import co.touchlab.kermit.Logger
import io.ktor.client.HttpClient
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.statement.HttpResponse
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import org.wenubey.wenuplayerfrontend.data.dto.VideoMetadata
import org.wenubey.wenuplayerfrontend.domain.repository.ApiService
import org.wenubey.wenuplayerfrontend.domain.repository.DispatcherProvider
import java.io.File
import java.util.UUID

// TODO: Change this ApiServiceImpl with necessary functionalities
class ApiServiceImpl(
    private val client: HttpClient,
    private val dispatcherProvider: DispatcherProvider,
) : ApiService {
    private val logger = Logger.withTag(TAG)
    private val ioDispatcher = dispatcherProvider.io()

    override suspend fun uploadVideo(videoMetadata: VideoMetadata, videoFile: File): Result<Unit> = safeApiCall(logger = logger, dispatcher =  ioDispatcher) {
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

    private companion object {
        const val TAG = "ApiService"
        const val BASE_URL = "http://localhost:8080/"
        const val VIDEOS_PATH = "videos/"
        const val UPLOAD_VIDEO_ENDPOINT = "upload"
        const val GET_VIDEO_METADATA_ENDPOINT = "video/{id}"
        const val GET_VIDEO_STREAM_ENDPOINT = "video/{id}/stream"
        const val GET_VIDEO_SUMMARIES_ENDPOINT = "/video-summaries"
        const val UPDATE_LAST_WATCHED_ENDPOINT = "/video/{id}/lastWatched"
        const val RESTORE_VIDEO_ENDPOINT = "/video/{id}/restore"
        const val SOFT_DELETE_VIDEO_ENDPOINT = "/video/{id}/trash"
    }
}