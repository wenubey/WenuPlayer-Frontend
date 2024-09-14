package org.wenubey.wenuplayerfrontend.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// TODO: change DTO for VideoStream and VideoMetaData
@Serializable
data class ApiResponse(
    @SerialName("title") val title: String?
)
