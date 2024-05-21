package com.nestoleh.light.data.database

import android.content.Context
import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver

fun getLightDatabase(context: Context): LightDatabase {
    val databaseFile = context.getDatabasePath(LightDatabase.DATABASE_FILE_NAME)
    return Room.databaseBuilder<LightDatabase>(
        context = context.applicationContext,
        name = databaseFile.absolutePath
    )
        .setDriver(BundledSQLiteDriver())
        .build()
}