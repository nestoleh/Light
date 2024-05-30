package com.nestoleh.light.domain.model

import com.nestoleh.light.util.randomUUID

data class Place(
    val id: String = randomUUID(),
    val name: String,
    val schedule: Schedule
)