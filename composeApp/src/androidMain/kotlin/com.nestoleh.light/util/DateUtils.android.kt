package com.nestoleh.light.util

import kotlinx.datetime.Instant
import java.text.SimpleDateFormat
import java.util.Date

actual fun Instant.formatDate(pattern: String, defValue: String): String {
    return try {
        SimpleDateFormat(pattern).format(Date(this.toEpochMilliseconds()))
    } catch (e: Exception) {
        defValue
    }
}