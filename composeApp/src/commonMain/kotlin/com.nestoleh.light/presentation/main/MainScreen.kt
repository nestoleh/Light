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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.nestoleh.light.domain.model.ElectricityStatusBlock
import com.nestoleh.light.domain.model.Place
import com.nestoleh.light.presentation.components.ToolbarIcon
import com.nestoleh.light.presentation.components.color
import com.nestoleh.light.presentation.components.hourName
import com.nestoleh.light.presentation.main.components.CurrentElectricityPeriod
import com.nestoleh.light.presentation.theme.LightAppColors
import com.nestoleh.light.util.HandleErrorsFlow
import com.nestoleh.light.util.koinViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import light.composeapp.generated.resources.Res
import light.composeapp.generated.resources.button_add_place
import light.composeapp.generated.resources.ic_date_range
import light.composeapp.generated.resources.ic_down
import light.composeapp.generated.resources.ic_settings
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun MainScreen(
    viewModel: MainViewModel = koinViewModel(),
    onNavigateToAddPLace: () -> Unit,
    onNavigateToPlaceSettings: (Place) -> Unit,
    onNavigateToPlaceSchedule: (Place) -> Unit
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    HandleErrorsFlow(viewModel.errorEventsFlow, snackbarHostState)
    MainScreenContent(
        snackbarHostState = snackbarHostState,
        selectedPlaceState = state.selectedPlaceState,
        places = state.allPlaces,
        onAddNewPlace = onNavigateToAddPLace,
        onOpenPlaceSettings = onNavigateToPlaceSettings,
        onOpenPlaceSchedule = onNavigateToPlaceSchedule,
        onAction = { viewModel.onAction(it) },
    )
}

@Composable
fun MainScreenContent(
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    selectedPlaceState: SelectedPlaceState,
    places: List<Place>,
    onAction: (MainAction) -> Unit,
    onAddNewPlace: () -> Unit,
    onOpenPlaceSettings: (Place) -> Unit,
    onOpenPlaceSchedule: (Place) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var openPlaces by rememberSaveable { mutableStateOf(false) }
    var showPlacesBottomSheet by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(openPlaces) {
        if (openPlaces) {
            scope.launch {
                delay(200)
                showPlacesBottomSheet = true
            }
        }
    }
    LaunchedEffect(showPlacesBottomSheet) {
        if (!showPlacesBottomSheet) {
            openPlaces = false
        }
    }
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                navigationIcon = {
                    if (selectedPlaceState is SelectedPlaceState.Selected) {
                        ToolbarIcon(
                            painter = painterResource(Res.drawable.ic_date_range),
                            onClick = {
                                onOpenPlaceSchedule(selectedPlaceState.place)
                            },
                            contentDescription = "Place settings"
                        )
                    }
                },
                title = {},
                actions = {
                    ToolbarIcon(
                        painter = painterResource(Res.drawable.ic_settings),
                        onClick = {
                            if (selectedPlaceState is SelectedPlaceState.Selected) {
                                onOpenPlaceSettings(selectedPlaceState.place)
                            }
                        },
                        contentDescription = "Place settings"
                    )
                }
            )
        },
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            when (selectedPlaceState) {
                is SelectedPlaceState.None -> {
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
                }

                is SelectedPlaceState.Selected -> {
                    val yOffset = animateDpAsState(
                        targetValue = if (openPlaces) (150).dp else 0.dp,
                        animationSpec = tween(
                            durationMillis = 200,
                            easing = EaseInOut
                        )
                    )
                    FloatingActionButton(
                        modifier = Modifier
                            .offset(y = yOffset.value)
                            .padding(bottom = 48.dp)
                            .height(54.dp)
                            .width(250.dp),
                        onClick = { openPlaces = true }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 48.dp, end = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                text = selectedPlaceState.place.name,
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
        }
    ) { paddings ->
        Column(
            modifier = Modifier
                .padding(paddings)
                .fillMaxSize()
                .padding(bottom = 102.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (selectedPlaceState) {
                SelectedPlaceState.None -> {
                    Text(
                        text = "You don't have any places yet. \nAdd a new one!",
                        textAlign = TextAlign.Center,
                    )
                }

                is SelectedPlaceState.Selected -> {
                    Spacer(modifier = Modifier.height(32.dp))
                    CurrentElectricityPeriod(
                        period = selectedPlaceState.currentPeriod
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }

        if (showPlacesBottomSheet) {
            PlacesSelectorBottomSheet(
                sheetState = sheetState,
                selectedPlaceState = selectedPlaceState,
                allPlaces = places,
                onPlaceSelected = { place ->
                    onAction(MainAction.SelectPlace(place))
                    scope.launch { sheetState.hide() }
                        .invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                showPlacesBottomSheet = false
                            }
                        }
                },
                onAddNewPlace = {
                    scope.launch { sheetState.hide() }
                        .invokeOnCompletion {
                            onAddNewPlace()
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

@Composable
private fun NextElectricityBlock(
    block: ElectricityStatusBlock
) {
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = block.status.color,
            contentColor = LightAppColors.onElectricityStatusColor,
        )
    ) {
        Text(
            modifier = Modifier.padding(16.dp),
            text = "Next status change to: ${block.status}\nwill happen at ${block.hourStart.hourName()}",
            textAlign = TextAlign.Start,
            color = LightAppColors.onElectricityStatusColor
        )
    }
}