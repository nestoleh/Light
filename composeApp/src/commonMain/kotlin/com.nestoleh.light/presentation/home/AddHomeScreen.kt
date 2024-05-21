package com.nestoleh.light.presentation.home

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
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.nestoleh.light.theme.components.LockedProgressButton
import com.nestoleh.light.theme.components.ToolbarIcon
import com.nestoleh.light.theme.components.ToolbarTitle
import com.nestoleh.light.util.koinViewModel
import light.composeapp.generated.resources.Res
import light.composeapp.generated.resources.add_home_title
import light.composeapp.generated.resources.field_home_name
import light.composeapp.generated.resources.ic_close
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource


@Composable
fun AddNewHomeScreen(
    viewModel: AddHomeViewModel = koinViewModel(),
    onBack: () -> Unit
) {
    val state = viewModel.state.collectAsState()
    AddNewHomeScreenContent(
        state = state,
        onAction = { viewModel.onAction(it) },
        onBack = onBack
    )
}

@Composable
fun AddNewHomeScreenContent(
    state: State<AddHomeState>,
    onAction: (AddHomeAction) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    ToolbarTitle(
                        modifier = Modifier.padding(end = 48.dp),
                        title = stringResource(Res.string.add_home_title)
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
            HomeNameField(
                name = state.value.name,
                nameError = state.value.nameError,
                onValueChanged = { onAction(AddHomeAction.NameChanged(it)) }
            )
            Spacer(modifier = Modifier.weight(1f))
            LockedProgressButton(
                isInProgress = state.value.isSaving,
                onClick = { onAction(AddHomeAction.Save) }
            )
        }
    }
}

@Composable
private fun HomeNameField(
    name: String,
    nameError: String?,
    onValueChanged: (value: String) -> Unit
) {
    TextField(
        modifier = Modifier
            .widthIn(max = 400.dp)
            .fillMaxWidth(),
        value = name,
        onValueChange = { onValueChanged(it) },
        label = {
            Text(stringResource(Res.string.field_home_name))
        },
        isError = !nameError.isNullOrEmpty(),
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Done
        ),
        supportingText = {
            if (!nameError.isNullOrEmpty()) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = nameError ?: "",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    )
}