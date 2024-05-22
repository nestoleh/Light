package com.nestoleh.light.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ParameterEntity(
    @PrimaryKey
    val key: String,
    val value: String
)