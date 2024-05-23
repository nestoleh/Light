package com.nestoleh.light.domain.usecase

import com.nestoleh.light.core.domain.usecase.OperationUseCase
import com.nestoleh.light.data.database.dao.ParametersDao
import com.nestoleh.light.data.database.dao.PlaceDao
import com.nestoleh.light.domain.ParametersKeys
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class DeletePlaceUseCase(
    private val placeDao: PlaceDao,
    private val parametersDao: ParametersDao,
    private val dispatcher: CoroutineDispatcher
) : OperationUseCase<DeletePlaceUseCase.Parameters>() {

    override suspend fun runOperation(params: Parameters) = withContext(dispatcher) {
        placeDao.delete(params.id)
        val selectedPlaceId = parametersDao.getParameterValue(ParametersKeys.SELECTED_PLACE_ID)?.value?.toIntOrNull()
        if (selectedPlaceId == params.id) {
            parametersDao.deleteParameter(ParametersKeys.SELECTED_PLACE_ID)
        }
    }

    data class Parameters(
        val id: Int
    )
}