package org.wenubey.wenuplayerfrontend.domain.repository

import org.wenubey.wenuplayerfrontend.data.ApiResponse

// TODO change this service with the necessary functionality
interface ApiService {
    suspend fun fetchData(): ApiResponse
}