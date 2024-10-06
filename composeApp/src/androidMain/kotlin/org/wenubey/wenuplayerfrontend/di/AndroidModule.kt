package org.wenubey.wenuplayerfrontend.di

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import org.wenubey.wenuplayerfrontend.domain.repository.AndroidLocalCommonRepository
import org.wenubey.wenuplayerfrontend.domain.repository.CommonRepository

private val repositoryModule = module {
    singleOf(::AndroidLocalCommonRepository).bind(CommonRepository::class)
}

val androidModules = listOf(
    repositoryModule
)