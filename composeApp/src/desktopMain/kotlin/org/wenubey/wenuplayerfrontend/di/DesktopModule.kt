package org.wenubey.wenuplayerfrontend.di

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import org.wenubey.wenuplayerfrontend.domain.repository.DesktopCommonRepository
import org.wenubey.wenuplayerfrontend.domain.repository.CommonRepository
import org.wenubey.wenuplayerfrontend.domain.repository.DesktopLocalFileRepository
import org.wenubey.wenuplayerfrontend.domain.repository.LocalFileRepository

private val repositoryModule = module {
    singleOf(::DesktopCommonRepository).bind(CommonRepository::class)
    singleOf(::DesktopLocalFileRepository).bind(LocalFileRepository::class)
}

val desktopModules = listOf(
    repositoryModule
)