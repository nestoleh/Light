package com.nestoleh.light.di

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

actual fun getIODispatcher(): CoroutineDispatcher {
    return Dispatchers.IO
}