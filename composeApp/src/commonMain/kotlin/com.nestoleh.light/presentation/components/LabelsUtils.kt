package com.nestoleh.light.presentation.components

import androidx.compose.ui.graphics.Color
import com.nestoleh.light.domain.model.ElectricityStatus
import com.nestoleh.light.presentation.theme.LightAppColors


fun Int.shortDayName(): String {
    return when (this) {
        0 -> "Mon"
        1 -> "Tue"
        2 -> "Wed"
        3 -> "Thu"
        4 -> "Fri"
        5 -> "Sat"
        6 -> "Sun"
        else -> throw IllegalArgumentException("Invalid day number - $this")
    }
}

fun Int.fullDayName(): String {
    return when (this) {
        0 -> "Monday"
        1 -> "Tuesday"
        2 -> "Wednesday"
        3 -> "Thursday"
        4 -> "Friday"
        5 -> "Saturday"
        6 -> "Sunday"
        else -> throw IllegalArgumentException("Invalid day number - $this")
    }
}

fun Int.hourName(): String {
    return if (this in 0..24) {
        "${if (this < 10) "0$this" else this}:00"
    } else {
        throw IllegalArgumentException("Invalid time index - $this")
    }
}

val ElectricityStatus.color: Color
    get() = when (this) {
        ElectricityStatus.On -> LightAppColors.electricityStatusOn
        ElectricityStatus.Off -> LightAppColors.electricityStatusOff
        ElectricityStatus.PossibleOff -> LightAppColors.electricityStatusPossibleOff
    }