package com.nestoleh.light.data.converters

import com.nestoleh.light.data.database.entity.PlaceEntity
import com.nestoleh.light.data.database.entity.PlaceWithScheduleEntity
import com.nestoleh.light.domain.model.Place


fun PlaceWithScheduleEntity.toPlace(): Place {
    return Place(
        id = this.place.id,
        name = this.place.name,
        schedule = this.schedule.toSchedule()
    )
}

fun List<PlaceWithScheduleEntity>.toPlaces(): List<Place> {
    return map { it.toPlace() }
}

fun Place.toPlaceEntity(): PlaceEntity {
    return PlaceEntity(
        id = id,
        name = name
    )
}