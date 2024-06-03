package com.nestoleh.light.presentation.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import co.touchlab.kermit.Logger
import com.nestoleh.light.domain.model.ElectricityStatusBlock
import com.nestoleh.light.presentation.components.ToolbarIcon
import com.nestoleh.light.presentation.components.ToolbarTitle
import com.nestoleh.light.presentation.components.color
import com.nestoleh.light.presentation.components.fullDayName
import com.nestoleh.light.presentation.components.hourName
import com.nestoleh.light.presentation.components.shortDayName
import com.nestoleh.light.presentation.components.util.animateScrollItemToCenter
import com.nestoleh.light.presentation.components.util.findFirstFullyVisibleItemIndex
import com.nestoleh.light.presentation.components.util.findLastFullyVisibleItemIndex
import com.nestoleh.light.presentation.components.util.scrollItemToCenter
import com.nestoleh.light.presentation.theme.DomainTheme
import com.nestoleh.light.util.koinViewModel
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import light.composeapp.generated.resources.Res
import light.composeapp.generated.resources.ic_close
import light.composeapp.generated.resources.ic_today
import light.composeapp.generated.resources.schedule_footer
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
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

private enum class ScrollToNowType {
    None,
    ImmediateScroll,
    AnimatedScroll
}

@Composable
private fun ScheduleScreenContent(
    state: State<PlaceScheduleUIState>,
    onBack: () -> Unit
) {
    val scrollToNowState = remember { mutableStateOf(ScrollToNowType.ImmediateScroll) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    ToolbarTitle(
                        title = state.value.place?.name ?: ""
                    )
                },
                navigationIcon = {
                    ToolbarIcon(
                        painter = painterResource(Res.drawable.ic_close),
                        onClick = onBack,
                        contentDescription = "Back button"
                    )
                },
                actions = {
                    ToolbarIcon(
                        painter = painterResource(Res.drawable.ic_today),
                        onClick = {
                            scrollToNowState.value = ScrollToNowType.AnimatedScroll
                        },
                        contentDescription = "Today button"
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
                val selectedTabState = remember {
                    mutableStateOf(
                        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).dayOfWeek.ordinal
                    )
                }
                val lazyListState: LazyListState = rememberLazyListState()
                val scope = rememberCoroutineScope()

                LaunchedEffect(Unit) {
                    snapshotFlow { lazyListState.layoutInfo }
                        .collect {
                            if (lazyListState.layoutInfo.totalItemsCount <= 0) return@collect
                            var itemPosition = lazyListState.itemFocusPosition()
                            if (itemPosition == -1) return@collect
                            if (lazyListState.findLastFullyVisibleItemIndex() >= state.value.weekBlocksDayIndices.last()) {
                                itemPosition = state.value.weekBlocksDayIndices.last()
                            }
                            val shouldBeSelectedIndex = state.value.weekBlocksDayIndices
                                .indexOfFirst { it > itemPosition }
                                .let { if (it >= 0) it - 1 else state.value.weekBlocksDayIndices.size - 1 }
                                .coerceAtLeast(0)
                            if (shouldBeSelectedIndex != selectedTabState.value) {
                                Logger.d { "Selected tab = $shouldBeSelectedIndex" }
                                selectedTabState.value = shouldBeSelectedIndex
                            }
                        }
                }

                DaysTabs(
                    selectedTabState = selectedTabState,
                    onTabSelected = { newSelectedTab ->
                        scope.launch {
                            state.value.weekBlocksDayIndices.getOrNull(newSelectedTab)?.let { index ->
                                lazyListState.animateScrollToItem(index)
                            }
                        }
                    }
                )
                val density = LocalDensity.current
                LaunchedEffect(state.value.place, scrollToNowState.value) {
                    val scrollState = scrollToNowState.value
                    if (scrollState != ScrollToNowType.None && state.value.place != null) {
                        scope.launch {
                            val (index, block) = state.value.countCurrentBlockIndex()
                            if (index >= 0) {
                                val shift = block?.let {
                                    with(density) {
                                        ((block.hourEnd - state.value.currentTime.timeAsFloat) * oneHourBlockHeightDp)
                                            .dp.roundToPx()
                                    }
                                }
                                Logger.d { "Scrolling to $index with shift $shift" }
                                if (scrollState == ScrollToNowType.AnimatedScroll) {
                                    lazyListState.animateScrollItemToCenter(
                                        index = index,
                                        additionalShift = shift ?: 0
                                    )
                                } else {
                                    lazyListState.scrollItemToCenter(
                                        index = index,
                                        additionalShift = shift ?: 0
                                    )
                                }
                            }
                        }
                        scrollToNowState.value = ScrollToNowType.None
                    }
                }
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = lazyListState,
                ) {
                    state.value.weekBlocks.forEachIndexed { dayIndex, daySchedule ->
                        item {
                            DayHeader(dayIndex)
                        }
                        items(daySchedule) { block ->
                            val currentTime = state.value.currentTime
                            if (currentTime.dayNumber == dayIndex
                                && currentTime.hours >= block.hourStart
                                && currentTime.hours < block.hourEnd
                            ) {
                                CurrentElectricityStatus(
                                    block = block,
                                    hour = currentTime.hours,
                                    minute = currentTime.minutes
                                )
                            } else {
                                ElectricityStatus(block)
                            }
                        }
                    }

                    item {
                        // footer element, required because of bug in scrollToItem function
                        Box(
                            modifier = Modifier
                                .padding(start = 64.dp)
                                .fillMaxWidth()
                                .height(54.dp),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            Text(
                                modifier = Modifier
                                    .padding(bottom = 8.dp)
                                    .alpha(0.5f),
                                text = stringResource(Res.string.schedule_footer),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
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
private fun CurrentElectricityStatus(
    block: ElectricityStatusBlock,
    hour: Int,
    minute: Int,
) {
    val textMeasurer = rememberTextMeasurer()
    val scheduleCurrentTimeColor = DomainTheme.colorScheme.scheduleCurrentTime
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        ElectricityStatus(block)
        Spacer(
            modifier = Modifier
                .zIndex(1f)
                .fillMaxWidth()
                .fillMaxHeight()
                .drawWithContent {
                    val yShift: Float = (hour - block.hourStart + minute / 60f) * oneHourBlockHeightDp.dp.toPx()
                    drawText(
                        textMeasurer = textMeasurer,
                        text = "${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}",
                        style = TextStyle(
                            fontSize = 12.sp,
                            color = scheduleCurrentTimeColor
                        ),
                        topLeft = Offset(
                            x = 8.dp.toPx(),
                            y = yShift - 16.sp.toPx()
                        )
                    )
                    drawRoundRect(
                        color = scheduleCurrentTimeColor,
                        topLeft = Offset(0f, yShift),
                        size = Size(size.width, 1.dp.toPx()),
                        cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
                    )
                }
        )
    }
}

@Composable
private fun ElectricityStatus(
    block: ElectricityStatusBlock,
) {
    Box(
        modifier = Modifier
            .padding(start = 64.dp, end = 16.dp, top = 1.dp, bottom = 1.dp)
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
                .zIndex(2f)
                .clip(RoundedCornerShape(50.dp))
                .background(MaterialTheme.colorScheme.tertiaryContainer)
                .padding(vertical = 1.dp, horizontal = 8.dp),
            style = MaterialTheme.typography.bodySmall
        )
    }
}