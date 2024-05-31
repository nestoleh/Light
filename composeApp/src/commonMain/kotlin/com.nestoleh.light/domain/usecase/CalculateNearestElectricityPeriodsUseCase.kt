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

class CalculateNearestElectricityPeriods2UseCase(
    private val dispatcher: CoroutineDispatcher
) : FlowUseCase<Schedule, NearestElectricityPeriods?>() {
    override fun doWork(params: Schedule): Flow<NearestElectricityPeriods?> {
        return flowOf(params.toIndependentElectricityStatusBlocks())
            .flatMapLatest { blocks ->
                Logger.d { "Blocks -> [${blocks.size}] --> $blocks" }
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
                            if (currentBlockIndex >= 0) {
                                val block = blocks[currentBlockIndex]
                                val circularBlock =
                                    if (blocks.size > 1
                                        && currentBlockIndex == blocks.size - 1
                                        && blocks.first().status == block.status
                                    ) {
                                        blocks.first()
                                    } else {
                                        null
                                    }
                                val additionalDaysFromCircularBlock = circularBlock?.dayEnd?.plus(1) ?: 0
                                val current = ElectricityStatusPeriod.Limited(
                                    periodStart = (now - (currentDay - block.dayStart).days)
                                        .toLocalDateTime(TimeZone.currentSystemDefault())
                                        .setupWithHour(block.hourStart),
                                    periodEnd = (now + (block.dayEnd - currentDay + additionalDaysFromCircularBlock).days)
                                        .toLocalDateTime(TimeZone.currentSystemDefault())
                                        .setupWithHour(circularBlock?.hourEnd ?: block.hourEnd),
                                    status = block.status
                                )
                                NearestElectricityPeriods(
                                    current = current,
                                    future = emptyList() // TODO: count future blocks
                                )
                            } else {
                                null
                            }
                        }
                }
            }
            .flowOn(dispatcher)
    }

    private fun LocalDateTime.setupWithHour(hour: Int): LocalDateTime {
        val localTime = if (hour == 24) {
            LocalTime(23, 59, 59)
        } else {
            LocalTime(hour, 0)
        }
        return this.date.atTime(localTime)
    }

    private fun List<IndependentElectricityStatusBlock>.findBlockIndexFor(
        day: Int,
        hour: Int
    ): Int {
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
}

data class IndependentElectricityStatusBlock(
    val dayStart: Int,
    val hourStart: Int,
    val dayEnd: Int,
    val hourEnd: Int,
    val status: ElectricityStatus
)