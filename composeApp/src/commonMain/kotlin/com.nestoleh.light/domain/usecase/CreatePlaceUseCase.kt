package com.nestoleh.light.domain.usecase

import com.nestoleh.light.data.database.dao.PlaceDao
import com.nestoleh.light.data.database.entity.PlaceEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class CreatePlaceUseCase(
    private val placeDao: PlaceDao,
    private val dispatcher: CoroutineDispatcher
) : OperationUseCase<CreatePlaceUseCase.Parameters>() {

    override suspend fun runOperation(params: Parameters) = withContext(dispatcher) {
        placeDao.upsert(
            PlaceEntity(
                name = params.placeName
            )
        )
    }

    data class Parameters(
        val placeName: String
    )
}