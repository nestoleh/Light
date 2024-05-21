package com.nestoleh.light.theme.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import light.composeapp.generated.resources.Res
import light.composeapp.generated.resources.button_save
import org.jetbrains.compose.resources.stringResource

@Composable
fun LockedProgressButton(
    isInProgress: Boolean,
    onClick: () -> Unit
) {
    Button(
        modifier = Modifier
            .animateContentSize(),
        enabled = !isInProgress,
        onClick = onClick
    ) {
        Text(text = stringResource(Res.string.button_save))
        if (isInProgress) {
            CircularProgressIndicator(
                strokeCap = StrokeCap.Round,
                strokeWidth = 3.dp,
                modifier = Modifier
                    .padding(start = 16.dp)
                    .size(20.dp),
            )
        }
    }
}