package com.nestoleh.light.data.database.entity

import androidx.room.Embedded
import androidx.room.Relation


data class PlaceWithScheduleEntity(
    @Embedded
    val place: PlaceEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "placeId"
    )
    val schedule: List<ScheduleSlotEntity>
)