package com.nestoleh.light.util

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

expect fun Instant.formatDate(
    pattern: String,
    defValue: String = ""
): String

fun Long.formatDate(
    pattern: String,
    defValue: String = ""
): String {
    return Instant.fromEpochMilliseconds(this).formatDate(pattern, defValue)
}

fun Instant.isToday(): Boolean {
    return this.isSameDay(Clock.System.now())
}

fun Instant.isSameDay(other: Instant): Boolean {
    val dateLocal = this.toLocalDateTime(TimeZone.currentSystemDefault())
    val otherLocal = other.toLocalDateTime(TimeZone.currentSystemDefault())
    return dateLocal.year == otherLocal.year && dateLocal.dayOfYear == otherLocal.dayOfYear
}
