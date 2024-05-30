package com.nestoleh.light.data.converters

import com.nestoleh.light.data.database.entity.ScheduleSlotEntity
import com.nestoleh.light.domain.model.ElectricityStatus
import com.nestoleh.light.domain.model.Schedule

fun Schedule.toScheduleSlotEntities(
    placeId: String
): List<ScheduleSlotEntity> {
    val slots = mutableListOf<ScheduleSlotEntity>()
    this.weekSchedule.forEachIndexed { dayIndex, dayOfWeekArray ->
        dayOfWeekArray.forEachIndexed { hourIndex, status ->
            slots.add(
                ScheduleSlotEntity(
                    placeId = placeId,
                    dayOfWeek = dayIndex,
                    hourOfDay = hourIndex,
                    status = status
                )
            )
        }
    }
    return slots
}

fun List<ScheduleSlotEntity>.toSchedule(): Schedule {
    val weekSchedule = Array(7) { Array(24) { ElectricityStatus.On } }
    forEach {
        if (it.dayOfWeek in 0..6 && it.hourOfDay in 0..23) {
            weekSchedule[it.dayOfWeek][it.hourOfDay] = it.status
        }
    }
    return Schedule(weekSchedule)
}