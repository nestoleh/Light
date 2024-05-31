package com.nestoleh.light.domain.usecase

import com.nestoleh.light.core.domain.usecase.ResultUseCase
import com.nestoleh.light.domain.model.ElectricityStatusBlock
import com.nestoleh.light.domain.model.Schedule
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class CalculateScheduleAsBlocksUseCase(
    private val dispatcher: CoroutineDispatcher
) : ResultUseCase<Schedule, List<List<ElectricityStatusBlock>>>() {

    override suspend fun doWork(params: Schedule): List<List<ElectricityStatusBlock>> = withContext(dispatcher) {
        params.weekSchedule.map { daySchedule ->
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