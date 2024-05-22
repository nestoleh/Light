package com.nestoleh.light.domain.converters

import com.nestoleh.light.data.database.entity.PlaceEntity
import com.nestoleh.light.domain.model.Place


fun PlaceEntity.toPlace(): Place {
    return Place(
        id = id,
        name = name
    )
}