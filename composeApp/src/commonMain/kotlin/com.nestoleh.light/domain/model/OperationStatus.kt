package com.nestoleh.light.domain.model

sealed class OperationStatus

data object OperationStarted : OperationStatus()

data object OperationSuccess : OperationStatus()

data class OperationError(
    val throwable: Throwable,
) : OperationStatus()