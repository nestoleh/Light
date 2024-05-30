package com.nestoleh.light.domain.model

class Schedule(
    val weekSchedule: Array<Array<ElectricityStatus>> =
        Array(7) { Array(24) { ElectricityStatus.On } }
)

data class ElectricityStatusBlock(
    val hourStart: Int, // include
    val hourEnd: Int, // exclude
    val status: ElectricityStatus
)

enum class ElectricityStatus {
    On,
    Off,
    PossibleOff
}