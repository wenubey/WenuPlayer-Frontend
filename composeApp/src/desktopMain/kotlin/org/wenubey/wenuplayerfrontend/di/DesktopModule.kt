package org.wenubey.wenuplayerfrontend.di

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import org.wenubey.wenuplayerfrontend.domain.repository.DesktopCommonRepository
import org.wenubey.wenuplayerfrontend.domain.repository.CommonRepository

private val repositoryModule = module {
    singleOf(::DesktopCommonRepository).bind(CommonRepository::class)
}

val desktopModules = listOf(
    repositoryModule
)