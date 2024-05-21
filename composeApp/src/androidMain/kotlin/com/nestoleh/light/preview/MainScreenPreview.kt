package com.nestoleh.light.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.nestoleh.light.presentation.main.MainScreenContent
import com.nestoleh.light.theme.LightAppTheme

@Composable
@Preview
fun MainScreenPreview() {
    LightAppTheme {
        MainScreenContent(
            onAddNewHome = {}
        )
    }
}