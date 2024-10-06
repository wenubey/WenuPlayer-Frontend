package org.wenubey.wenuplayerfrontend.domain.repository

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Environment
import org.koin.java.KoinJavaComponent.inject
import org.wenubey.wenuplayerfrontend.data.dto.VideoSummary
import java.io.ByteArrayOutputStream
import java.io.File

class AndroidLocalCommonRepository: CommonRepository {
    private val context: Context by inject(Context::class.java)

    override fun hasInternetConnection(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)

        return capabilities != null && capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
    }

}



actual fun hasInternetConnection(): CommonRepository = AndroidLocalCommonRepository()
