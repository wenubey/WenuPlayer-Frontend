package org.wenubey.wenuplayerfrontend.di

import org.koin.compose.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import org.wenubey.wenuplayerfrontend.data.ApiServiceImpl
import org.wenubey.wenuplayerfrontend.data.getHttpClient
import org.wenubey.wenuplayerfrontend.domain.repository.ApiService
import org.wenubey.wenuplayerfrontend.presentation.MainViewModel

val commonModule = module {
    singleOf(::getHttpClient)
    singleOf(::ApiServiceImpl).bind(ApiService::class)
}

val commonViewModelModule = module {
    viewModelOf(::MainViewModel)
}