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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn

class MainViewModel(
    private val getAllPlacesUseCase: GetAllPlacesUseCase,
    private val getSelectedPlaceUseCase: GetSelectedPlaceUseCase,
    private val selectPlaceUseCase: SelectPlaceUseCase
) : ViewModel() {
    val currentPlaceFlow: StateFlow<Place?> = getSelectedPlaceUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = null
        )

    val placesFlow: StateFlow<List<Place>> = getAllPlacesUseCase()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun selectPlace(place: Place) {
        selectPlaceUseCase(SelectPlaceUseCase.Parameters(place.id))
            .onEach {
                when (it) {
                    is OperationError -> {
                        // TODO: show error message
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