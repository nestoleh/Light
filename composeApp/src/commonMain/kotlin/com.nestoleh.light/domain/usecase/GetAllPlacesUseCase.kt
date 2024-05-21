package com.nestoleh.light.domain.usecase

import com.nestoleh.light.data.database.dao.PlaceDao
import com.nestoleh.light.data.database.entity.PlaceEntity
import com.nestoleh.light.domain.model.Place
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class GetAllPlacesUseCase(
    private val placeDao: PlaceDao
) : FlowUseCase<Unit, List<Place>>() {

    override fun doWork(params: Unit): Flow<List<Place>> {
        return placeDao
            .getAllPlacesFlow()
            .map { places -> places.map { it.toPlace() } }
    }

    private fun PlaceEntity.toPlace(): Place {
        return Place(
            id = id,
            name = name
        )
    }
}