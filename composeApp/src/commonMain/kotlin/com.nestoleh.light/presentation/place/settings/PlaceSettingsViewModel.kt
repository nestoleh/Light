package com.nestoleh.light.presentation.place.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.nestoleh.light.core.domain.model.OperationError
import com.nestoleh.light.core.domain.model.OperationStarted
import com.nestoleh.light.core.domain.model.OperationSuccess
import com.nestoleh.light.domain.model.ElectricityStatus
import com.nestoleh.light.domain.model.Schedule
import com.nestoleh.light.domain.usecase.DeletePlaceUseCase
import com.nestoleh.light.domain.usecase.GetPlaceUseCase
import com.nestoleh.light.domain.usecase.UpdatePlaceUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch

class PlaceSettingsViewModel(
    private val placeId: Int,
    private val getPlaceUseCase: GetPlaceUseCase,
    private val deletePlaceUseCase: DeletePlaceUseCase,
    private val updatePlaceUseCase: UpdatePlaceUseCase
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

            is PlaceSettingsAction.ToggleSchedule -> {
                toggleSchedule(event.day, event.hour)
            }

            PlaceSettingsAction.Save -> {
                save()
            }
        }
    }

    private fun save() {
        state.value.place?.let { place ->
            viewModelScope.launch {
                _state.value = _state.value.copy(isSaving = true)
                updatePlaceUseCase(UpdatePlaceUseCase.Parameters(place = place))
                    .take(1)
                    .catch {
                        Logger.e(it) { "An error occurred while saving the place" }
                        errorEventsChannel.send("An error occurred while saving the place, please try again")
                        _state.value = _state.value.copy(isSaving = false)
                    }
                    .onEach {
                        _state.value = _state.value.copy(
                            place = it,
                            isSaving = false,
                            isSaved = true
                        )
                    }
                    .launchIn(viewModelScope)
            }
        }
    }

    private fun toggleSchedule(day: Int, hour: Int) {
        state.value.place?.schedule?.let { schedule ->
            val currentState = schedule.weekSchedule[day][hour]
            val nextState = when (currentState) {
                ElectricityStatus.On -> ElectricityStatus.PossibleOff
                ElectricityStatus.PossibleOff -> ElectricityStatus.Off
                ElectricityStatus.Off -> ElectricityStatus.On
            }
            schedule.weekSchedule[day][hour] = nextState
            _state.value = _state.value.copy(
                place = state.value.place?.copy(
                    schedule = Schedule(schedule.weekSchedule)
                )
            )
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