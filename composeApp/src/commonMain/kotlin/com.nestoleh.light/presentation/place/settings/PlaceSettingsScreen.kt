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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.nestoleh.light.domain.model.ElectricityStatus
import com.nestoleh.light.domain.model.Schedule
import com.nestoleh.light.presentation.components.LockedProgressButton
import com.nestoleh.light.presentation.components.ToolbarIcon
import com.nestoleh.light.presentation.components.ToolbarTitle
import com.nestoleh.light.presentation.components.color
import com.nestoleh.light.presentation.components.hourName
import com.nestoleh.light.presentation.components.shortDayName
import com.nestoleh.light.util.HandleErrorsFlow
import com.nestoleh.light.util.koinViewModel
import light.composeapp.generated.resources.Res
import light.composeapp.generated.resources.button_save
import light.composeapp.generated.resources.ic_close
import light.composeapp.generated.resources.ic_delete
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.core.parameter.parametersOf

@Composable
fun PlaceSettingsScreen(
    id: String,
    onBack: () -> Unit
) {
    val viewModel: PlaceSettingsViewModel = koinViewModel {
        parametersOf(id)
    }
    val state = viewModel.state.collectAsState()
    HandleCloseState(state, onBack)
    val snackbarHostState = remember { SnackbarHostState() }
    HandleErrorsFlow(viewModel.errorEventsFlow, snackbarHostState)
    PlaceSettingsScreenContent(
        state = state.value,
        snackbarHostState = snackbarHostState,
        onAction = { viewModel.onAction(it) },
        onBack = onBack
    )
}

@Composable
fun PlaceSettingsScreenContent(
    state: PlaceSettingsUIState,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    onAction: (PlaceSettingsAction) -> Unit,
    onBack: () -> Unit
) {
    val showDeleteDialog = remember { mutableStateOf(false) }
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    ToolbarTitle(
                        title = state.place?.name ?: ""
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
                    if (state.place != null) {
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
            if (state.place != null) {
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    text = "Schedule",
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )
                ScheduleGrid(
                    schedule = state.place.schedule,
                    onScheduleToggle = { day, hour ->
                        onAction(PlaceSettingsAction.ToggleSchedule(day, hour))
                    }
                )
                Spacer(modifier = Modifier.height(32.dp))
                Spacer(modifier = Modifier.weight(1f))
                LockedProgressButton(
                    text = stringResource(Res.string.button_save),
                    isInProgress = state.isSaving,
                    onClick = {
                        onAction(PlaceSettingsAction.Save)
                    }
                )
            }
        }
        if (showDeleteDialog.value) {
            DeletePlaceDialog(
                onDismissRequest = {
                    showDeleteDialog.value = false
                },
                onConfirm = {
                    onAction(PlaceSettingsAction.DeletePlace)
                    showDeleteDialog.value = false
                }
            )
        }
    }
}

private const val dayCellWidthDp = 60
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
            .padding(end = 8.dp)
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
        Row {
            Spacer(Modifier.width(dayCellWidthDp.dp))
            for (hourIndex in 0..23) {
                ScheduleHourHeader(hourIndex)
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
            .clip(RoundedCornerShape(8.dp))
            .padding(1.dp)
            .background(status.color)
            .clickable { onClick() }

    )
}

@Composable
fun ScheduleDayHeader(dayIndex: Int) {
    Box(
        modifier = Modifier.width(dayCellWidthDp.dp)
    ) {
        Text(
            text = dayIndex.shortDayName(),
            modifier = Modifier
                .padding(vertical = 3.dp, horizontal = 2.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(50.dp))
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .padding(vertical = 6.dp),
            textAlign = TextAlign.Center,
            maxLines = 1,
            style = MaterialTheme.typography.titleSmall
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
            modifier = Modifier
                .padding(vertical = 6.dp, horizontal = 2.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(50.dp))
                .background(MaterialTheme.colorScheme.tertiaryContainer)
                .padding(vertical = 2.dp),
            maxLines = 1,
            textAlign = TextAlign.Center,
            text = hourIndex.hourName(),
            style = MaterialTheme.typography.titleSmall
        )
    }
}

@Composable
private inline fun HandleCloseState(
    state: State<PlaceSettingsUIState>,
    noinline onBack: () -> Unit
) {
    LaunchedEffect(state.value.isDeleted, state.value.isSaved) {
        if (state.value.isDeleted || state.value.isSaved) {
            onBack()
        }
    }
}
