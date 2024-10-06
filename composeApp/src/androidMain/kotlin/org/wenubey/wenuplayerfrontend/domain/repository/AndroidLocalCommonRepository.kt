package org.wenubey.wenuplayerfrontend.domain.repository

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Environment
import org.koin.compose.koinInject
import org.koin.java.KoinJavaComponent.inject
import org.wenubey.wenuplayerfrontend.data.dto.VideoSummary
import java.io.ByteArrayOutputStream
import java.io.File

class AndroidLocalCommonRepository: CommonRepository {
    private val context: Context by inject(Context::class.java)

    override fun fetchLocalVideoSummaries(): Result<List<VideoSummary>> {
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

    override fun fetchDownloadsDirectory(): File {
        return Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DOWNLOADS
        )
    }

    override fun hasInternetConnection(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)

        return capabilities != null && capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
    }


    private fun mapToVideoSummary(file: File): VideoSummary {
        val retriever = MediaMetadataRetriever()

        retriever.setDataSource(file.absolutePath)

        val title =
            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE) ?: file.nameWithoutExtension

        val bitmap = retriever.frameAtTime

        val thumbnail = if(bitmap != null) {
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

actual fun fetchLocalVideoSummaries(): CommonRepository = AndroidLocalCommonRepository()

actual fun fetchDownloadsDirectory(): CommonRepository = AndroidLocalCommonRepository()

actual fun hasInternetConnection(): CommonRepository = AndroidLocalCommonRepository()
