package com.nestoleh.light.domain.usecase

import com.nestoleh.light.core.domain.usecase.FlowUseCase
import com.nestoleh.light.data.database.dao.ParametersDao
import com.nestoleh.light.data.database.dao.PlaceDao
import com.nestoleh.light.data.database.entity.PlaceEntity
import com.nestoleh.light.domain.ParametersKeys
import com.nestoleh.light.domain.converters.toPlace
import com.nestoleh.light.domain.model.Place
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class GetSelectedPlaceUseCase(
    private val placeDao: PlaceDao,
    private val parametersDao: ParametersDao,
    private val dispatcher: CoroutineDispatcher
) : FlowUseCase<Unit, Place?>() {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun doWork(params: Unit): Flow<Place?> {
        return parametersDao.getParameter(ParametersKeys.SELECTED_PLACE_ID)
            .map { it?.value?.toIntOrNull() }
            .flatMapLatest { selectedPlaceId ->
                if (selectedPlaceId == null) {
                    getFirstPlaceOrNull()
                } else {
                    placeDao.getPlaceFlow(selectedPlaceId)
                        .flatMapLatest { placeEntity: PlaceEntity? ->
                            if (placeEntity == null) {
                                getFirstPlaceOrNull()
                            } else {
                                flowOf(placeEntity.toPlace())
                            }
                        }
                }
            }
            .flowOn(dispatcher)
    }

    private fun getFirstPlaceOrNull(): Flow<Place?> {
        return placeDao.getAllPlacesFlow()
            .map { it.firstOrNull()?.toPlace() }
    }
}