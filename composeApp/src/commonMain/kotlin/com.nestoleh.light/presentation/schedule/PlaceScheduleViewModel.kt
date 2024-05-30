package com.nestoleh.light.presentation.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nestoleh.light.domain.model.ElectricityStatusBlock
import com.nestoleh.light.domain.model.Schedule
import com.nestoleh.light.domain.usecase.GetPlaceUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class PlaceScheduleViewModel(
    private val placeId: String,
    private val getPlaceUseCase: GetPlaceUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(PlaceScheduleUIState())
    val state = _state.asStateFlow()

    init {
        getPlaceUseCase(GetPlaceUseCase.Parameters(placeId))
            .onEach {
                val weekBlocks = it?.schedule?.asBlocks() ?: emptyList()
                _state.value = _state.value.copy(
                    place = it,
                    weekBlocks = weekBlocks,
                    weekBlocksDayIndices = weekBlocks.daysIndices()
                )
            }
            .launchIn(viewModelScope)
    }

    private fun List<List<ElectricityStatusBlock>>.daysIndices(): List<Int> {
        val syncedIndices = MutableList(7) { 0 }
        for (i in 0..(5.coerceAtMost(this.size - 2))) {
            syncedIndices[i + 1] = syncedIndices[i] + this[i].size + 1
        }
        return syncedIndices
    }

    private fun Schedule.asBlocks(): List<List<ElectricityStatusBlock>> {
        return weekSchedule.map { daySchedule ->
            val dayBlocks = mutableListOf<ElectricityStatusBlock>()
            if (daySchedule.isNotEmpty()) {
                var blockStart = 0
                var blockEnd = 1
                var blockStatus = daySchedule.first()
                for (i in 1 until daySchedule.size) {
                    val status = daySchedule[i]
                    if (status == blockStatus) {
                        blockEnd = i + 1
                    } else {
                        dayBlocks.add(
                            ElectricityStatusBlock(
                                hourStart = blockStart,
                                hourEnd = blockEnd,
                                status = blockStatus
                            )
                        )
                        blockStart = i
                        blockEnd = i + 1
                        blockStatus = status
                    }
                }
                dayBlocks.add(
                    ElectricityStatusBlock(
                        hourStart = blockStart,
                        hourEnd = blockEnd,
                        status = blockStatus
                    )
                )
            }
            dayBlocks
        }
    }
}