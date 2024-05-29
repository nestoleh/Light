package com.nestoleh.light.domain.model

class Schedule(
    val weekSchedule: Array<Array<ElectricityStatus>> =
        Array(7) { Array(24) { ElectricityStatus.On } }
)

enum class ElectricityStatus {
    On,
    Off,
    PossibleOff
}