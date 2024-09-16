package org.wenubey.wenuplayerfrontend.data

import android.content.Context
import android.os.Environment
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import java.io.File

actual fun getHttpClient(): HttpClient = HttpClient(OkHttp) {
    install(ContentNegotiation) {
        json(
            Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            }
        )
    }
}


actual fun getDownloadsDirectory(): File {
    return Environment.getExternalStoragePublicDirectory(
        Environment.DIRECTORY_DOWNLOADS
    )
}

