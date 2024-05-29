package com.nestoleh.light.data.database.entity

import androidx.room.Entity
import com.nestoleh.light.domain.model.ElectricityStatus

@Entity(
    primaryKeys = ["placeId", "dayOfWeek", "hourOfDay"],
    foreignKeys = [
        androidx.room.ForeignKey(
            entity = PlaceEntity::class,
            parentColumns = ["id"],
            childColumns = ["placeId"],
            onDelete = androidx.room.ForeignKey.CASCADE
        )
    ]
)
data class ScheduleSlotEntity(
    val placeId: Int,
    val dayOfWeek: Int,
    val hourOfDay: Int,
    val status: ElectricityStatus
)