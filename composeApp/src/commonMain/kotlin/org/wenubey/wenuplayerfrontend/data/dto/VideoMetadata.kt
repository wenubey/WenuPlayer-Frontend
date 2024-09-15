package org.wenubey.wenuplayerfrontend.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class VideoMetadata(
    val id: String,
    val title: String,
    val url: String,
    val lastWatched: Long,
    val deletedAt: Long? = null,
)
