package org.wenubey.wenuplayerfrontend.presentation

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.koin.core.context.startKoin
import org.wenubey.wenuplayerfrontend.di.commonModule
import org.wenubey.wenuplayerfrontend.di.commonViewModelModule

fun main() = application {
    startKoin {
        modules(
            commonModule,
            commonViewModelModule
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