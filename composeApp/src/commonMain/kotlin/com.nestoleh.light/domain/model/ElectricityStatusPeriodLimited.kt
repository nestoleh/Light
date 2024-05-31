package com.nestoleh.light.domain.model

import kotlinx.datetime.LocalDateTime

data class NearestElectricityPeriods(
    val current: ElectricityStatusPeriod,
    val future: List<ElectricityStatusPeriod.Limited>
)

sealed class ElectricityStatusPeriod(
    open val status: ElectricityStatus
) {

    data class Limited(
        override val status: ElectricityStatus,
        val periodStart: LocalDateTime,
        val periodEnd: LocalDateTime
    ) : ElectricityStatusPeriod(status)

    data class Unlimited(
        override val status: ElectricityStatus,
    ) : ElectricityStatusPeriod(status)
}
