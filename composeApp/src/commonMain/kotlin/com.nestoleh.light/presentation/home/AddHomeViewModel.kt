package com.nestoleh.light.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nestoleh.light.domain.validator.HomeNameValidator
import com.nestoleh.light.domain.validator.ValidationResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddHomeViewModel : ViewModel() {
    private val _state = MutableStateFlow(AddHomeState())
    val state = _state.asStateFlow()

    fun onAction(action: AddHomeAction) {
        when (action) {
            is AddHomeAction.NameChanged -> {
                _state.value = _state.value.copy(
                    name = action.name,
                    nameError = null
                )
            }

            AddHomeAction.Save -> {
                viewModelScope.launch {
                    when (val validationResult = HomeNameValidator().validate(_state.value.name)) {
                        is ValidationResult.Error -> {
                            _state.value = _state.value.copy(
                                nameError = validationResult.message
                            )
                        }

                        ValidationResult.Valid -> {
                            viewModelScope.launch {
                                _state.value = _state.value.copy(
                                    isSaving = true
                                )
                                // TODO: save home
                                delay(1000)
                                _state.value = _state.value.copy(
                                    isSaving = false
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

data class AddHomeState(
    val name: String = "",
    val nameError: String? = null,
    val isSaving: Boolean = false
) {
    val isNameError: Boolean
        get() = !nameError.isNullOrEmpty()
}

sealed interface AddHomeAction {
    data class NameChanged(val name: String) : AddHomeAction
    data object Save : AddHomeAction
}