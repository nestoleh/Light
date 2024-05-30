package com.nestoleh.light.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import com.nestoleh.light.domain.model.ElectricityStatus

@Entity(
    primaryKeys = ["placeId", "dayOfWeek", "hourOfDay"],
    foreignKeys = [
        ForeignKey(
            entity = PlaceEntity::class,
            parentColumns = ["id"],
            childColumns = ["placeId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ScheduleSlotEntity(
    val placeId: String,
    val dayOfWeek: Int,
    val hourOfDay: Int,
    val status: ElectricityStatus
)