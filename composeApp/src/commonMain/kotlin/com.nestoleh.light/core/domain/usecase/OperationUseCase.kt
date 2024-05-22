package com.nestoleh.light.core.domain.usecase

import com.nestoleh.light.core.domain.model.OperationError
import com.nestoleh.light.core.domain.model.OperationStarted
import com.nestoleh.light.core.domain.model.OperationStatus
import com.nestoleh.light.core.domain.model.OperationSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

abstract class OperationUseCase<in P> {
    operator fun invoke(
        params: P,
    ): Flow<OperationStatus> = flow {
        emit(OperationStarted)
        runOperation(params)
        emit(OperationSuccess)
    }.catch { t -> emit(OperationError(t)) }

    suspend fun executeSync(params: P) = runOperation(params)

    protected abstract suspend fun runOperation(params: P)
}

operator fun OperationUseCase<Any?>.invoke(): Flow<OperationStatus> {
    return this.invoke(null)
}

suspend fun OperationUseCase<Any?>.executeSync() {
    return this.executeSync(null)
}

suspend fun Flow<OperationStatus>.runOperation(
    onStarted: suspend () -> Unit = {},
    onSuccess: suspend () -> Unit = {},
    onError: suspend (throwable: Throwable) -> Unit = {},
) {
    this.collect {
        when (it) {
            is OperationStarted -> onStarted()
            is OperationSuccess -> onSuccess()
            is OperationError -> onError(it.throwable)
        }
    }
}