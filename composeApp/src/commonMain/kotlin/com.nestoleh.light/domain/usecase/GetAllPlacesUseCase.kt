package com.nestoleh.light.domain.usecase

import com.nestoleh.light.core.domain.usecase.FlowUseCase
import com.nestoleh.light.domain.model.Place
import com.nestoleh.light.domain.repository.PlaceRepository
import kotlinx.coroutines.flow.Flow


class GetAllPlacesUseCase(
    private val placeRepository: PlaceRepository
) : FlowUseCase<Unit, List<Place>>() {

    override fun doWork(params: Unit): Flow<List<Place>> {
        return placeRepository
            .getAllPlacesAsFlow()
    }
}