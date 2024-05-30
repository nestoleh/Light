package com.nestoleh.light.domain.usecase

import com.nestoleh.light.core.domain.usecase.ResultUseCase
import com.nestoleh.light.data.converters.toPlace
import com.nestoleh.light.data.database.entity.PlaceEntity
import com.nestoleh.light.domain.model.Place
import com.nestoleh.light.domain.repository.PlaceRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class UpdatePlaceUseCase(
    private val placeRepository: PlaceRepository,
    private val dispatcher: CoroutineDispatcher
) : ResultUseCase<UpdatePlaceUseCase.Parameters, Place>() {

    override suspend fun doWork(params: Parameters): Place = withContext(dispatcher) {
        placeRepository.updatePlace(params.place)
    }

    data class Parameters(
        val place: Place
    )
}