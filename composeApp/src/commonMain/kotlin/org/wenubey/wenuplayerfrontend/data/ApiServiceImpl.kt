package org.wenubey.wenuplayerfrontend.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.wenubey.wenuplayerfrontend.domain.repository.ApiService

// TODO: Change this ApiServiceImpl with necessary functionalities
// TODO: Implement safeApiCall helper to handle exceptions.
class ApiServiceImpl(private val client: HttpClient): ApiService {
    override suspend fun fetchData(): ApiResponse = withContext(Dispatchers.IO) {
       client.get("https://jsonplaceholder.typicode.com/posts/1").body<ApiResponse>()
    }
}