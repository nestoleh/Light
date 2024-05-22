package com.nestoleh.light.domain.usecase

import com.nestoleh.light.core.domain.usecase.FlowUseCase
import com.nestoleh.light.data.database.dao.PlaceDao
import com.nestoleh.light.domain.converters.toPlace
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
}