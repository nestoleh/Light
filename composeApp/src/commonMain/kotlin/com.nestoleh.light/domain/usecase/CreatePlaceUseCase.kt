package com.nestoleh.light.domain.usecase

import com.nestoleh.light.core.domain.usecase.ResultUseCase
import com.nestoleh.light.data.database.dao.PlaceDao
import com.nestoleh.light.data.database.entity.PlaceEntity
import com.nestoleh.light.domain.converters.toPlace
import com.nestoleh.light.domain.model.Place
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class CreatePlaceUseCase(
    private val placeDao: PlaceDao,
    private val dispatcher: CoroutineDispatcher
) : ResultUseCase<CreatePlaceUseCase.Parameters, Place>() {

    override suspend fun doWork(params: Parameters): Place = withContext(dispatcher) {
        placeDao.insert(
            PlaceEntity(name = params.placeName)
        ).toPlace()
    }

    data class Parameters(
        val placeName: String
    )
}