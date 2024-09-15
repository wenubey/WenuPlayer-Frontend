package org.wenubey.wenuplayerfrontend.presentation

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.koin.core.context.startKoin
import org.wenubey.wenuplayerfrontend.di.commonModules

fun main() = application {
    startKoin {
        printLogger()
        modules(
            commonModules
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