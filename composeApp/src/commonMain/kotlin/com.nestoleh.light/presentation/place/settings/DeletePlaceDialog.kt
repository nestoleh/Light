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
import org.jetbrains.compose.resources.painterResource

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
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Cancel")
            }
        },
        title = {
            Text("Delete this home?")
        },
        text = {
            Text("Are you sure you want to delete this home? This action cannot be undone.")
        }
    )
}