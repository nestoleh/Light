package com.nestoleh.light.domain.usecase

import com.nestoleh.light.core.domain.usecase.FlowUseCase
import com.nestoleh.light.domain.ParametersKeys
import com.nestoleh.light.domain.model.Place
import com.nestoleh.light.domain.repository.ParametersRepository
import com.nestoleh.light.domain.repository.PlaceRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class GetSelectedPlaceUseCase(
    private val placeRepository: PlaceRepository,
    private val parametersRepository: ParametersRepository,
    private val dispatcher: CoroutineDispatcher
) : FlowUseCase<Unit, Place?>() {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun doWork(params: Unit): Flow<Place?> {
        return parametersRepository.getIntAsFlow(ParametersKeys.SELECTED_PLACE_ID)
            .flatMapLatest { selectedPlaceId ->
                if (selectedPlaceId == null) {
                    getFirstPlaceOrNull()
                } else {
                    placeRepository.getPlaceAsFlow(selectedPlaceId)
                        .flatMapLatest { place ->
                            if (place == null) {
                                getFirstPlaceOrNull()
                            } else {
                                flowOf(place)
                            }
                        }
                }
            }
            .flowOn(dispatcher)
    }

    private fun getFirstPlaceOrNull(): Flow<Place?> {
        return placeRepository.getAllPlacesAsFlow()
            .map { it.firstOrNull() }
    }
}