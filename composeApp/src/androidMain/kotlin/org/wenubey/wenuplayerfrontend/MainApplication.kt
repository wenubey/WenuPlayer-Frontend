package org.wenubey.wenuplayerfrontend

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.wenubey.wenuplayerfrontend.di.apiServiceModule
import org.wenubey.wenuplayerfrontend.di.commonModules
import org.wenubey.wenuplayerfrontend.di.commonViewModelModule
import org.wenubey.wenuplayerfrontend.di.dispatcherModule

class MainApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@MainApplication)
            modules(

                apiServiceModule,
                dispatcherModule,
                commonViewModelModule
                // TODO: add your android modules
            )
        }
    }
}