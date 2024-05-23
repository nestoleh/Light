package com.nestoleh.light.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.nestoleh.light.core.domain.model.OperationError
import com.nestoleh.light.core.domain.model.OperationStarted
import com.nestoleh.light.core.domain.model.OperationSuccess
import com.nestoleh.light.core.domain.usecase.invoke
import com.nestoleh.light.domain.model.Place
import com.nestoleh.light.domain.usecase.GetAllPlacesUseCase
import com.nestoleh.light.domain.usecase.GetSelectedPlaceUseCase
import com.nestoleh.light.domain.usecase.SelectPlaceUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow

class MainViewModel(
    getAllPlacesUseCase: GetAllPlacesUseCase,
    getSelectedPlaceUseCase: GetSelectedPlaceUseCase,
    private val selectPlaceUseCase: SelectPlaceUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(MainUIState())
    val state = _state.asStateFlow()

    private val errorEventsChannel = Channel<String>()
    val errorEventsFlow = errorEventsChannel.receiveAsFlow()

    init {
        getSelectedPlaceUseCase()
            .onEach { _state.value = _state.value.copy(selectedPlace = it) }
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
}