package com.nestoleh.light.data.database.dao

import androidx.room.Dao
import androidx.room.Upsert
import com.nestoleh.light.data.database.entity.ScheduleSlotEntity

@Dao
interface ScheduleDao {
    @Upsert
    suspend fun upsertScheduleList(slots: List<ScheduleSlotEntity>)
}