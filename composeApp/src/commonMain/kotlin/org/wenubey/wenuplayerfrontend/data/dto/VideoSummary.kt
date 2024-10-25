package org.wenubey.wenuplayerfrontend.data.dto

import androidx.compose.ui.graphics.ImageBitmap
import kotlinx.serialization.Serializable

@Serializable
data class VideoSummary(
    val id: String?= null,
    val title: String,
    val thumbnail: ImageBitmap? = null,
)
