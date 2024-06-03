package com.nestoleh.light.presentation.main.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.nestoleh.light.domain.model.ElectricityStatusPeriod
import com.nestoleh.light.presentation.components.color
import com.nestoleh.light.presentation.components.statusName
import com.nestoleh.light.presentation.theme.DomainTheme
import com.nestoleh.light.util.formatDate
import com.nestoleh.light.util.isToday
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import light.composeapp.generated.resources.Res
import light.composeapp.generated.resources.electricity_period_next_status_label
import light.composeapp.generated.resources.electricity_period_next_status_label_start_at
import light.composeapp.generated.resources.electricity_period_no_limits_message
import light.composeapp.generated.resources.electricity_period_no_limits_title
import light.composeapp.generated.resources.ic_lightbulb
import light.composeapp.generated.resources.time_format
import light.composeapp.generated.resources.time_format_with_date
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun NextElectricityPeriod(
    modifier: Modifier = Modifier,
    period: ElectricityStatusPeriod.Limited
) {
    Card(
        modifier = modifier
            .padding(horizontal = 24.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = period.status.color,
            contentColor = DomainTheme.colorScheme.onElectricityStatusColor,
        ),
    ) {
        val date = period.periodStart.toInstant(TimeZone.currentSystemDefault())
        val startDateFormatted = if (date.isToday()) {
            date.formatDate(stringResource(Res.string.time_format))
        } else {
            date.formatDate(stringResource(Res.string.time_format_with_date))
        }
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row {
                Text(
                    modifier = Modifier,
                    text = stringResource(Res.string.electricity_period_next_status_label),
                    style = MaterialTheme.typography.titleMedium,
                    color = DomainTheme.colorScheme.onElectricityStatusColor
                )
                Text(
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .clip(RoundedCornerShape(50.dp))
                        .background(MaterialTheme.colorScheme.tertiaryContainer)
                        .padding(vertical = 1.dp, horizontal = 8.dp),
                    text = period.status.statusName,
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Text(
                modifier = Modifier,
                text = stringResource(Res.string.electricity_period_next_status_label_start_at, startDateFormatted),
                color = DomainTheme.colorScheme.onElectricityStatusColor,
                style = MaterialTheme.typography.labelMedium,
            )
        }
    }
}

@Composable
fun NoNextElectricityPeriod(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(24.dp)
        ) {
            Icon(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .size(24.dp),
                painter = painterResource(Res.drawable.ic_lightbulb),
                contentDescription = "Bulb icon",
            )
            Column(
                modifier = Modifier.padding(start = 16.dp)
            ) {
                Text(
                    modifier = Modifier,
                    text = stringResource(Res.string.electricity_period_no_limits_title),
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    modifier = Modifier,
                    text = stringResource(Res.string.electricity_period_no_limits_message),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}