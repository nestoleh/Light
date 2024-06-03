package com.nestoleh.light.presentation.schedule

import com.nestoleh.light.domain.model.ElectricityStatusBlock
import com.nestoleh.light.domain.model.Place

data class PlaceScheduleUIState(
    val place: Place? = null,
    val weekBlocks: List<List<ElectricityStatusBlock>> = emptyList(),
    val weekBlocksDayIndices: List<Int> = List(7) { 0 },
    val currentTime: CurrentTimeState = CurrentTimeState(),
) {

    fun countCurrentBlockIndex(): Pair<Int, ElectricityStatusBlock?> {
        var index = weekBlocks
            .take(currentTime.dayNumber)
            .sumOf { it.size + 1 }
        var block: ElectricityStatusBlock? = null
        weekBlocks.getOrNull(currentTime.dayNumber)?.let {
            val hourFloat = currentTime.hours + currentTime.minutes / 60f
            val blocks = it.takeWhile { block -> hourFloat >= block.hourStart.toFloat() }
            block = blocks.lastOrNull()
            index += blocks.size + 1
        }
        return Pair(index, block)
    }

    data class CurrentTimeState(
        val dayNumber: Int = 0,
        val hours: Int = 0,
        val minutes: Int = 0,
    ) {
        val timeAsFloat: Float
            get() = hours + minutes / 60f
    }

}