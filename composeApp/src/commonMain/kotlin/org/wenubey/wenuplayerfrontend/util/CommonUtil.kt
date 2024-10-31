package org.wenubey.wenuplayerfrontend.util

import java.util.concurrent.TimeUnit

fun formatDuration(currentTime: Long): String {
    val hours = TimeUnit.MILLISECONDS.toHours(currentTime)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(currentTime) % 60
    val seconds = TimeUnit.MILLISECONDS.toSeconds(currentTime) % 60
    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}