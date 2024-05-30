package com.nestoleh.light.di

import com.nestoleh.light.presentation.main.MainViewModel
import com.nestoleh.light.presentation.place.add.AddPlaceViewModel
import com.nestoleh.light.presentation.place.settings.PlaceSettingsViewModel
import com.nestoleh.light.presentation.schedule.PlaceScheduleViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

actual val viewModelModule = module {
    factoryOf(::MainViewModel)
    factoryOf(::AddPlaceViewModel)
    factoryOf(::PlaceSettingsViewModel)
    factoryOf(::PlaceScheduleViewModel)
}