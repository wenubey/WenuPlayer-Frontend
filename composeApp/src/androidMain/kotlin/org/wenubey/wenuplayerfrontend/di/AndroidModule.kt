package org.wenubey.wenuplayerfrontend.di

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import org.wenubey.wenuplayerfrontend.domain.repository.AndroidLocalCommonRepository
import org.wenubey.wenuplayerfrontend.domain.repository.AndroidLocalFileRepository
import org.wenubey.wenuplayerfrontend.domain.repository.CommonRepository
import org.wenubey.wenuplayerfrontend.domain.repository.LocalFileRepository

private val repositoryModule = module {
    singleOf(::AndroidLocalCommonRepository).bind(CommonRepository::class)
    singleOf(::AndroidLocalFileRepository).bind(LocalFileRepository::class)
}

val androidModules = listOf(
    repositoryModule
)