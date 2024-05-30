package com.nestoleh.light.domain.usecase

import com.nestoleh.light.core.domain.usecase.OperationUseCase
import com.nestoleh.light.domain.ParametersKeys
import com.nestoleh.light.domain.repository.ParametersRepository
import com.nestoleh.light.domain.repository.PlaceRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class DeletePlaceUseCase(
    private val placeRepository: PlaceRepository,
    private val parametersRepository: ParametersRepository,
    private val dispatcher: CoroutineDispatcher
) : OperationUseCase<DeletePlaceUseCase.Parameters>() {

    override suspend fun runOperation(params: Parameters) = withContext(dispatcher) {
        placeRepository.deletePlace(params.id)
        val selectedPlaceId = parametersRepository.getString(ParametersKeys.SELECTED_PLACE_ID)
        if (selectedPlaceId == params.id) {
            parametersRepository.deleteValue(ParametersKeys.SELECTED_PLACE_ID)
        }
    }

    data class Parameters(
        val id: String
    )
}