package com.nestoleh.light.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.nestoleh.light.data.database.LightDatabase.Companion.VERSION
import com.nestoleh.light.data.database.converter.DbConverters
import com.nestoleh.light.data.database.dao.ParametersDao
import com.nestoleh.light.data.database.dao.PlaceDao
import com.nestoleh.light.data.database.entity.ParameterEntity
import com.nestoleh.light.data.database.entity.PlaceEntity
import com.nestoleh.light.data.database.entity.ScheduleSlotEntity

@Database(
    entities = [
        PlaceEntity::class,
        ParameterEntity::class,
        ScheduleSlotEntity::class
    ],
    version = VERSION
)
@TypeConverters(DbConverters::class)
abstract class LightDatabase : RoomDatabase() {

    abstract fun placeDao(): PlaceDao
    abstract fun parametersDao(): ParametersDao

    companion object {
        const val VERSION = 1
        const val DATABASE_FILE_NAME = "light_database.db"
    }
}