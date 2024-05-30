package com.nestoleh.light.domain.usecase

import com.nestoleh.light.core.domain.usecase.OperationUseCase
import com.nestoleh.light.domain.ParametersKeys
import com.nestoleh.light.domain.repository.ParametersRepository
import com.nestoleh.light.domain.repository.PlaceRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext

class SelectPlaceUseCase(
    private val placeRepository: PlaceRepository,
    private val parametersRepository: ParametersRepository,
    private val dispatcher: CoroutineDispatcher
) : OperationUseCase<SelectPlaceUseCase.Parameters>() {

    override suspend fun runOperation(params: Parameters) = withContext(dispatcher) {
        val isPlaceExist = placeRepository.getPlaceAsFlow(params.placeId).firstOrNull() != null
        if (isPlaceExist) {
            parametersRepository.putString(ParametersKeys.SELECTED_PLACE_ID, params.placeId)
        } else {
            throw IllegalArgumentException("Place with id ${params.placeId} does not exist")
        }
    }

    data class Parameters(
        val placeId: String
    )
}