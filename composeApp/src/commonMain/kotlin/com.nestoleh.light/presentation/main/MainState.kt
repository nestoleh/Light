package com.nestoleh.light.presentation.main

import com.nestoleh.light.domain.model.ElectricityStatusPeriod
import com.nestoleh.light.domain.model.Place

data class MainUIState(
    val selectedPlaceState: SelectedPlaceState = SelectedPlaceState.None,
    val allPlaces: List<Place> = emptyList(),
)

sealed interface SelectedPlaceState {
    data object None : SelectedPlaceState
    data class Selected(
        val place: Place,
        val currentPeriod: ElectricityStatusPeriod?,
        val futurePeriods: List<ElectricityStatusPeriod.Limited>,
    ) : SelectedPlaceState
}

sealed interface MainAction {
    data class SelectPlace(val place: Place) : MainAction
}