package com.nestoleh.light.domain.usecase

import com.nestoleh.light.core.domain.usecase.FlowUseCase
import com.nestoleh.light.data.database.dao.PlaceDao
import com.nestoleh.light.domain.converters.toPlace
import com.nestoleh.light.domain.model.Place
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetPlaceUseCase(
    private val placeDao: PlaceDao
) : FlowUseCase<GetPlaceUseCase.Parameters, Place?>() {

    override fun doWork(params: Parameters): Flow<Place?> {
        return placeDao
            .getPlaceFlow(params.id)
            .map { it?.toPlace() }
    }

    data class Parameters(
        val id: Int
    )
}