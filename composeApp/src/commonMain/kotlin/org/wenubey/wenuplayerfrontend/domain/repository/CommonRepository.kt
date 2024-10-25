package org.wenubey.wenuplayerfrontend.domain.repository

interface CommonRepository {
    fun hasInternetConnection(): Boolean
    suspend fun showToast(message: String)
}

expect fun hasInternetConnection(): CommonRepository

expect fun showToast(): CommonRepository
