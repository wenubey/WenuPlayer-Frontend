package org.wenubey.wenuplayerfrontend.di

import org.koin.compose.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import org.wenubey.wenuplayerfrontend.domain.repository.DesktopCommonRepository
import org.wenubey.wenuplayerfrontend.domain.repository.CommonRepository
import org.wenubey.wenuplayerfrontend.domain.repository.DesktopLocalFileRepository
import org.wenubey.wenuplayerfrontend.domain.repository.LocalFileRepository
import org.wenubey.wenuplayerfrontend.presentation.viewmodel.DesktopVideoPlayerViewModel

private val repositoryModule = module {
    singleOf(::DesktopCommonRepository).bind(CommonRepository::class)
    singleOf(::DesktopLocalFileRepository).bind(LocalFileRepository::class)
}

private val viewModelModule = module {
    viewModelOf(::DesktopVideoPlayerViewModel)
}

val desktopModules = listOf(
    repositoryModule,
    viewModelModule,
)