package com.nestoleh.light.util

import light.composeapp.generated.resources.Res
import light.composeapp.generated.resources.human_readable_days
import light.composeapp.generated.resources.human_readable_hours
import light.composeapp.generated.resources.human_readable_minutes
import light.composeapp.generated.resources.human_readable_seconds
import org.jetbrains.compose.resources.getString
import kotlin.time.Duration


suspend fun Duration.toHumanReadable(
    isShowSeconds: Boolean = true,
): String {
    val prefix = if (isNegative()) "-" else ""
    return prefix + when {
        inWholeSeconds == 0L -> getString(Res.string.human_readable_minutes, 0)
        isInfinite() -> "Infinity"
        else -> toComponents { days, hours, minutes, seconds, _ ->
            val components = ArrayList<DurationComponent>(4).apply {
                add(DurationComponent(getString(Res.string.human_readable_days, days), isEmpty = days == 0L))
                add(DurationComponent(getString(Res.string.human_readable_hours, hours), isEmpty = hours == 0))
                add(DurationComponent(getString(Res.string.human_readable_minutes, minutes), isEmpty = minutes == 0))
                if (isShowSeconds) {
                    add(
                        DurationComponent(
                            readableValue = getString(Res.string.human_readable_seconds, seconds),
                            isEmpty = seconds == 0
                        )
                    )
                }
            }
            val start = components.indexOfFirst { !it.isEmpty }
            val end = components.indexOfLast { !it.isEmpty }
            if (start < 0 || end < 0) {
                ""
            } else {
                components.subList(start, end + 1).joinToString(separator = " ") { it.readableValue }
            }
        }
    }
}

private data class DurationComponent(
    val readableValue: String,
    val isEmpty: Boolean,
)