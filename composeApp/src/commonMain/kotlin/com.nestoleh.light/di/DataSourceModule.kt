package com.nestoleh.light.di

import com.nestoleh.light.data.database.LightDatabase
import com.nestoleh.light.data.database.dao.ParametersDao
import com.nestoleh.light.data.database.dao.PlaceDao
import com.nestoleh.light.data.database.dao.ScheduleDao
import org.koin.dsl.module

val datasourceModule = module {
    single<PlaceDao> {
        val db = get<LightDatabase>()
        db.placeDao()
    }
    single<ParametersDao> {
        val db = get<LightDatabase>()
        db.parametersDao()
    }
    single<ScheduleDao> {
        val db = get<LightDatabase>()
        db.scheduleDao()
    }
}