package com.nestoleh.light.presentation.place.add

import com.nestoleh.light.domain.model.Place

data class AddPlaceUIState(
    val name: String = "",
    val nameError: String? = null,
    val isSaving: Boolean = false,
    val savedPlace: Place? = null,
)

sealed interface AddPlaceAction {
    data class NameChanged(val name: String) : AddPlaceAction
    data object Save : AddPlaceAction
}