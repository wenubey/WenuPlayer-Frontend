package org.wenubey.wenuplayerfrontend.presentation.viewmodel

import uk.co.caprica.vlcj.player.base.TrackDescription

data class SubtitleState(
    val subtitles: List<TrackDescription> = emptyList(),
    val selectedSubtitle: TrackDescription? = null,
    )
