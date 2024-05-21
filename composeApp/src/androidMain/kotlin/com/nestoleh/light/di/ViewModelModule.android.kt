package com.nestoleh.light.di

import com.nestoleh.light.presentation.home.AddHomeViewModel
import com.nestoleh.light.presentation.main.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

actual val viewModelModule = module {
    viewModelOf(::MainViewModel)
    viewModelOf(::AddHomeViewModel)
}
