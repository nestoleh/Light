package com.nestoleh.light.presentation

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.nestoleh.light.theme.LightAppColors
import light.composeapp.generated.resources.Res
import light.composeapp.generated.resources.button_switch
import light.composeapp.generated.resources.ic_bulb
import light.composeapp.generated.resources.label_light_status
import light.composeapp.generated.resources.label_status_off
import light.composeapp.generated.resources.label_status_on
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalResourceApi::class)
@Composable
fun MainScreen() {
    var isLightOn by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1f))
        val color by animateColorAsState(
            if (isLightOn) LightAppColors.bulbOn else LightAppColors.bulbOff
        )
        Box(
            modifier = Modifier
                .size(300.dp)
                .clip(CircleShape)
                .background(color),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(
                    resource = Res.string.label_light_status,
                    stringResource(if (isLightOn) Res.string.label_status_on else Res.string.label_status_off)
                ),
                textAlign = TextAlign.Center,
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = { isLightOn = !isLightOn }
        ) {
            Icon(
                modifier = Modifier.size(20.dp),
                painter = painterResource(Res.drawable.ic_bulb),
                contentDescription = "bulb",
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(Res.string.button_switch),
            )
        }
        Spacer(modifier = Modifier.weight(0.5f))
    }
}