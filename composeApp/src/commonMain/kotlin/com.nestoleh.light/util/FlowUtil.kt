package com.nestoleh.light.util

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration

fun watchFlow(tick: Duration): Flow<Instant> {
    return flow {
        while (true) {
            emit(Clock.System.now())
            delay(tick)
        }
    }
}