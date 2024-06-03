package com.nestoleh.light.presentation.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp

@Composable
fun LockedProgressButton(
    isInProgress: Boolean,
    onClick: () -> Unit,
    text: String
) {
    Button(
        modifier = Modifier
            .widthIn(min = 200.dp)
            .animateContentSize(),
        enabled = !isInProgress,
        onClick = onClick
    ) {
        Text(text = text)
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