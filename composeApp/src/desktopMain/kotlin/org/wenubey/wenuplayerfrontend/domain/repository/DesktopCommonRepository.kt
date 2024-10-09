package org.wenubey.wenuplayerfrontend.domain.repository

import co.touchlab.kermit.Logger
import org.wenubey.wenuplayerfrontend.data.dto.VideoSummary
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.File
import java.net.InetAddress
import java.net.NetworkInterface
import javax.imageio.ImageIO
import javax.swing.ImageIcon
import javax.swing.filechooser.FileSystemView

class DesktopCommonRepository : CommonRepository {
    private val logger = Logger.withTag("DesktopCommonRepository")
    override fun hasInternetConnection(): Boolean {
        return try {
            val networkInterfaces = NetworkInterface.getNetworkInterfaces()
            while (networkInterfaces.hasMoreElements()) {
                val networkInterface = networkInterfaces.nextElement()
                if (networkInterface.isUp && !networkInterface.isLoopback) {
                    return true
                }
            }
            false
        } catch (e: Exception) {
            logger.e { "${e.message}" }
            false
        }
    }
}

actual fun hasInternetConnection(): CommonRepository = DesktopCommonRepository()