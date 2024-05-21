package com.nestoleh.light.domain.usecase

import com.nestoleh.light.data.database.dao.PlaceDao
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class IsPlaceWithNameExistUseCase(
    private val placeDao: PlaceDao,
    private val dispatcher: CoroutineDispatcher
) : ResultUseCase<IsPlaceWithNameExistUseCase.Parameters, Boolean>() {

    override suspend fun doWork(params: Parameters): Boolean = withContext(dispatcher) {
        placeDao.getAllPlacesWithName(params.placeName).isNotEmpty()
    }

    data class Parameters(
        val placeName: String
    )
}