package com.nestoleh.light.util

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

fun watchFlow(tick: Duration): Flow<Instant> {
    return flow {
        while (true) {
            emit(Clock.System.now())
            delay(tick)
        }
    }
}

fun everyMinuteChangeFlow(): Flow<Instant> {
    return watchFlow(1.seconds)
        .distinctUntilChangedBy { it.toLocalDateTime(TimeZone.currentSystemDefault()).minute }
}