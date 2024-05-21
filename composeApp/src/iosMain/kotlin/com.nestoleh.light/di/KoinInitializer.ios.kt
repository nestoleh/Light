package com.nestoleh.light.di

import org.koin.core.context.startKoin

actual class KoinInitializer {
    actual fun init() {
        startKoin {
            modules(
                dispatcherModule,
                databaseModule,
                datasourceModule,
                repositoryModule,
                viewModelModule
            )
        }
    }
}