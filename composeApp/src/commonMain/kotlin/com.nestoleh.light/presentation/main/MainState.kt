package com.nestoleh.light.presentation.main

import com.nestoleh.light.domain.model.Place

data class MainUIState(
    val selectedPlace: Place? = null,
    val allPlaces: List<Place> = emptyList(),
)

sealed interface MainAction {
    data class SelectPlace(val place: Place) : MainAction
}