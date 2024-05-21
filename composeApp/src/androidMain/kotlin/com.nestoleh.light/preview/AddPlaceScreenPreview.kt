package com.nestoleh.light.preview

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import com.nestoleh.light.presentation.place.AddPlaceScreenContent
import com.nestoleh.light.presentation.place.AddPlaceState

@Composable
@Preview
fun AddPlaceScreenPreview() {
    AddPlaceScreenContent(
        state = remember { mutableStateOf(AddPlaceState()) },
        onAction = {},
        onBack = {}
    )
}