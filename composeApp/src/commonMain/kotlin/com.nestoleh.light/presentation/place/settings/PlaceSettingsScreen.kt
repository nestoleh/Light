package com.nestoleh.light.presentation.place.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.nestoleh.light.domain.model.Place
import com.nestoleh.light.theme.components.ToolbarIcon
import com.nestoleh.light.theme.components.ToolbarTitle
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
        onDelete = {
            viewModel.deletePlace()
        },
        onBack = onBack
    )
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

@Composable
fun PlaceSettingsScreenContent(
    place: Place?,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    onDelete: () -> Unit,
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
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 102.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Text(
                    text = "Place settings",
                    textAlign = TextAlign.Center
                )
            }
        }
        if (showDeleteDialog.value) {
            DeletePlaceDialog(
                onDismissRequest = {
                    showDeleteDialog.value = false
                },
                onConfirm = {
                    onDelete()
                    showDeleteDialog.value = false
                }
            )
        }
    }
}