package com.nestoleh.light.preview

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import com.nestoleh.light.presentation.home.AddHomeState
import com.nestoleh.light.presentation.home.AddNewHomeScreenContent

@Composable
@Preview
fun AddNewHomeScreenPreview() {
    AddNewHomeScreenContent(
        state = remember { mutableStateOf(AddHomeState()) },
        onAction = {},
        onBack = {}
    )
}