package com.nestoleh.light.domain.usecase

import co.touchlab.kermit.Logger
import com.nestoleh.light.core.domain.usecase.FlowUseCase
import com.nestoleh.light.domain.model.ElectricityStatus
import com.nestoleh.light.domain.model.ElectricityStatusPeriod
import com.nestoleh.light.domain.model.NearestElectricityPeriods
import com.nestoleh.light.domain.model.Schedule
import com.nestoleh.light.util.watchFlow
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.seconds

class CalculateNearestElectricityPeriodsUseCase(
    private val dispatcher: CoroutineDispatcher
) : FlowUseCase<Schedule, NearestElectricityPeriods?>() {

    override fun doWork(params: Schedule): Flow<NearestElectricityPeriods?> {
        return flowOf(params.toIndependentElectricityStatusBlocks())
            .flatMapLatest { blocks ->
                Logger.d { "Original blocks -> [${blocks.size}] --> $blocks" }
                if (blocks.size == 1) {
                    flowOf(
                        NearestElectricityPeriods(
                            current = ElectricityStatusPeriod.Unlimited(
                                status = blocks.first().status
                            ),
                            future = emptyList()
                        )
                    )
                } else {
                    watchFlow(1.seconds)
                        .distinctUntilChangedBy { it.toLocalDateTime(TimeZone.currentSystemDefault()).minute }
                        .map { now: Instant ->
                            val localNow = now.toLocalDateTime(TimeZone.currentSystemDefault())
                            val currentDay = localNow.dayOfWeek.ordinal
                            val currentHour = localNow.hour
                            val currentBlockIndex = blocks.findBlockIndexFor(currentDay, currentHour)
                            val reformattedBlocks = blocks.reformatStartingFrom(currentBlockIndex)
                            Logger.d { "Reformatted blocks -> [${reformattedBlocks.size}] --> $reformattedBlocks" }
                            NearestElectricityPeriods(
                                current = reformattedBlocks[0].toElectricityStatusPeriod(now),
                                future = reformattedBlocks.subList(1, 3).map { it.toElectricityStatusPeriod(now) }
                            )
                        }
                }
            }
            .flowOn(dispatcher)
    }

    private fun IndependentElectricityStatusBlock.toElectricityStatusPeriod(
        now: Instant
    ): ElectricityStatusPeriod.Limited {
        return ElectricityStatusPeriod.Limited(
            periodStart = (now + this.dayStart.days)
                .toLocalDateTime(TimeZone.currentSystemDefault())
                .setupWithHour(this.hourStart),
            periodEnd = (now + this.dayEnd.days)
                .toLocalDateTime(TimeZone.currentSystemDefault())
                .setupWithHour(this.hourEnd),
            status = this.status
        )
    }

    private fun LocalDateTime.setupWithHour(hour: Int): LocalDateTime {
        val localTime = if (hour == 24) {
            LocalTime(23, 59, 59)
        } else {
            LocalTime(hour, 0)
        }
        return this.date.atTime(localTime)
    }

    private fun List<IndependentElectricityStatusBlock>.reformatStartingFrom(
        index: Int
    ): List<IndependentElectricityStatusBlock> {
        return if (index == 0) {
            val first = this.first()
            val last = this.last()
            if (last.status == first.status) {
                this.toMutableList().apply {
                    set(
                        index = 0,
                        element = first.copy(
                            hourStart = last.hourStart,
                            dayStart = first.dayStart - (7 - last.dayStart)
                        )
                    )
                }
            } else {
                this
            }
        } else {
            val stickEndWithStart = index != 0 && this.last().status == this.first().status
            val daysShift = this[index].dayStart
            val newList = mutableListOf<IndependentElectricityStatusBlock>()
            newList.addAll(this.subList(index, if (stickEndWithStart) this.size - 1 else this.size))
            if (stickEndWithStart) {
                val first = this.first()
                val last = this.last()
                newList.add(
                    last.copy(
                        dayEnd = last.dayEnd + first.dayEnd + 1,
                        hourEnd = first.hourEnd
                    )
                )
            }
            newList.addAll(
                this.subList(if (stickEndWithStart) 1 else 0, index)
                    .map { block ->
                        block.copy(
                            dayStart = block.dayStart + 7,
                            dayEnd = block.dayEnd + 7
                        )
                    })
            newList.map {
                it.copy(
                    dayStart = it.dayStart - daysShift,
                    dayEnd = it.dayEnd - daysShift
                )
            }
        }
    }

    private fun List<IndependentElectricityStatusBlock>.findBlockIndexFor(day: Int, hour: Int): Int {
        return this.indexOfFirst { block ->
            (block.dayStart < day || (block.dayStart == day && block.hourStart <= hour))
                    && (block.dayEnd > day || (block.dayEnd == day && hour < block.hourEnd))
        }
    }

    private fun Schedule.toIndependentElectricityStatusBlocks(): List<IndependentElectricityStatusBlock> {
        val blocks = mutableListOf<IndependentElectricityStatusBlock>()
        var currentBlock: IndependentElectricityStatusBlock? = null
        for (day in weekSchedule.indices) {
            for (hour in weekSchedule[day].indices) {
                val status = weekSchedule[day][hour]
                if (currentBlock?.status != status) {
                    if (currentBlock != null) {
                        blocks += currentBlock
                    }
                    currentBlock = IndependentElectricityStatusBlock(
                        dayStart = day,
                        hourStart = hour,
                        dayEnd = day,
                        hourEnd = hour + 1,
                        status = status
                    )
                } else {
                    currentBlock = currentBlock.copy(
                        dayEnd = day,
                        hourEnd = hour + 1,
                    )
                }
            }
        }
        if (currentBlock != null) {
            blocks += currentBlock
        }
        return blocks
    }

    private data class IndependentElectricityStatusBlock(
        val dayStart: Int,
        val hourStart: Int,
        val dayEnd: Int,
        val hourEnd: Int,
        val status: ElectricityStatus
    )
}
