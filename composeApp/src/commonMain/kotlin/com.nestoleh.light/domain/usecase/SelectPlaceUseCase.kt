package com.nestoleh.light.domain.usecase

import com.nestoleh.light.core.domain.usecase.OperationUseCase
import com.nestoleh.light.data.database.dao.ParametersDao
import com.nestoleh.light.data.database.dao.PlaceDao
import com.nestoleh.light.data.database.entity.ParameterEntity
import com.nestoleh.light.domain.ParametersKeys
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext

class SelectPlaceUseCase(
    private val placeDao: PlaceDao,
    private val parametersDao: ParametersDao,
    private val dispatcher: CoroutineDispatcher
) : OperationUseCase<SelectPlaceUseCase.Parameters>() {

    override suspend fun runOperation(params: Parameters) = withContext(dispatcher) {
        val isPlaceExist = placeDao.getPlaceFlow(params.placeId).firstOrNull() != null
        if (isPlaceExist) {
            parametersDao.upsertParameter(
                ParameterEntity(
                    key = ParametersKeys.SELECTED_PLACE_ID,
                    value = params.placeId.toString()
                )
            )
        } else {
            throw IllegalArgumentException("Place with id ${params.placeId} does not exist")
        }
    }

    data class Parameters(
        val placeId: Int
    )
}