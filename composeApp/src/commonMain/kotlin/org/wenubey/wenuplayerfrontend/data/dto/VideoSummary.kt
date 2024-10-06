package org.wenubey.wenuplayerfrontend.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class VideoSummary(
    val id: String?= null,
    val title: String,
    val thumbnail: ByteArray? = null,
)
