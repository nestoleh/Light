package com.nestoleh.light.presentation.main

import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.nestoleh.light.domain.model.Place
import com.nestoleh.light.util.koinViewModel
import kotlinx.coroutines.launch
import light.composeapp.generated.resources.Res
import light.composeapp.generated.resources.button_add_place
import light.composeapp.generated.resources.ic_down
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun MainScreen(
    viewModel: MainViewModel = koinViewModel(),
    onAddNewPlace: () -> Unit
) {
    val currentPlace by viewModel.currentPlaceFlow.collectAsState()
    val allPlaces by viewModel.placesFlow.collectAsState()
    MainScreenContent(
        currentPlace = currentPlace,
        places = allPlaces,
        onAddNewPlace = onAddNewPlace,
        onSelectPlace = { place -> viewModel.selectPlace(place) }
    )
}

@Composable
fun MainScreenContent(
    currentPlace: Place?,
    places: List<Place>,
    onAddNewPlace: () -> Unit,
    onSelectPlace: (Place) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showPlacesBottomSheet by rememberSaveable { mutableStateOf(false) }
    Scaffold(
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            if (currentPlace == null) {
                FloatingActionButton(
                    modifier = Modifier
                        .padding(bottom = 48.dp)
                        .height(54.dp)
                        .widthIn(min = 200.dp),
                    onClick = onAddNewPlace
                ) {
                    Text(
                        text = stringResource(Res.string.button_add_place)
                    )
                }
            } else {
                val yOffset = animateDpAsState(
                    targetValue = if (showPlacesBottomSheet) (150).dp else 0.dp,
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = EaseInOut
                    )
                )
                FloatingActionButton(
                    modifier = Modifier
                        .offset(y = yOffset.value)
                        .padding(bottom = 48.dp)
                        .height(54.dp)
                        .width(250.dp),
                    onClick = { showPlacesBottomSheet = true }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 48.dp, end = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = currentPlace.name,
                            maxLines = 1,
                            textAlign = TextAlign.Center,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Image(
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .size(24.dp),
                            painter = painterResource(Res.drawable.ic_down),
                            colorFilter = ColorFilter.tint(LocalContentColor.current),
                            contentDescription = "Arrow down icon"
                        )
                    }
                }
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 102.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (currentPlace == null) {
                Text(text = "You don't have any places yet. \nAdd a new one!")
            } else {
                Text(text = "Selected place:\n${currentPlace.name}")
            }
        }

        if (showPlacesBottomSheet) {
            PlacesSelectorBottomSheet(
                selectedPlace = currentPlace,
                allPlaces = places,
                onPlaceSelected = { place ->
                    onSelectPlace(place)
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) {
                            showPlacesBottomSheet = false
                        }
                    }
                },
                onAddNewPlace = {
                    onAddNewPlace()
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) {
                            showPlacesBottomSheet = false
                        }
                    }
                },
                onDismissRequest = { showPlacesBottomSheet = false }
            )
        }

    }
}