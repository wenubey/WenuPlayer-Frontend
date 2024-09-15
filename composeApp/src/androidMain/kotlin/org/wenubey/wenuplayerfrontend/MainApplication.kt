package org.wenubey.wenuplayerfrontend

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.wenubey.wenuplayerfrontend.di.commonModules

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@MainApplication)
            modules(
                commonModules,
                // TODO: add your android modules
            )
        }
    }
}