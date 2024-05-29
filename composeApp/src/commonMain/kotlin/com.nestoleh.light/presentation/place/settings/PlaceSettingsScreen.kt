package com.nestoleh.light.presentation.place.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.nestoleh.light.domain.model.ElectricityStatus
import com.nestoleh.light.domain.model.Place
import com.nestoleh.light.domain.model.Schedule
import com.nestoleh.light.presentation.components.ToolbarIcon
import com.nestoleh.light.presentation.components.ToolbarTitle
import com.nestoleh.light.util.HandleErrorsFlow
import com.nestoleh.light.util.koinViewModel
import light.composeapp.generated.resources.Res
import light.composeapp.generated.resources.ic_close
import light.composeapp.generated.resources.ic_delete
import org.jetbrains.compose.resources.painterResource
import org.koin.core.parameter.parametersOf

@Composable
fun PlaceSettingsScreen(
    id: Int,
    onBack: () -> Unit
) {
    val viewModel: PlaceSettingsViewModel = koinViewModel {
        parametersOf(id)
    }
    val state = viewModel.state.collectAsState()
    HandleDeleteState(state, onBack)
    val snackbarHostState = remember { SnackbarHostState() }
    HandleErrorsFlow(viewModel.errorEventsFlow, snackbarHostState)
    PlaceSettingsScreenContent(
        place = state.value.place,
        snackbarHostState = snackbarHostState,
        onEvent = { viewModel.onAction(it) },
        onBack = onBack
    )
}

@Composable
fun PlaceSettingsScreenContent(
    place: Place?,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    onEvent: (PlaceSettingsAction) -> Unit,
    onBack: () -> Unit
) {
    val showDeleteDialog = remember { mutableStateOf(false) }
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    ToolbarTitle(
                        title = place?.name ?: ""
                    )
                },
                navigationIcon = {
                    ToolbarIcon(
                        painter = painterResource(Res.drawable.ic_close),
                        onClick = onBack,
                        contentDescription = "Back button"
                    )
                },
                actions = {
                    if (place != null) {
                        ToolbarIcon(
                            painter = painterResource(Res.drawable.ic_delete),
                            onClick = {
                                showDeleteDialog.value = true
                            },
                            contentDescription = "Delete button"
                        )
                    }
                }
            )
        }
    ) { paddings ->
        Column(
            modifier = Modifier
                .padding(paddings)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (place != null) {
                Spacer(modifier = Modifier.height(32.dp))
                ScheduleGrid(
                    schedule = place.schedule,
                    onScheduleToggle = { day, hour ->
                        onEvent(PlaceSettingsAction.ToggleSchedule(day, hour))
                    }
                )
                Spacer(modifier = Modifier.height(32.dp))
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = {
                        onEvent(PlaceSettingsAction.Save)
                    }
                ) {
                    Text(text = "Save")
                }
            }
        }
        if (showDeleteDialog.value) {
            DeletePlaceDialog(
                onDismissRequest = {
                    showDeleteDialog.value = false
                },
                onConfirm = {
                    onEvent(PlaceSettingsAction.DeletePlace)
                    showDeleteDialog.value = false
                }
            )
        }
    }
}

private const val dayCellWidthDp = 50
private const val statusCellWidthDp = 70

@Composable
fun ScheduleGrid(
    schedule: Schedule,
    onScheduleToggle: (day: Int, hour: Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
    ) {
        Row {
            Spacer(Modifier.width(dayCellWidthDp.dp))
            for (hourIndex in 0..23) {
                ScheduleHourHeader(hourIndex)
            }
        }
        for (dayIndex in 0..6) {
            Row {
                ScheduleDayHeader(dayIndex)
                for (hourIndex in 0..23) {
                    ScheduleCellStatus(
                        status = schedule.weekSchedule[dayIndex][hourIndex],
                        onClick = {
                            onScheduleToggle(dayIndex, hourIndex)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ScheduleCellStatus(
    status: ElectricityStatus,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(statusCellWidthDp.dp)
            .height(40.dp)
            .padding(2.dp)
            .background(
                when (status) {
                    ElectricityStatus.On -> Color.Green
                    ElectricityStatus.Off -> Color.Red
                    ElectricityStatus.PossibleOff -> Color.Yellow
                }
            )
            .clickable { onClick() }

    )
}

@Composable
fun ScheduleDayHeader(dayIndex: Int) {
    Box(modifier = Modifier.width(dayCellWidthDp.dp)) {
        Text(
            text = dayIndex.dayName(),
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Composable
fun ScheduleHourHeader(hourIndex: Int) {
    Box(
        modifier = Modifier.width(statusCellWidthDp.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier.fillMaxWidth().padding(4.dp),
            maxLines = 1,
            textAlign = TextAlign.Center,
            text = hourIndex.hourName(),
        )
    }
}

private fun Int.dayName(): String {
    return when (this) {
        0 -> "Mon"
        1 -> "Tue"
        2 -> "Wed"
        3 -> "Thu"
        4 -> "Fri"
        5 -> "Sat"
        6 -> "Sun"
        else -> throw IllegalArgumentException("Invalid day index")
    }
}

private fun Int.hourName(): String {
    return if (this in 0..24) {
        "${if (this < 10) "0$this" else this}:00"
    } else {
        throw IllegalArgumentException("Invalid time index")
    }
}

@Composable
private inline fun HandleDeleteState(
    state: State<PlaceSettingsUIState>,
    noinline onBack: () -> Unit
) {
    LaunchedEffect(state.value.isDeleted) {
        if (state.value.isDeleted) {
            onBack()
        }
    }
}
