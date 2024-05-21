package com.nestoleh.light.domain.usecase

import kotlinx.coroutines.flow.Flow

abstract class FlowUseCase<in P, R> {

    operator fun invoke(params: P): Flow<R> {
        return doWork(params)
    }

    protected abstract fun doWork(params: P): Flow<R>
}


operator fun <R> FlowUseCase<Unit, R>.invoke(): Flow<R> {
    return this.invoke(Unit)
}