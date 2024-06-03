package com.nestoleh.light.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.nestoleh.light.domain.model.ElectricityStatus
import com.nestoleh.light.presentation.theme.DomainTheme
import light.composeapp.generated.resources.Res
import light.composeapp.generated.resources.day_friday
import light.composeapp.generated.resources.day_friday_short
import light.composeapp.generated.resources.day_monday
import light.composeapp.generated.resources.day_monday_short
import light.composeapp.generated.resources.day_saturday
import light.composeapp.generated.resources.day_saturday_short
import light.composeapp.generated.resources.day_sunday
import light.composeapp.generated.resources.day_sunday_short
import light.composeapp.generated.resources.day_thursday
import light.composeapp.generated.resources.day_thursday_short
import light.composeapp.generated.resources.day_tuesday
import light.composeapp.generated.resources.day_tuesday_short
import light.composeapp.generated.resources.day_wednesday
import light.composeapp.generated.resources.day_wednesday_short
import light.composeapp.generated.resources.electricity_status_name_off
import light.composeapp.generated.resources.electricity_status_name_on
import light.composeapp.generated.resources.electricity_status_name_possible_off
import org.jetbrains.compose.resources.stringResource

@Composable
fun Int.shortDayName(): String {
    return when (this) {
        0 -> stringResource(Res.string.day_monday_short)
        1 -> stringResource(Res.string.day_tuesday_short)
        2 -> stringResource(Res.string.day_wednesday_short)
        3 -> stringResource(Res.string.day_thursday_short)
        4 -> stringResource(Res.string.day_friday_short)
        5 -> stringResource(Res.string.day_saturday_short)
        6 -> stringResource(Res.string.day_sunday_short)
        else -> throw IllegalArgumentException("Invalid day number - $this")
    }
}

@Composable
fun Int.fullDayName(): String {
    return when (this) {
        0 -> stringResource(Res.string.day_monday)
        1 -> stringResource(Res.string.day_tuesday)
        2 -> stringResource(Res.string.day_wednesday)
        3 -> stringResource(Res.string.day_thursday)
        4 -> stringResource(Res.string.day_friday)
        5 -> stringResource(Res.string.day_saturday)
        6 -> stringResource(Res.string.day_sunday)
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
    @Composable
    get() = when (this) {
        ElectricityStatus.On -> DomainTheme.colorScheme.electricityStatusOn
        ElectricityStatus.Off -> DomainTheme.colorScheme.electricityStatusOff
        ElectricityStatus.PossibleOff -> DomainTheme.colorScheme.electricityStatusPossibleOff
    }

val ElectricityStatus.statusName: String
    @Composable
    get() = when (this) {
        ElectricityStatus.On -> stringResource(Res.string.electricity_status_name_on)
        ElectricityStatus.Off -> stringResource(Res.string.electricity_status_name_off)
        ElectricityStatus.PossibleOff -> stringResource(Res.string.electricity_status_name_possible_off)
    }