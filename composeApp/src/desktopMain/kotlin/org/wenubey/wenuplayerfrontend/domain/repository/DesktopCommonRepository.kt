package org.wenubey.wenuplayerfrontend.domain.repository

import co.touchlab.kermit.Logger
import java.net.NetworkInterface

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