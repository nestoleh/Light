package com.nestoleh.light.domain.usecase

import com.nestoleh.light.core.domain.usecase.ResultUseCase
import com.nestoleh.light.domain.repository.PlaceRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class IsPlaceWithNameExistUseCase(
    private val placeRepository: PlaceRepository,
    private val dispatcher: CoroutineDispatcher
) : ResultUseCase<IsPlaceWithNameExistUseCase.Parameters, Boolean>() {

    override suspend fun doWork(params: Parameters): Boolean = withContext(dispatcher) {
        placeRepository.getAllPlacesWithName(params.placeName).isNotEmpty()
    }

    data class Parameters(
        val placeName: String
    )
}