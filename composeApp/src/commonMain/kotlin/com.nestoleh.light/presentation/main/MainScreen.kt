package com.nestoleh.light.presentation.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nestoleh.light.domain.model.Place
import com.nestoleh.light.util.koinViewModel
import light.composeapp.generated.resources.Res
import light.composeapp.generated.resources.button_add_place
import org.jetbrains.compose.resources.stringResource

@Composable
fun MainScreen(
    viewModel: MainViewModel = koinViewModel(),
    onAddNewPlace: () -> Unit
) {
    val places by viewModel.placesState.collectAsState()
    MainScreenContent(
        places = places,
        onAddNewPlace = onAddNewPlace
    )
}

@Composable
fun MainScreenContent(
    places: List<Place>,
    onAddNewPlace: () -> Unit
) {
    Scaffold(
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier
                    .padding(bottom = 32.dp)
                    .height(54.dp)
                    .widthIn(min = 200.dp),
                onClick = onAddNewPlace
            ) {
                Text(
                    text = stringResource(Res.string.button_add_place)
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
        if (places.isEmpty()) {
            Text(text = "No places")
        } else {
            places.forEach {
                Text(text = "${it.id}. ${it.name}")
            }
        }

        // TODO: ui for main screen
    }
}