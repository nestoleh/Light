package com.nestoleh.light.di

import com.nestoleh.light.presentation.home.AddHomeViewModel
import com.nestoleh.light.presentation.main.MainViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

actual val viewModelModule = module {
    factoryOf(::MainViewModel)
    factoryOf(::AddHomeViewModel)
}