package org.wenubey.wenuplayerfrontend.domain.repository

import org.wenubey.wenuplayerfrontend.data.dto.VideoSummary
import org.wenubey.wenuplayerfrontend.domain.model.VideoModel
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.File
import javax.imageio.ImageIO
import javax.swing.ImageIcon
import javax.swing.filechooser.FileSystemView

class DesktopLocalFileRepository: LocalFileRepository {

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
                    val thumbnail = extractThumbnail(file)
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
        TODO("Not yet implemented")
    }

    override suspend fun getVideoByName(name: String): Result<VideoModel> {
        TODO("Not yet implemented")
    }

    private fun getDownloadsDirectory(): String {
        val os = System.getProperty("os.name").lowercase()
        return when {
            os.contains("win") -> {
                val userHome = System.getProperty("user.home")
                "$userHome\\Downloads"
            }

            os.contains("nix") || os.contains("nux") || os.contains("aix") -> {
                val userHome = System.getProperty("user.home")
                "$userHome/Downloads"
            }

            else -> {
                throw Exception("Unsupported operating system.")
            }
        }
    }

    private fun extractThumbnail(file: File): ByteArray? {
        val icon = FileSystemView.getFileSystemView().getSystemIcon(file) as ImageIcon
        val image = BufferedImage(
            icon.iconWidth,
            icon.iconHeight,
            BufferedImage.TYPE_INT_ARGB
        )
        val graphics = image.createGraphics()
        icon.paintIcon(null, graphics, 0, 0)
        graphics.dispose()

        val byteArrayOutputStream = ByteArrayOutputStream()
        ImageIO.write(image, "png", byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()
    }
}

actual fun fetchLocalVideoSummaries(): LocalFileRepository = DesktopLocalFileRepository()

actual fun fetchDownloadsDirectory(): LocalFileRepository = DesktopLocalFileRepository()
