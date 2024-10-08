package org.wenubey.wenuplayerfrontend.data

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

actual fun getHttpClient(): HttpClient = HttpClient(CIO) {
    install(ContentNegotiation) {
        json(
            Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            }
        )
    }
    install(HttpTimeout) {
        requestTimeoutMillis = 120_000
    }
}

//actual fun fetchDownloadsDirectory(): File {
//    val osName = System.getProperty("os.name")
//   return when {
//        osName.contains("Windows", ignoreCase = true) -> File(System.getenv("USERPROFILE"), "Downloads")
//        osName.contains("Linux", ignoreCase = true) -> File(System.getProperty("user.home"), "Downloads")
//        else -> File(System.getProperty("user.home"), "Downloads")
//   }
//}