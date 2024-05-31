package com.nestoleh.light.presentation.main

import com.nestoleh.light.domain.model.ElectricityStatusBlock
import com.nestoleh.light.domain.model.Place

data class MainUIState(
    val selectedPlaceState: SelectedPlaceState = SelectedPlaceState.None,
    val allPlaces: List<Place> = emptyList(),
)

sealed interface SelectedPlaceState {
    data object None : SelectedPlaceState
    data class Selected(
        val place: Place,
        val currentBlock: ElectricityStatusBlock?,
        val next1Block: ElectricityStatusBlock?,
        val next2Block: ElectricityStatusBlock?,
    ) : SelectedPlaceState
}

sealed interface MainAction {
    data class SelectPlace(val place: Place) : MainAction
}