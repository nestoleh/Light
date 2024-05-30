package com.nestoleh.light.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "Place"
)
data class PlaceEntity(
    @PrimaryKey
    val id: String,
    val name: String,
)