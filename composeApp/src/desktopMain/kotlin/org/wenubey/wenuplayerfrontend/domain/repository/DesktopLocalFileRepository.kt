package org.wenubey.wenuplayerfrontend.domain.repository

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import org.jetbrains.skiko.toImage
import org.wenubey.wenuplayerfrontend.data.dto.VideoMetadata
import org.wenubey.wenuplayerfrontend.data.dto.VideoSummary
import org.wenubey.wenuplayerfrontend.domain.model.VideoModel
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import javax.swing.filechooser.FileSystemView

class DesktopLocalFileRepository : LocalFileRepository {

    override suspend fun fetchLocalVideoSummaries(): Result<List<VideoSummary>> {
        return try {
            val downloadsDir = getDownloadsDirectory()
            val wenuplayerDir = File(downloadsDir, "wenuplayer")

            if (!wenuplayerDir.exists()) {
                wenuplayerDir.mkdirs()
                return Result.success(emptyList())
            }

            if (wenuplayerDir.isDirectory) {
                val videoFiles = wenuplayerDir.listFiles { file ->
                    file.isFile && (file.extension == "mp4" || file.extension == "mkv")
                }?.toList() ?: emptyList()

                val videoSummaries = videoFiles.map { file ->
                    val thumbnail = extractThumbnail(file.absolutePath)
                    VideoSummary(
                        id = null,
                        title = file.nameWithoutExtension,
                        thumbnail = thumbnail
                    )
                }
                Result.success(videoSummaries)
            } else {
                Result.success(emptyList())
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun fetchDownloadsDirectory(): File {
        return File(getDownloadsDirectory())
    }

    override suspend fun getVideoByName(name: String): Result<VideoModel> {
        val downloadsDir = fetchDownloadsDirectory()
        val wenuplayerDir = File(downloadsDir, "wenuplayer")
        val videoFile = File(wenuplayerDir, name)

        if (!videoFile.exists() || !videoFile.isFile) {
            return Result.failure(IOException("Video file not found: ${videoFile.absolutePath}"))
        }
        val metadata = extractMetadata(videoFile)
        val videoModel = VideoModel(
            metadata = metadata,
            videoFile = videoFile
        )
        return Result.success(videoModel)
    }

    override suspend fun fetchThumbnail(name: String): ImageBitmap {
        return extractThumbnail(name)
    }

    private fun getDownloadsDirectory(): String {
        val os = System.getProperty("os.name").lowercase()
        return when {
            os.contains("win") -> {
                val userHome = System.getProperty("user.home")
                "$userHome\\Downloads"
            }

            os.contains("nix") || os.contains("nux") || os.contains("aix") || os.contains("mac") -> {
                val userHome = System.getProperty("user.home")
                "$userHome/Downloads"
            }

            else -> {
                throw Exception("Unsupported operating system.")
            }
        }
    }

    private fun extractMetadata(file: File): VideoMetadata {
        return VideoMetadata(
            id = "OFFLINE",
            title = file.name,
            url = file.absolutePath,
            // TODO fetch from Room in future
            lastWatched = 0L
        )
    }

    private fun extractThumbnail(fileName: String): ImageBitmap {
        val wenuplayerDir = getWenuplayerDir()
        val file = File(wenuplayerDir, fileName)
        val icon = FileSystemView.getFileSystemView().getSystemIcon(file)

        return if (icon != null) {
            val image = BufferedImage(
                icon.iconWidth,
                icon.iconHeight,
                BufferedImage.TYPE_INT_ARGB
            )
            val graphics = image.createGraphics()
            icon.paintIcon(null, graphics, 0, 0)
            graphics.dispose()

            image.toImage().toComposeImageBitmap()
        } else {
            // Create placeholder image
            val placeholder = BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB) // Customize size
            val graphics = placeholder.createGraphics()
            graphics.color = Color.GRAY // Customize color
            graphics.fillRect(0, 0, placeholder.width, placeholder.height)
            graphics.dispose()

            placeholder.toImage().toComposeImageBitmap()
        }
    }

    private fun getWenuplayerDir(): File {
        val downloadsDir = getDownloadsDirectory()
        return File(downloadsDir, "wenuplayer")
    }
}

actual fun fetchLocalVideoSummaries(): LocalFileRepository = DesktopLocalFileRepository()

actual fun fetchDownloadsDirectory(): LocalFileRepository = DesktopLocalFileRepository()
