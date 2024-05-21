package com.nestoleh.light.presentation.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nestoleh.light.util.koinViewModel
import light.composeapp.generated.resources.Res
import light.composeapp.generated.resources.button_add_home
import org.jetbrains.compose.resources.stringResource

@Composable
fun MainScreen(
    viewModel: MainViewModel = koinViewModel(),
    onAddNewHome: () -> Unit
) {
    MainScreenContent(
        onAddNewHome = onAddNewHome
    )
}

@Composable
fun MainScreenContent(
    onAddNewHome: () -> Unit
) {
    Scaffold(
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier
                    .padding(bottom = 32.dp)
                    .height(54.dp)
                    .widthIn(min = 200.dp),
                onClick = onAddNewHome
            ) {
                Text(
                    text = stringResource(Res.string.button_add_home)
                )
            }
        }
    ) {

    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 102.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // TODO: ui for main screen
    }
}