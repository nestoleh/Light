package com.nestoleh.light.di

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.newFixedThreadPoolContext

actual fun getIODispatcher(): CoroutineDispatcher {
    return newFixedThreadPoolContext(nThreads = 16, name = "IO")
}