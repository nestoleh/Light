package com.nestoleh.light.presentation.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.nestoleh.light.domain.model.ElectricityStatusBlock
import com.nestoleh.light.presentation.components.ToolbarIcon
import com.nestoleh.light.presentation.components.ToolbarTitle
import com.nestoleh.light.presentation.components.color
import com.nestoleh.light.presentation.components.fullDayName
import com.nestoleh.light.presentation.components.hourName
import com.nestoleh.light.presentation.components.shortDayName
import com.nestoleh.light.presentation.components.util.findFirstFullyVisibleItemIndex
import com.nestoleh.light.presentation.components.util.findLastFullyVisibleItemIndex
import com.nestoleh.light.presentation.components.util.lastVisiblePosition
import com.nestoleh.light.util.koinViewModel
import kotlinx.coroutines.launch
import light.composeapp.generated.resources.Res
import light.composeapp.generated.resources.ic_close
import org.jetbrains.compose.resources.painterResource
import org.koin.core.parameter.parametersOf
import kotlin.math.absoluteValue

@Composable
fun PlaceScheduleScreen(
    id: String,
    onBack: () -> Unit
) {
    val viewModel: PlaceScheduleViewModel = koinViewModel {
        parametersOf(id)
    }
    val state = viewModel.state.collectAsState()
    ScheduleScreenContent(
        state = state,
        onBack = onBack
    )
}

@Composable
private fun ScheduleScreenContent(
    state: State<PlaceScheduleUIState>,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    ToolbarTitle(
                        modifier = Modifier.padding(end = 48.dp),
                        title = state.value.place?.name ?: ""
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
    ) { paddings ->
        Box(
            modifier = Modifier
                .padding(paddings)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                val selectedTabState = remember { mutableStateOf(0) }
                val lazyListState: LazyListState = rememberLazyListState()
                val scope = rememberCoroutineScope()

                LaunchedEffect(Unit) {
                    snapshotFlow { lazyListState.layoutInfo }
                        .collect {
                            var itemPosition = lazyListState.itemFocusPosition()
                            if (itemPosition == -1) {
                                return@collect
                            }
                            if (lazyListState.findLastFullyVisibleItemIndex() == state.value.weekBlocksDayIndices.last()) {
                                itemPosition = state.value.weekBlocksDayIndices.last()
                            }
                            val lastVisiblePosition = lazyListState.lastVisiblePosition()
                            if (state.value.weekBlocksDayIndices.contains(itemPosition)
                                && itemPosition != state.value.weekBlocksDayIndices[selectedTabState.value]
                            ) {
                                selectedTabState.value = state.value.weekBlocksDayIndices.indexOf(itemPosition)
                            } else if (
                                lastVisiblePosition >= 0
                                && lastVisiblePosition < state.value.weekBlocksDayIndices[selectedTabState.value]
                                && selectedTabState.value > 0
                            ) {
                                selectedTabState.value = (selectedTabState.value - 1).coerceAtLeast(0)
                            }
                        }
                }

                DaysTabs(
                    selectedTabState = selectedTabState,
                    onTabSelected = {
                        selectedTabState.value = it
                        scope.launch {
                            state.value.weekBlocksDayIndices.getOrNull(selectedTabState.value)?.let { index ->
                                lazyListState.animateScrollToItem(index)
                            }
                        }
                    }
                )
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    state = lazyListState
                ) {
                    state.value.weekBlocks.forEachIndexed { dayIndex, daySchedule ->
                        item {
                            DayHeader(dayIndex)
                        }
                        items(daySchedule) { block ->
                            ElectricityStatus(block)
                        }
                    }
                }
            }
        }
    }
}

private fun LazyListState.itemFocusPosition(): Int {
    var itemPosition = this.findFirstFullyVisibleItemIndex()

    if (itemPosition == -1) {
        itemPosition = this.firstVisibleItemIndex
    }
    return itemPosition
}

@Composable
private fun DaysTabs(
    selectedTabState: State<Int>,
    onTabSelected: (Int) -> Unit
) {
    PrimaryTabRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        selectedTabIndex = selectedTabState.value,
        divider = {},
        indicator = {
            Box(
                modifier = Modifier
                    .tabIndicatorOffset(selectedTabState.value)
                    .zIndex(0f)
                    .padding(5.dp)
                    .fillMaxSize()
                    .clip(RoundedCornerShape(50.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
            )
        }
    ) {
        for (i in 0..6) {
            Tab(
                modifier = Modifier
                    .zIndex(1f)
                    .clip(RoundedCornerShape(50.dp)),
                selected = selectedTabState.value == i,
                text = {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 1,
                        overflow = TextOverflow.Visible,
                        text = i.shortDayName(),
                        style = MaterialTheme.typography.labelMedium,
                        textAlign = TextAlign.Center
                    )
                },
                onClick = { onTabSelected(i) },
            )
        }
    }
}

@Composable
private fun DayHeader(day: Int) {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 32.dp, bottom = 8.dp, start = 16.dp, end = 16.dp),
        text = day.fullDayName(),
        maxLines = 1,
        style = MaterialTheme.typography.titleLarge
    )
}

private const val oneHourBlockHeightDp = 60

@Composable
private fun ElectricityStatus(
    block: ElectricityStatusBlock
) {
    Box(
        modifier = Modifier
            .padding(start = 32.dp, end = 16.dp, top = 1.dp, bottom = 1.dp)
            .fillMaxWidth()
            .height(((block.hourEnd - block.hourStart).absoluteValue * oneHourBlockHeightDp).dp)
            .clip(RoundedCornerShape(16.dp))
            .background(block.status.color)
            .padding(vertical = 8.dp, horizontal = 16.dp),
        contentAlignment = Alignment.TopEnd
    ) {
        Text(
            text = "${block.hourStart.hourName()} - ${block.hourEnd.hourName()}",
            modifier = Modifier
                .clip(RoundedCornerShape(50.dp))
                .background(MaterialTheme.colorScheme.tertiaryContainer)
                .padding(vertical = 1.dp, horizontal = 8.dp),
            style = MaterialTheme.typography.bodySmall
        )
    }
}