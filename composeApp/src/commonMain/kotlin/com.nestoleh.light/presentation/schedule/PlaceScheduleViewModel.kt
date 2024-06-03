package com.nestoleh.light.presentation.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nestoleh.light.domain.model.ElectricityStatusBlock
import com.nestoleh.light.domain.usecase.CalculateScheduleAsBlocksUseCase
import com.nestoleh.light.domain.usecase.GetPlaceUseCase
import com.nestoleh.light.util.watchFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration.Companion.seconds

class PlaceScheduleViewModel(
    private val placeId: String,
    private val getPlaceUseCase: GetPlaceUseCase,
    private val calculateScheduleAsBlocksUseCase: CalculateScheduleAsBlocksUseCase
) : ViewModel() {

    private val _state = combine(
        getPlaceUseCase(GetPlaceUseCase.Parameters(placeId)),
        watchFlow(1.seconds)
            .distinctUntilChangedBy { it.toLocalDateTime(TimeZone.currentSystemDefault()).minute }
            .map { currentTime(it) }
    ) { place, currentTime ->
        val weekBlocks = place?.schedule?.let {
            calculateScheduleAsBlocksUseCase.executeSync(it)
        } ?: emptyList()
        PlaceScheduleUIState(
            place = place,
            weekBlocks = weekBlocks,
            weekBlocksDayIndices = weekBlocks.daysIndices(),
            currentTime = currentTime
        )
    }

    val state = _state
        .stateIn(viewModelScope, SharingStarted.Eagerly, PlaceScheduleUIState())

    private fun List<List<ElectricityStatusBlock>>.daysIndices(): List<Int> {
        val syncedIndices = MutableList(7) { 0 }
        for (i in 0..(5.coerceAtMost(this.size - 2))) {
            syncedIndices[i + 1] = syncedIndices[i] + this[i].size + 1
        }
        return syncedIndices
    }

    private fun currentTime(instant: Instant): PlaceScheduleUIState.CurrentTimeState {
        val now = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        return PlaceScheduleUIState.CurrentTimeState(
            dayNumber = now.dayOfWeek.ordinal,
            hours = now.hour,
            minutes = now.minute
        )
    }
}