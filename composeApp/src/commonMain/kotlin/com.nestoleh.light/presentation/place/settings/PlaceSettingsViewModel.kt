package com.nestoleh.light.presentation.place.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.nestoleh.light.core.domain.model.OperationError
import com.nestoleh.light.core.domain.model.OperationStarted
import com.nestoleh.light.core.domain.model.OperationSuccess
import com.nestoleh.light.domain.usecase.DeletePlaceUseCase
import com.nestoleh.light.domain.usecase.GetPlaceUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow

class PlaceSettingsViewModel(
    private val placeId: Int,
    private val getPlaceUseCase: GetPlaceUseCase,
    private val deletePlaceUseCase: DeletePlaceUseCase
) : ViewModel() {

    private val errorEventsChannel = Channel<String>()
    val errorEventsFlow = errorEventsChannel.receiveAsFlow()

    private val _state = MutableStateFlow(PlaceSettingsUIState())
    val state = _state.asStateFlow()

    init {
        getPlaceUseCase(GetPlaceUseCase.Parameters(placeId))
            .onEach { _state.value = _state.value.copy(place = it) }
            .launchIn(viewModelScope)
    }

    fun onAction(event: PlaceSettingsAction) {
        when (event) {
            is PlaceSettingsAction.DeletePlace -> {
                deletePlace()
            }
        }
    }

    private fun deletePlace() {
        val place = state.value.place
        if (place != null) {
            deletePlaceUseCase(DeletePlaceUseCase.Parameters(id = place.id))
                .onEach { result ->
                    when (result) {
                        is OperationError -> {
                            errorEventsChannel.send("An error occurred while deleting the place, please try again")
                        }

                        is OperationSuccess -> {
                            _state.value = _state.value.copy(isDeleted = true)
                        }

                        OperationStarted -> {
                            Logger.d("Delete place ${place.id} operation started")
                        }
                    }
                }
                .launchIn(viewModelScope)
        }
    }

}