package com.nestoleh.light.data.converters

import com.nestoleh.light.data.database.entity.PlaceEntity
import com.nestoleh.light.domain.model.Place


fun PlaceEntity.toPlace(): Place {
    return Place(
        id = id,
        name = name
    )
}

fun List<PlaceEntity>.toPlaces(): List<Place> {
    return map { it.toPlace() }
}

fun Place.toPlaceEntity(): PlaceEntity {
    return PlaceEntity(
        id = id,
        name = name
    )
}