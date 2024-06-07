package com.nestoleh.light.presentation.main.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.nestoleh.light.domain.model.ElectricityStatusPeriod
import com.nestoleh.light.presentation.components.color
import com.nestoleh.light.presentation.components.statusName
import com.nestoleh.light.presentation.theme.DomainTheme
import com.nestoleh.light.util.toHumanReadable
import com.nestoleh.light.util.watchFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import light.composeapp.generated.resources.Res
import light.composeapp.generated.resources.common_no_data
import light.composeapp.generated.resources.electricity_period_label_percent_left
import light.composeapp.generated.resources.electricity_period_label_status
import light.composeapp.generated.resources.electricity_period_label_time_left
import light.composeapp.generated.resources.electricity_period_label_unlimited
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@Composable
fun CurrentElectricityPeriod(
    modifier: Modifier = Modifier,
    period: ElectricityStatusPeriod?
) {
    when (period) {
        is ElectricityStatusPeriod.Limited -> {
            CurrentPeriodLimited(
                modifier = modifier,
                period = period
            )
        }

        is ElectricityStatusPeriod.Unlimited -> {
            CurrentPeriodUnlimited(
                modifier = modifier,
                period = period
            )
        }

        null -> {
            UnknownCurrentPeriod(
                modifier = modifier
            )
        }
    }
}

@Composable
private fun UnknownCurrentPeriod(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .padding(((circleSizeOutDp - circleSizeInDp) / 2).dp)
            .size(circleSizeInDp.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.errorContainer),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(Res.string.common_no_data),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onErrorContainer
        )
    }
}

@Composable
private fun CurrentPeriodUnlimited(
    modifier: Modifier = Modifier,
    period: ElectricityStatusPeriod.Unlimited
) {
    val color by animateColorAsState(period.status.color)
    Box(
        modifier = modifier
            .padding(((circleSizeOutDp - circleSizeInDp) / 2).dp)
            .size(circleSizeInDp.dp)
            .clip(CircleShape)
            .background(color),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(Res.string.electricity_period_label_status, period.status.statusName),
                textAlign = TextAlign.Center,
                color = DomainTheme.colorScheme.onElectricityStatusColor,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.size(32.dp))
            Text(
                modifier = Modifier
                    .clip(RoundedCornerShape(50.dp))
                    .background(MaterialTheme.colorScheme.tertiaryContainer)
                    .padding(vertical = 1.dp, horizontal = 8.dp),
                text = stringResource(Res.string.electricity_period_label_unlimited),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun CurrentPeriodLimited(
    modifier: Modifier = Modifier,
    period: ElectricityStatusPeriod.Limited
) {
    val color by animateColorAsState(period.status.color)
    val durationLeft = remember {
        mutableStateOf("")
    }
    var timeLeft by rememberSaveable { mutableStateOf(1f) }
    val animatedTimeLeft by animateFloatAsState(
        targetValue = timeLeft,
        animationSpec = tween(500),
    )

    LaunchedEffect(period) {
        watchFlow(1.seconds)
            .onEach {
                val localPeriodStart = period.periodStart.toInstant(TimeZone.currentSystemDefault())
                val localPeriodEnd = period.periodEnd.toInstant(TimeZone.currentSystemDefault())
                val duration: Duration =
                    localPeriodEnd - Clock.System.now()
                durationLeft.value = duration.toHumanReadable(isShowSeconds = duration < 1.minutes)
                val fullPeriod = localPeriodEnd - localPeriodStart
                timeLeft = duration.inWholeMilliseconds.toFloat() / fullPeriod.inWholeMilliseconds.toFloat()
            }
            .launchIn(this)
    }
    Box(
        modifier = modifier
            .size(circleSizeOutDp.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = modifier
                .size(circleSizeInDp.dp)
                .clip(CircleShape)
                .background(color),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = stringResource(Res.string.electricity_period_label_status, period.status.statusName),
                    textAlign = TextAlign.Center,
                    color = DomainTheme.colorScheme.onElectricityStatusColor,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50.dp))
                        .background(MaterialTheme.colorScheme.tertiaryContainer)
                        .padding(vertical = 1.dp, horizontal = 8.dp),
                    text = stringResource(Res.string.electricity_period_label_percent_left, (timeLeft * 100).toInt()),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.size(32.dp))
                Text(
                    text = stringResource(Res.string.electricity_period_label_time_left),
                    textAlign = TextAlign.Center,
                    color = DomainTheme.colorScheme.onElectricityStatusColor
                )
                Spacer(modifier = Modifier.size(4.dp))
                Text(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50.dp))
                        .background(MaterialTheme.colorScheme.tertiaryContainer)
                        .padding(vertical = 1.dp, horizontal = 8.dp),
                    text = durationLeft.value,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        Spacer(
            modifier = Modifier
                .size(280.dp)
                .drawWithCache {
                    val brush = SolidColor(color)
                    val width = 12.dp.toPx()
                    onDrawWithContent {
                        drawArc(
                            brush = brush,
                            startAngle = -90f,
                            sweepAngle = 360f * animatedTimeLeft,
                            useCenter = false,
                            style = Stroke(
                                width = width,
                                cap = StrokeCap.Round
                            )
                        )
                    }
                }
        )
    }
}

private const val circleSizeInDp = 250
private const val circleSizeOutDp = 300