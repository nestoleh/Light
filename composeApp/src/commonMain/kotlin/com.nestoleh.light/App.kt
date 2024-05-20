package com.nestoleh.light

import androidx.compose.runtime.Composable
import com.nestoleh.light.presentation.MainScreen
import com.nestoleh.light.theme.LightAppTheme
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    LightAppTheme {
        MainScreen()
    }
}