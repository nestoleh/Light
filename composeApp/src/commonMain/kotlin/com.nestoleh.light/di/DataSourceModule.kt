package com.nestoleh.light.di

import com.nestoleh.light.data.database.LightDatabase
import com.nestoleh.light.data.database.dao.PlaceDao
import org.koin.dsl.module

val datasourceModule = module {
    single<PlaceDao> {
        val db = get<LightDatabase>()
        db.placeDao()
    }
}