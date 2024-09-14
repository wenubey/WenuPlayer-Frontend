package org.wenubey.wenuplayerfrontend.presentation

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.koin.core.context.startKoin

fun main() = application {
    startKoin {
        modules(
            // TODO add your desktop modules
        )
    }
    Window(
        onCloseRequest = ::exitApplication,
        title = "WenuPlayerFrontend",
    ) {
        App()
    }
}