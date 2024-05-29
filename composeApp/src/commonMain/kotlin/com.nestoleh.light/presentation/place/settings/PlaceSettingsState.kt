package com.nestoleh.light.presentation.place.settings

import com.nestoleh.light.domain.model.Place

data class PlaceSettingsUIState(
    val place: Place? = null,
    val isDeleted: Boolean = false,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false
)

sealed interface PlaceSettingsAction {
    data object DeletePlace : PlaceSettingsAction
    data object Save : PlaceSettingsAction
    data class ToggleSchedule(val day: Int, val hour: Int) : PlaceSettingsAction
}