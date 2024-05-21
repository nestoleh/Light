package com.nestoleh.light.di

import com.nestoleh.light.presentation.main.MainViewModel
import com.nestoleh.light.presentation.place.AddPlaceViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

actual val viewModelModule = module {
    factoryOf(::MainViewModel)
    factoryOf(::AddPlaceViewModel)
}