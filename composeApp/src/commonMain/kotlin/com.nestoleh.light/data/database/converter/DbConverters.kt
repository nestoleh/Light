package com.nestoleh.light.data.database.converter

import androidx.room.TypeConverter
import com.nestoleh.light.domain.model.ElectricityStatus

class DbConverters {

    @TypeConverter
    fun toElectricityStatus(value: String) = enumValueOf<ElectricityStatus>(value)

    @TypeConverter
    fun fromElectricityStatus(value: ElectricityStatus) = value.name
}