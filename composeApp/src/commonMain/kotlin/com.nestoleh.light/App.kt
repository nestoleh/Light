package com.nestoleh.light

import androidx.compose.runtime.Composable
import com.nestoleh.light.presentation.navigation.LightAppNavigation
import com.nestoleh.light.presentation.theme.LightAppTheme
import org.koin.compose.KoinContext

@Composable
fun App() {
    LightAppTheme {
        KoinContext {
            LightAppNavigation()
        }
    }
}
