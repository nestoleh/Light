package com.nestoleh.light.presentation.place.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.nestoleh.light.core.domain.model.OperationError
import com.nestoleh.light.domain.model.Place
import com.nestoleh.light.domain.model.Schedule
import com.nestoleh.light.domain.usecase.CreatePlaceUseCase
import com.nestoleh.light.domain.usecase.SelectPlaceUseCase
import com.nestoleh.light.domain.validator.PlaceNameValidator
import com.nestoleh.light.domain.validator.ValidationResult
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import light.composeapp.generated.resources.Res
import light.composeapp.generated.resources.add_new_place_error_unexpected
import org.jetbrains.compose.resources.getString

class AddPlaceViewModel(
    private val createPlaceUseCase: CreatePlaceUseCase,
    private val placeNameValidator: PlaceNameValidator,
    private val selectedPlaceUseCase: SelectPlaceUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AddPlaceUIState())
    val state = _state.asStateFlow()

    private val errorEventsChannel = Channel<String>()
    val errorEventsFlow = errorEventsChannel.receiveAsFlow()

    fun onAction(action: AddPlaceAction) {
        when (action) {
            is AddPlaceAction.NameChanged -> {
                _state.value = _state.value.copy(
                    name = action.name,
                    nameError = null
                )
            }

            AddPlaceAction.Save -> {
                viewModelScope.launch {
                    _state.value = _state.value.copy(
                        name = _state.value.name.trim()
                    )
                    when (val validationResult = placeNameValidator.validate(_state.value.name)) {
                        is ValidationResult.Error -> {
                            _state.value = _state.value.copy(
                                nameError = validationResult.message
                            )
                        }

                        ValidationResult.Valid -> {
                            viewModelScope.launch {
                                savePlace(_state.value.name)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun savePlace(name: String) {
        _state.value = _state.value.copy(
            isSaving = true
        )
        viewModelScope.launch {
            createPlaceUseCase(
                CreatePlaceUseCase.Parameters(
                    Place(
                        name = name,
                        schedule = Schedule()
                    )
                )
            ).catch {
                Logger.e(it) { "An error occurred when trying to add new place" }
                errorEventsChannel.send(getString(Res.string.add_new_place_error_unexpected))
                _state.value = _state.value.copy(
                    isSaving = false
                )
            }.flatMapLatest { place ->
                selectedPlaceUseCase(
                    SelectPlaceUseCase.Parameters(
                        placeId = place.id
                    )
                ).filter { it.isTerminate }
                    .onEach {
                        if (it is OperationError) {
                            Logger.e(it.throwable) { "An error occurred when trying to select new place" }
                        }
                    }
                    .map { place }
            }.collect { newPlace ->
                _state.value = _state.value.copy(
                    isSaving = false,
                    savedPlace = newPlace
                )
            }
        }
    }
}