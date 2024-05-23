package com.nestoleh.light.presentation.place.add

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.nestoleh.light.domain.model.Place
import com.nestoleh.light.presentation.components.LockedProgressButton
import com.nestoleh.light.presentation.components.ToolbarIcon
import com.nestoleh.light.presentation.components.ToolbarTitle
import com.nestoleh.light.util.HandleErrorsFlow
import com.nestoleh.light.util.koinViewModel
import kotlinx.coroutines.launch
import light.composeapp.generated.resources.Res
import light.composeapp.generated.resources.add_place_title
import light.composeapp.generated.resources.field_place_name
import light.composeapp.generated.resources.ic_close
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource


@Composable
fun AddPlaceScreen(
    viewModel: AddPlaceViewModel = koinViewModel(),
    onNavigateToPlaceSettings: (place: Place) -> Unit,
    onBack: () -> Unit
) {
    val state = viewModel.state.collectAsState()
    HandlePlaceAdded(state, onNavigateToPlaceSettings)
    val snackbarHostState = remember { SnackbarHostState() }
    HandleErrorsFlow(viewModel.errorEventsFlow, snackbarHostState)
    AddPlaceScreenContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onAction = { viewModel.onAction(it) },
        onBack = onBack
    )
}

@Composable
private inline fun HandlePlaceAdded(
    state: State<AddPlaceUIState>,
    noinline onPlaceAdded: (place: Place) -> Unit
) {
    LaunchedEffect(state.value.savedPlace) {
        state.value.savedPlace?.let {
            onPlaceAdded(it)
        }
    }
}

@Composable
fun AddPlaceScreenContent(
    state: State<AddPlaceUIState>,
    onAction: (AddPlaceAction) -> Unit,
    onBack: () -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    ToolbarTitle(
                        modifier = Modifier.padding(end = 48.dp),
                        title = stringResource(Res.string.add_place_title)
                    )
                },
                navigationIcon = {
                    ToolbarIcon(
                        painter = painterResource(Res.drawable.ic_close),
                        onClick = onBack,
                        contentDescription = "Back button"
                    )
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))
            PlaceNameField(
                name = state.value.name,
                nameError = state.value.nameError,
                onValueChanged = { onAction(AddPlaceAction.NameChanged(it)) }
            )
            Spacer(modifier = Modifier.weight(1f))
            val keyboardController = LocalSoftwareKeyboardController.current
            val scope = rememberCoroutineScope()
            LockedProgressButton(
                isInProgress = state.value.isSaving,
                onClick = {
                    scope.launch {
                        keyboardController?.hide()
                    }
                    onAction(AddPlaceAction.Save)
                }
            )
        }
    }
}

@Composable
private fun PlaceNameField(
    name: String,
    nameError: String?,
    onValueChanged: (value: String) -> Unit
) {
    TextField(
        modifier = Modifier
            .widthIn(max = 400.dp)
            .fillMaxWidth(),
        value = name,
        onValueChange = {
            if (it.length <= 50) {
                onValueChanged(it)
            }
        },
        label = {
            Text(stringResource(Res.string.field_place_name))
        },
        isError = !nameError.isNullOrEmpty(),
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            capitalization = KeyboardCapitalization.Words,
            imeAction = ImeAction.Done
        ),
        supportingText = {
            if (!nameError.isNullOrEmpty()) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = nameError,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    )
}