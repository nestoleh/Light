package com.nestoleh.light.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.nestoleh.light.core.domain.model.OperationError
import com.nestoleh.light.core.domain.model.OperationStarted
import com.nestoleh.light.core.domain.model.OperationSuccess
import com.nestoleh.light.core.domain.usecase.invoke
import com.nestoleh.light.domain.model.ElectricityStatusBlock
import com.nestoleh.light.domain.model.Place
import com.nestoleh.light.domain.usecase.CalculateNearestElectricityPeriods2UseCase
import com.nestoleh.light.domain.usecase.GetAllPlacesUseCase
import com.nestoleh.light.domain.usecase.GetSelectedPlaceUseCase
import com.nestoleh.light.domain.usecase.SelectPlaceUseCase
import com.nestoleh.light.util.nextElementIndexes
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow

class MainViewModel(
    getAllPlacesUseCase: GetAllPlacesUseCase,
    getSelectedPlaceUseCase: GetSelectedPlaceUseCase,
    private val selectPlaceUseCase: SelectPlaceUseCase,
    private val calculateNearestElectricityPeriodsUseCase: CalculateNearestElectricityPeriods2UseCase
) : ViewModel() {

    private val _state = MutableStateFlow(MainUIState())
    val state = _state.asStateFlow()

    private val errorEventsChannel = Channel<String>()
    val errorEventsFlow = errorEventsChannel.receiveAsFlow()

    init {
        getSelectedPlaceUseCase.invoke()
            .flatMapLatest { place ->
                Logger.d{ "Block = new place"}
                if (place == null) {
                    flowOf(SelectedPlaceState.None)
                } else {
                    calculateNearestElectricityPeriodsUseCase(place.schedule)
                        .map {
                            Logger.d{ "Block = $it"}
                            if (it == null) {
                                errorEventsChannel.send(" Unexpected error occurred, please check you schedule")
                                Logger.e { "Calculating nearest electricity periods failed" }
                            }
                            SelectedPlaceState.Selected(
                                place = place,
                                currentPeriod = it?.current,
                                futurePeriods = it?.future ?: emptyList()
                            )
                        }
                }
            }
            .onEach { place ->
                _state.value = _state.value.copy(
                    selectedPlaceState = place
                )
            }
            .launchIn(viewModelScope)

        getAllPlacesUseCase()
            .onEach { _state.value = _state.value.copy(allPlaces = it) }
            .launchIn(viewModelScope)
    }

    fun onAction(action: MainAction) {
        when (action) {
            is MainAction.SelectPlace -> {
                selectPlace(action.place)
            }
        }
    }

    private fun selectPlace(place: Place) {
        selectPlaceUseCase(SelectPlaceUseCase.Parameters(place.id))
            .onEach {
                when (it) {
                    is OperationError -> {
                        errorEventsChannel.send("An error occurred while selecting the place, please try again")
                        Logger.e(it.throwable) { "Selecting place id = ${place.id} failed" }
                    }

                    OperationStarted -> {
                        Logger.d { "Selecting place id = ${place.id} started" }
                    }

                    OperationSuccess -> {
                        Logger.d { "Place id = ${place.id} selected" }
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    private fun List<List<ElectricityStatusBlock>>.findNextBlockIndexesWithDifferentStatus(
        startI: Int,
        startJ: Int
    ): Pair<Int, Int> {
        if ((startI < 0 || startI >= this.size) || (startJ < 0 || startJ >= this[startI].size)) {
            return Pair(-1, -1)
        }
        val startStatus = this[startI][startJ].status
        var (nextI, nextJ) = nextElementIndexes(startI, startJ)
        while (nextI >= 0 && nextJ >= 0 && (nextI != startI || nextJ != startJ)) {
            if (this[nextI][nextJ].status != startStatus) {
                return Pair(nextI, nextJ)
            }
            val (i, j) = nextElementIndexes(nextI, nextJ)
            nextI = i
            nextJ = j
        }
        return Pair(-1, -1)
    }
}

