package org.wenubey.wenuplayerfrontend.data

import co.touchlab.kermit.Logger
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.File
import java.nio.file.Files

expect fun getHttpClient(): HttpClient


suspend fun <T> safeApiCall(logger: Logger, dispatcher: CoroutineDispatcher,apiCall: suspend () -> T): Result<T> =
    withContext(dispatcher) {
        try {
            Result.success(apiCall())
        } catch (e: Exception) {
            logger.e { "Error during API call: ${e.message}" }
            Result.failure(e)
        }
    }

fun isValidContentType(contentType: String): Boolean {
    return contentType == "video/mp4" || contentType == "video/x-matroska"
}

fun getContentType(file: File): String =
    Files.probeContentType(file.toPath()) ?: "application/octet-stream"


