package org.wenubey.wenuplayerfrontend.domain.repository

import co.touchlab.kermit.Logger
import java.net.HttpURLConnection
import java.net.URL

class DesktopCommonRepository : CommonRepository {
    override fun hasInternetConnection(): Boolean {
        return try {
            val url = URL("https://www.google.com")
            val urlConnection = url.openConnection() as HttpURLConnection
            urlConnection.connectTimeout = 5000
            urlConnection.connect()

            // Check if the response code is 200 (HTTP_OK)
            val isConnected = urlConnection.responseCode == 200
            urlConnection.disconnect()
            isConnected
        } catch (e: Exception) {
            false
        }
    }
}

actual fun hasInternetConnection(): CommonRepository = DesktopCommonRepository()