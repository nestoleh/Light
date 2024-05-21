package com.nestoleh.light.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.nestoleh.light.data.database.entity.PlaceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaceDao {

    @Query("SELECT * FROM Place")
    fun getAllPlacesFlow(): Flow<List<PlaceEntity>>

    @Query("SELECT * FROM Place WHERE name = :name")
    suspend fun getAllPlacesWithName(name: String): List<PlaceEntity>

    @Upsert
    suspend fun upsert(place: PlaceEntity)

    @Query("DELETE FROM Place WHERE id = :placeId")
    suspend fun delete(placeId: Int)
}