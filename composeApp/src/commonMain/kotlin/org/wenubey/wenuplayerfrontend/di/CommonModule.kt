package org.wenubey.wenuplayerfrontend.di

import org.koin.compose.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import org.wenubey.wenuplayerfrontend.data.ApiServiceImpl
import org.wenubey.wenuplayerfrontend.data.DispatcherProviderImpl
import org.wenubey.wenuplayerfrontend.data.VideoRepositoryImpl
import org.wenubey.wenuplayerfrontend.data.getHttpClient
import org.wenubey.wenuplayerfrontend.domain.repository.ApiService
import org.wenubey.wenuplayerfrontend.domain.repository.DispatcherProvider
import org.wenubey.wenuplayerfrontend.domain.repository.VideoRepository
import org.wenubey.wenuplayerfrontend.presentation.viewmodel.MainViewModel

private val dispatcherModule = module {
    singleOf(::DispatcherProviderImpl).bind(DispatcherProvider::class)
}

private val apiServiceModule = module {
    singleOf(::getHttpClient)
    singleOf(::ApiServiceImpl).bind(ApiService::class)
}

private val commonViewModelModule = module {
    viewModelOf(::MainViewModel)
}

private val repositoryModule = module {
    singleOf(::VideoRepositoryImpl).bind(VideoRepository::class)
}

val commonModules = listOf(
    dispatcherModule,
    apiServiceModule,
    commonViewModelModule,
    repositoryModule
)