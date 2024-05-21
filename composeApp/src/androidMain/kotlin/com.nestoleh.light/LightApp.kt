package com.nestoleh.light

import android.app.Application
import com.nestoleh.light.di.KoinInitializer

class LightApp : Application() {

    override fun onCreate() {
        super.onCreate()
        KoinInitializer(
            context = applicationContext
        ).init()
    }
}