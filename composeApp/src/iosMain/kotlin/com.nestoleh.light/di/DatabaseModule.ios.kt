package com.nestoleh.light.di

import com.nestoleh.light.data.database.getLightDatabase
import org.koin.core.module.Module
import org.koin.dsl.module

actual val databaseModule: Module = module {
    single { getLightDatabase() }
}