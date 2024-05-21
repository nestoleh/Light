package com.nestoleh.light.di

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.named
import org.koin.dsl.module

val dispatcherModule = module {
    single(DispatcherQualifier.Main.qualifier) { Dispatchers.Main }
    single(DispatcherQualifier.IO.qualifier) { getIODispatcher() }
    single(DispatcherQualifier.Default.qualifier) { Dispatchers.Default }
}

expect fun getIODispatcher(): CoroutineDispatcher

sealed class DispatcherQualifier(
    val qualifier: Qualifier
) {
    data object Main : DispatcherQualifier(named("Main"))
    data object IO : DispatcherQualifier(named("IO"))
    data object Default : DispatcherQualifier(named("Default"))
}