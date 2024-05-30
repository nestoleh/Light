package com.nestoleh.light.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.unit.sp

val lightAppTypography: () -> Typography = {
    val defaultTypography = Typography()
    Typography(
        titleLarge = defaultTypography.titleLarge.copy(
            fontSize = 20.sp
        )
    )
}