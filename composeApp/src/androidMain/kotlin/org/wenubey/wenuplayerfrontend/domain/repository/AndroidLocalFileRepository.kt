package org.wenubey.wenuplayerfrontend.domain.repository

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.os.Environment
import co.touchlab.kermit.Logger
import kotlinx.coroutines.withContext
import org.koin.java.KoinJavaComponent.inject
import org.wenubey.wenuplayerfrontend.data.dto.VideoMetadata
import org.wenubey.wenuplayerfrontend.data.dto.VideoSummary
import org.wenubey.wenuplayerfrontend.domain.model.VideoModel
import java.io.ByteArrayOutputStream
import java.io.File

class AndroidLocalFileRepository() : LocalFileRepository {

    private val logger = Logger.withTag("AndroidLocalFileRepository")
    private val dispatcherProvider: DispatcherProvider by inject(DispatcherProvider::class.java)
    private val ioDispatcher = dispatcherProvider.io()

    override suspend fun fetchLocalVideoSummaries(): Result<List<VideoSummary>> {
        val downloadsDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

        val wenuplayerDir =
            File(downloadsDir, "wenuplayer")
        return try {
            if (wenuplayerDir.exists() && wenuplayerDir.isDirectory) {
                val videoFiles = wenuplayerDir.listFiles { file ->
                    file.isFile && (file.extension == "mp4" || file.extension == "mkv")
                } ?: emptyArray()
                val videoSummaries = videoFiles.map { file ->
                    mapToVideoSummary(file)
                }
                Result.success(videoSummaries)
            } else {
                Result.failure(Exception("File not found."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun fetchDownloadsDirectory(): File {
        return Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DOWNLOADS
        )
    }

    override suspend fun getVideoByName(name: String): Result<VideoModel> =
        withContext(ioDispatcher) {
            try {
                logger.i { "getVideoByName: $name" }
                val downloadsDir = fetchDownloadsDirectory()
                val wenuplayerDir = File(downloadsDir, "wenuplayer")
                val videoFile = File(wenuplayerDir, name)
                val metadata = if (videoFile.exists() && videoFile.isFile) {
                    extractMetadata(videoFile)
                } else {
                    VideoMetadata.default()
                }
                Result.success(VideoModel(metadata = metadata, videoFile = videoFile))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    private fun extractMetadata(file: File): VideoMetadata {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(file.absolutePath)
        val title =
            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE) ?: file.name
        retriever.release()
        val videoMetadata = VideoMetadata(
            id = "OFFLINE",
            title = title,
            url = file.absolutePath,
            // TODO fetch from dataStore in future
            lastWatched = 0L,
        )
        return videoMetadata
    }

    private fun mapToVideoSummary(file: File): VideoSummary {
        val retriever = MediaMetadataRetriever()

        retriever.setDataSource(file.absolutePath)

        val title =
            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE) ?: file.name

        val bitmap = retriever.frameAtTime

        val thumbnail = if (bitmap != null) {
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.toByteArray()
        } else {
            null
        }

        retriever.release()
        return VideoSummary(
            id = null,
            title = title,
            thumbnail = thumbnail
        )
    }
}

actual fun fetchLocalVideoSummaries(): LocalFileRepository = AndroidLocalFileRepository()

actual fun fetchDownloadsDirectory(): LocalFileRepository = AndroidLocalFileRepository()