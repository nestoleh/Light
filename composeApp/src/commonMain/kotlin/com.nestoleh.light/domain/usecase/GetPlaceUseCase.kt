package com.nestoleh.light.domain.usecase

import com.nestoleh.light.core.domain.usecase.FlowUseCase
import com.nestoleh.light.domain.model.Place
import com.nestoleh.light.domain.repository.PlaceRepository
import kotlinx.coroutines.flow.Flow

class GetPlaceUseCase(
    private val placeRepository: PlaceRepository
) : FlowUseCase<GetPlaceUseCase.Parameters, Place?>() {

    override fun doWork(params: Parameters): Flow<Place?> {
        return placeRepository
            .getPlaceAsFlow(params.id)
    }

    data class Parameters(
        val id: String
    )
}