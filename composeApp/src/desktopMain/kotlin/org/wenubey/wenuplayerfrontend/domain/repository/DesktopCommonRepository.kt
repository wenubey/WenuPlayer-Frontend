package org.wenubey.wenuplayerfrontend.domain.repository

import org.wenubey.wenuplayerfrontend.data.dto.VideoSummary
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.File
import java.net.InetAddress
import javax.imageio.ImageIO
import javax.swing.ImageIcon
import javax.swing.filechooser.FileSystemView

class DesktopCommonRepository : CommonRepository {

    override fun hasInternetConnection(): Boolean {
        return try {
            InetAddress.getByName("google.com").isReachable(5000)
        } catch (e: Exception) {
            false
        }
    }
}

actual fun hasInternetConnection(): CommonRepository = DesktopCommonRepository()