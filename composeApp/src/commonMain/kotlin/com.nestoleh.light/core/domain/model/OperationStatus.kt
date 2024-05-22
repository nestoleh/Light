package com.nestoleh.light.core.domain.model

sealed class OperationStatus(val isTerminate: Boolean)

data object OperationStarted : OperationStatus(
    isTerminate = false,
)

data object OperationSuccess : OperationStatus(
    isTerminate = true,
)

data class OperationError(
    val throwable: Throwable,
) : OperationStatus(
    isTerminate = true,
)