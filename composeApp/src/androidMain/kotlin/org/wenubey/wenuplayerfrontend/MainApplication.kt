package org.wenubey.wenuplayerfrontend

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.wenubey.wenuplayerfrontend.di.commonModule
import org.wenubey.wenuplayerfrontend.di.commonViewModelModule

class MainApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@MainApplication)
            modules(
                commonModule,
                commonViewModelModule
                // TODO: add your android modules
            )
        }
    }
}