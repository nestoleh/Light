package com.nestoleh.light.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.nestoleh.light.data.database.LightDatabase.Companion.VERSION
import com.nestoleh.light.data.database.dao.PlaceDao
import com.nestoleh.light.data.database.entity.PlaceEntity

@Database(
    entities = [
        PlaceEntity::class
    ],
    version = VERSION
)
abstract class LightDatabase : RoomDatabase() {

    abstract fun placeDao(): PlaceDao

    companion object {
        const val VERSION = 1
        const val DATABASE_FILE_NAME = "light_database.db"
    }
}