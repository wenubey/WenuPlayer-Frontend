package org.wenubey.wenuplayerfrontend.domain.repository

import org.wenubey.wenuplayerfrontend.data.dto.VideoSummary
import java.io.File

interface CommonRepository {

    fun hasInternetConnection(): Boolean
}



expect fun hasInternetConnection(): CommonRepository
