package com.nestoleh.light.di

import com.nestoleh.light.presentation.main.MainViewModel
import com.nestoleh.light.presentation.place.add.AddPlaceViewModel
import com.nestoleh.light.presentation.place.settings.PlaceSettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

actual val viewModelModule = module {
    viewModelOf(::MainViewModel)
    viewModelOf(::AddPlaceViewModel)
    viewModelOf(::PlaceSettingsViewModel)
}


