package com.nestoleh.light.presentation.place.settings

import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import light.composeapp.generated.resources.Res
import light.composeapp.generated.resources.ic_delete
import light.composeapp.generated.resources.place_settings_delete_dialog_button_confirm
import light.composeapp.generated.resources.place_settings_delete_dialog_button_reject
import light.composeapp.generated.resources.place_settings_delete_dialog_message
import light.composeapp.generated.resources.place_settings_delete_dialog_title
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun DeletePlaceDialog(
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        icon = {
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(Res.drawable.ic_delete),
                contentDescription = "Delete icon",
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm()
                }
            ) {
                Text(text = stringResource(Res.string.place_settings_delete_dialog_button_confirm))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text(text = stringResource(Res.string.place_settings_delete_dialog_button_reject))
            }
        },
        title = {
            Text(text = stringResource(Res.string.place_settings_delete_dialog_title))
        },
        text = {
            Text(text = stringResource(Res.string.place_settings_delete_dialog_message))
        }
    )
}