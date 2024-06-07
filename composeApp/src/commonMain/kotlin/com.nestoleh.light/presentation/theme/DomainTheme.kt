package com.nestoleh.light.presentation.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.nestoleh.light.presentation.theme.LightAppColors.electricityStatusOffLight
import com.nestoleh.light.presentation.theme.LightAppColors.electricityStatusOnLight
import com.nestoleh.light.presentation.theme.LightAppColors.electricityStatusPossibleOffLight
import com.nestoleh.light.presentation.theme.LightAppColors.onElectricityStatusColorLight
import com.nestoleh.light.presentation.theme.LightAppColors.scheduleCurrentTimeLight

/*
     Class with color scheme for the current application
     It contains specific colors which can't be get from material theme
 */
data class DomainColorScheme(
    val electricityStatusOn: Color,
    val electricityStatusOff: Color,
    val electricityStatusPossibleOff: Color,
    val onElectricityStatusColor: Color,
    val scheduleCurrentTime: Color
)

val defaultDomainColorScheme = DomainColorScheme(
    electricityStatusOn = electricityStatusOnLight,
    electricityStatusOff = electricityStatusOffLight,
    electricityStatusPossibleOff = electricityStatusPossibleOffLight,
    onElectricityStatusColor = onElectricityStatusColorLight,
    scheduleCurrentTime = scheduleCurrentTimeLight
)

object DomainTheme {
    val colorScheme: DomainColorScheme
        @Composable
        @ReadOnlyComposable
        get() = LocalDomainColorScheme.current
}

internal val LocalDomainColorScheme = staticCompositionLocalOf { defaultDomainColorScheme }

@Composable
fun DomainTheme(
    colorScheme: DomainColorScheme = defaultDomainColorScheme,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalDomainColorScheme provides colorScheme,
    ) {
        content()
    }
}
