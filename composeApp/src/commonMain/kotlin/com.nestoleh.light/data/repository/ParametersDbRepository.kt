package com.nestoleh.light.data.repository

import com.nestoleh.light.data.database.dao.ParametersDao
import com.nestoleh.light.data.database.entity.ParameterEntity
import com.nestoleh.light.domain.repository.ParametersRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ParametersDbRepository(
    private val dao: ParametersDao
) : ParametersRepository {
    override suspend fun putString(key: String, value: String) {
        dao.upsertParameter(
            ParameterEntity(
                key = key,
                value = value
            )
        )
    }

    override suspend fun getString(key: String): String? {
        return dao.getParameterValue(key)?.value
    }

    override fun getStringAsFlow(key: String): Flow<String?> {
        return dao.getParameterAsFlow(key).map { it?.value }
    }

    override suspend fun putInt(key: String, value: Int) {
        dao.upsertParameter(
            ParameterEntity(
                key = key,
                value = value.toString()
            )
        )
    }

    override suspend fun getInt(key: String): Int? {
        return dao.getParameterValue(key)?.value?.toIntOrError()
    }

    override fun getIntAsFlow(key: String): Flow<Int?> {
        return dao.getParameterAsFlow(key).map { it?.value?.toIntOrError() }
    }

    private fun String?.toIntOrError(): Int? {
        return try {
            this?.toInt()
        } catch (e: NumberFormatException) {
            throw IllegalArgumentException("Value is not an Integer")
        }
    }

    override suspend fun deleteValue(key: String) {
        dao.deleteParameter(key)
    }
}