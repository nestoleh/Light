package com.nestoleh.light.util

import kotlinx.datetime.Instant
import kotlinx.datetime.toNSDate
import platform.Foundation.NSDateFormatter

actual fun Instant.formatDate(pattern: String, defValue: String): String {
    return try {
        val dateFormatter = NSDateFormatter()
        dateFormatter.dateFormat = pattern
        dateFormatter.stringFromDate(
            toNSDate()
        )
    } catch (e: Exception) {
        defValue
    }
}