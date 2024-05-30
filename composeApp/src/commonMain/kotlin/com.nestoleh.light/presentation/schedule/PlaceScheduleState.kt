package com.nestoleh.light.presentation.schedule

import com.nestoleh.light.domain.model.ElectricityStatusBlock
import com.nestoleh.light.domain.model.Place

data class PlaceScheduleUIState(
    val place: Place? = null,
    val weekBlocks: List<List<ElectricityStatusBlock>> = emptyList(),
    val weekBlocksDayIndices: List<Int> = List(7) { 0 }
)