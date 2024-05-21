package com.nestoleh.light.data.database

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import platform.Foundation.NSHomeDirectory

fun getLightDatabase(): LightDatabase {
    val databaseFile = NSHomeDirectory() + "/" + LightDatabase.DATABASE_FILE_NAME
    return Room.databaseBuilder<LightDatabase>(
        name = databaseFile,
        factory = {
            LightDatabase::class.instantiateImpl()
        }
    ).setDriver(BundledSQLiteDriver())
        .build()
}