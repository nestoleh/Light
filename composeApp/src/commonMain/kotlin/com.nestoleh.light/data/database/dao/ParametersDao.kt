package com.nestoleh.light.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.nestoleh.light.data.database.entity.ParameterEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ParametersDao {
    @Upsert
    suspend fun upsertParameter(parameter: ParameterEntity)

    @Query("SELECT * FROM ParameterEntity WHERE \"key\" = :key")
    fun getParameterAsFlow(key: String): Flow<ParameterEntity?>

    @Query("SELECT * FROM ParameterEntity WHERE \"key\" = :key")
    suspend fun getParameterValue(key: String): ParameterEntity?

    @Query("DELETE FROM ParameterEntity WHERE \"key\" = :key")
    suspend fun deleteParameter(key: String)
}