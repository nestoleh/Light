package com.nestoleh.light.presentation.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.nestoleh.light.domain.model.ElectricityStatusBlock
import com.nestoleh.light.domain.model.Schedule
import com.nestoleh.light.domain.usecase.GetPlaceUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration.Companion.seconds

class PlaceScheduleViewModel(
    private val placeId: String,
    private val getPlaceUseCase: GetPlaceUseCase
) : ViewModel() {

    private val _state = combine(
        getPlaceUseCase(GetPlaceUseCase.Parameters(placeId)),
        flow {
            while (true) {
                emit(currentTime())
                delay(1.seconds)
            }
        }
    ) { place, currentTime ->
        val weekBlocks = place?.schedule?.asBlocks() ?: emptyList()
        PlaceScheduleUIState(
            place = place,
            weekBlocks = weekBlocks,
            weekBlocksDayIndices = weekBlocks.daysIndices(),
            currentTime = currentTime
        )
    }
    val state = _state
        .onEach {
            Logger.d { "PlaceScheduleViewModel state: $it" }
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, PlaceScheduleUIState())

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

    private fun currentTime(): PlaceScheduleUIState.CurrentTimeState {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        return PlaceScheduleUIState.CurrentTimeState(
            dayNumber = now.dayOfWeek.ordinal,
            hours = now.hour,
            minutes = now.minute
        )
    }
}