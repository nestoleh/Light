package com.nestoleh.light.presentation.place

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.nestoleh.light.core.domain.model.OperationError
import com.nestoleh.light.domain.usecase.CreatePlaceUseCase
import com.nestoleh.light.domain.usecase.SelectPlaceUseCase
import com.nestoleh.light.domain.validator.PlaceNameValidator
import com.nestoleh.light.domain.validator.ValidationResult
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class AddPlaceViewModel(
    private val createPlaceUseCase: CreatePlaceUseCase,
    private val placeNameValidator: PlaceNameValidator,
    private val selectedPlaceUseCase: SelectPlaceUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AddPlaceState())
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
                    placeName = name
                )
            ).catch {
                Logger.e(it) { "Ann error occurred when trying to add new place" }
                errorEventsChannel.send("An unexpected error happened, can't add new place")
                _state.value = _state.value.copy(
                    isSaving = false
                )
            }.flatMapConcat { place ->
                selectedPlaceUseCase(
                    SelectPlaceUseCase.Parameters(
                        placeId = place.id
                    )
                )
            }.collect {
                if (it is OperationError) {
                    Logger.e(it.throwable) { "An error occurred when trying to select new place" }
                }
                if (it.isTerminate) {
                    _state.value = _state.value.copy(
                        isSaving = false,
                        isSaved = true
                    )
                }
            }
        }
    }
}

data class AddPlaceState(
    val name: String = "",
    val nameError: String? = null,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false
)

sealed interface AddPlaceAction {
    data class NameChanged(val name: String) : AddPlaceAction
    data object Save : AddPlaceAction
}