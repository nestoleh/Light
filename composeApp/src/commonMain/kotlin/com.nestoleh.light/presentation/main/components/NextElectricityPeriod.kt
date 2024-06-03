package com.nestoleh.light.presentation.main.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.nestoleh.light.domain.model.ElectricityStatusPeriod
import com.nestoleh.light.presentation.components.color
import com.nestoleh.light.presentation.theme.LightAppColors
import com.nestoleh.light.util.formatDate
import com.nestoleh.light.util.isToday
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import light.composeapp.generated.resources.Res
import light.composeapp.generated.resources.time_format
import light.composeapp.generated.resources.time_format_with_date
import org.jetbrains.compose.resources.stringResource

@Composable
fun NextElectricityPeriod(
    modifier: Modifier = Modifier,
    period: ElectricityStatusPeriod.Limited
) {
    Card(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = period.status.color,
            contentColor = LightAppColors.onElectricityStatusColor,
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
                    text = "Next status is",
                    style = MaterialTheme.typography.titleMedium,
                    color = LightAppColors.onElectricityStatusColor
                )
                Text(
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .clip(RoundedCornerShape(50.dp))
                        .background(MaterialTheme.colorScheme.tertiaryContainer)
                        .padding(vertical = 1.dp, horizontal = 8.dp),
                    text = "${period.status}",
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                    style = MaterialTheme.typography.titleSmall
                )
            }
            Text(
                modifier = Modifier,
                text = "at $startDateFormatted",
                color = LightAppColors.onElectricityStatusColor,
                style = MaterialTheme.typography.labelMedium,
            )
        }
    }
}

@Composable
fun NoNextElectricityPeriod(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                modifier = Modifier
                    .clip(RoundedCornerShape(50.dp))
                    .background(MaterialTheme.colorScheme.tertiaryContainer)
                    .padding(vertical = 1.dp, horizontal = 8.dp),
                text = "No limits",
                style = MaterialTheme.typography.titleSmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                modifier = Modifier,
                text = "According to schedule you don't have any possible electricity status changes in the future",
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}