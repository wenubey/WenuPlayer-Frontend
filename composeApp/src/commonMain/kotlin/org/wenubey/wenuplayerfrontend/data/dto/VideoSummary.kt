package org.wenubey.wenuplayerfrontend.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class VideoSummary(
    val id: String,
    val title: String
)
