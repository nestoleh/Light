package com.nestoleh.light.domain.repository

import kotlinx.coroutines.flow.Flow

interface ParametersRepository {
    suspend fun putString(key: String, value: String)
    suspend fun getString(key: String): String?
    fun getStringAsFlow(key: String): Flow<String?>

    suspend fun putInt(key: String, value: Int)
    suspend fun getInt(key: String): Int?
    fun getIntAsFlow(key: String): Flow<Int?>

    suspend fun deleteValue(key: String)
}